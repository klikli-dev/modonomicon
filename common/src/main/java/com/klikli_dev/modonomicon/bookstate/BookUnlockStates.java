/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookEntryUnlockedCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionCategoryContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BookUnlockStates {
    public static final Codec<BookUnlockStates> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.concurrentMap(ResourceLocation.CODEC, Codecs.set(ResourceLocation.CODEC)).fieldOf("readEntries").forGetter((s) -> s.readEntries),
            Codecs.concurrentMap(ResourceLocation.CODEC, Codecs.set(ResourceLocation.CODEC)).fieldOf("unlockedEntries").forGetter((s) -> s.unlockedEntries),
            Codecs.concurrentMap(ResourceLocation.CODEC, Codecs.set(ResourceLocation.CODEC)).fieldOf("unlockedCategories").forGetter((s) -> s.unlockedCategories),
            Codecs.concurrentMap(ResourceLocation.CODEC, Codecs.mutableMap(ResourceLocation.CODEC, Codec.INT)).fieldOf("usedCommands").forGetter((s) -> s.usedCommands)
    ).apply(instance, BookUnlockStates::new));


    /**
     * Map Book ID to read entry IDs
     */
    public ConcurrentMap<ResourceLocation, Set<ResourceLocation>> readEntries;

    /**
     * Map Book ID to unlocked entry IDs
     */
    public ConcurrentMap<ResourceLocation, Set<ResourceLocation>> unlockedEntries;

    /**
     * Map Book ID to unlocked categories IDs
     */
    public ConcurrentMap<ResourceLocation, Set<ResourceLocation>> unlockedCategories;

    /**
     * Map Book ID to commands used. This is never wiped to avoid reusing reward commands.
     */
    public ConcurrentMap<ResourceLocation, Map<ResourceLocation, Integer>> usedCommands;

    public BookUnlockStates() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    public BookUnlockStates(ConcurrentMap<ResourceLocation, Set<ResourceLocation>> readEntries,
                            ConcurrentMap<ResourceLocation, Set<ResourceLocation>> unlockedEntries,
                            ConcurrentMap<ResourceLocation, Set<ResourceLocation>> unlockedCategories,
                            ConcurrentMap<ResourceLocation, Map<ResourceLocation, Integer>> usedCommands) {
        this.readEntries = readEntries;
        this.unlockedEntries = unlockedEntries;
        this.unlockedCategories = unlockedCategories;
        this.usedCommands = usedCommands;
    }

    public void update(ServerPlayer owner) {
        //loop through available books and update unlocked pages and categories

        //store conditions that are not unlocked when first checked, but may be unlocked due to changes later in the first pass.
        List<Map.Entry<BookCondition, BookConditionContext>> conditionsThatRequireMultipass = new ArrayList<>();

        for (var book : BookDataManager.get().getBooks().values()) {
            BookErrorManager.get().reset();
            BookErrorManager.get().setCurrentBookId(book.getId());
            for (var category : book.getCategories().values()) {
                BookErrorManager.get().setContext("Category to perform condition test on: {}",
                        category.getId().toString()
                );
                try {
                    var categoryContext = BookConditionContext.of(book, category);
                    if (category.getCondition().test(categoryContext, owner))
                        this.unlockedCategories.computeIfAbsent(book.getId(), k -> new HashSet<>()).add(category.getId());
                    else if (category.getCondition().requiresMultiPassUnlockTest())
                        //if the condition is not met AND it requires a multi pass unlock test we store it to test again later
                        //this is because if the condition depends on an unlock that may happen later in the first pass it should unlock this condition alongside
                        conditionsThatRequireMultipass.add(Map.entry(category.getCondition(), categoryContext));
                } catch (Exception e) {
                    BookErrorManager.get().error("Error while testing category condition", e);
                }


                for (var entry : category.getEntries().values()) {
                    BookErrorManager.get().setContext("Entry to perform condition test on: {}",
                            entry.getId().toString()
                    );

                    try {
                        var entryContext = BookConditionContext.of(book, entry);
                        if (entry.getCondition().test(entryContext, owner))
                            this.unlockedEntries.computeIfAbsent(book.getId(), k -> new HashSet<>()).add(entry.getId());
                        else if (entry.getCondition().requiresMultiPassUnlockTest())
                            //if the condition is not met AND it requires a multi pass unlock test we store it to test again later
                            //this is because if the condition depends on an unlock that may happen later in the first pass it should unlock this condition alongside
                            conditionsThatRequireMultipass.add(Map.entry(entry.getCondition(), entryContext));
                    } catch (Exception e) {
                        BookErrorManager.get().error("Error while testing entry condition", e);
                    }
                }
            }
        }

        BookErrorManager.get().reset();

        boolean unlockedAny = false;
        do {
            unlockedAny = false;
            var iter = conditionsThatRequireMultipass.iterator();
            while (iter.hasNext()) {
                var condition = iter.next();
                BookErrorManager.get().setCurrentBookId(condition.getValue().getBook().getId());
                BookErrorManager.get().setContext("Context to perform unlockedConditions test on: {}",
                        condition.getValue().toString()
                );

                //check if condition is now unlocked
                if (condition.getKey().test(condition.getValue(), owner)) {
                    try {
                        //then store the unlock result
                        if (condition.getValue() instanceof BookConditionEntryContext entryContext) {
                            this.unlockedEntries.computeIfAbsent(entryContext.getBook().getId(), k -> new HashSet<>()).add(entryContext.getEntry().getId());
                        } else if (condition.getValue() instanceof BookConditionCategoryContext categoryContext) {
                            this.unlockedCategories.computeIfAbsent(categoryContext.getBook().getId(), k -> new HashSet<>()).add(categoryContext.getCategory().getId());
                        }

                        //make sure to iterate again now -> could unlock further conditions depending on this unlock
                        unlockedAny = true;

                        //remove the condition from the list, so it is not checked again
                        iter.remove();

                    } catch (Exception e) {
                        BookErrorManager.get().error("Error while testing entry condition", e);
                    }
                }
            }

            //now repeat until we no longer unlock anything
        } while (unlockedAny);

        BookErrorManager.get().reset();
    }


    /**
     * @return true if entry is now read, false if it was already read before.
     */
    public boolean read(BookEntry entry, ServerPlayer player) {
        if (this.isRead(entry))
            return false;

        this.readEntries.computeIfAbsent(entry.getBook().getId(), k -> new HashSet<>()).add(entry.getId());

        var command = entry.getCommandToRunOnFirstRead();
        if (command != null) {
            command.execute(player);
        }

        return true;
    }

    public void setRun(BookCommand command) {
        if (command.getBook() == null)
            return;

        var uses = this.usedCommands.getOrDefault(command.getBook().getId(), new HashMap<>()).getOrDefault(command.getId(), 0);
        this.usedCommands.computeIfAbsent(command.getBook().getId(), k -> new HashMap<>()).put(command.getId(), uses + 1);
    }

    public boolean canRun(BookCommand command) {
        if (command.getBook() == null)
            return false;

        if (command.getMaxUses() == -1) //unlimited uses
            return true;

        return this.usedCommands.getOrDefault(command.getBook().getId(), new HashMap<>()).getOrDefault(command.getId(), 0) < command.getMaxUses();
    }

    public boolean isRead(BookEntry entry) {
        if (entry.getBook() == null)
            return false;
        return this.readEntries.getOrDefault(entry.getBook().getId(), new HashSet<>()).contains(entry.getId());
    }

    public boolean isUnlocked(BookEntry entry) {
        if (entry.getBook() == null)
            return false;
        return this.unlockedEntries.getOrDefault(entry.getBook().getId(), new HashSet<>()).contains(entry.getId());
    }

    public boolean isUnlocked(BookCategory category) {
        if (category.getBook() == null)
            return false;
        return this.unlockedCategories.getOrDefault(category.getBook().getId(), new HashSet<>()).contains(category.getId());
    }

    public void reset(Book book) {
        this.readEntries.remove(book.getId());
        this.unlockedEntries.remove(book.getId());
        this.unlockedCategories.remove(book.getId());
        //Do not reset the commands!
    }

    public List<ResourceLocation> getBooks() {
        var books = new HashSet<ResourceLocation>();
        books.addAll(this.readEntries.keySet());
        books.addAll(this.unlockedEntries.keySet());
        books.addAll(this.unlockedCategories.keySet());
        return books.stream().toList();
    }

    public String getUnlockCode(Book book) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(book.getId());

        var unlockedCategories = this.unlockedCategories.getOrDefault(book.getId(), Set.of());
        buf.writeVarInt(unlockedCategories.size());
        unlockedCategories.forEach(buf::writeResourceLocation);

        var unlockedEntries = this.unlockedEntries.getOrDefault(book.getId(), Set.of());
        buf.writeVarInt(unlockedEntries.size());
        unlockedEntries.forEach(buf::writeResourceLocation);

        var readEntries = this.readEntries.getOrDefault(book.getId(), Set.of());
        buf.writeVarInt(readEntries.size());
        readEntries.forEach(buf::writeResourceLocation);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        return Base64.getEncoder().encodeToString(bytes);
    }

    public Book applyUnlockCode(String code) {
        try {
            var decoded = Base64.getDecoder().decode(code);
            var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(decoded));
            var bookId = buf.readResourceLocation();

            var book = BookDataManager.get().getBook(bookId);
            if (book == null)
                return null;

            var unlockedCategories = new HashSet<ResourceLocation>();
            var unlockedEntries = new HashSet<ResourceLocation>();
            var readEntries = new HashSet<ResourceLocation>();
            var unlockedCategoriesSize = buf.readVarInt();
            for (var i = 0; i < unlockedCategoriesSize; i++) {
                unlockedCategories.add(buf.readResourceLocation());
            }

            var unlockedEntriesSize = buf.readVarInt();
            for (var i = 0; i < unlockedEntriesSize; i++) {
                unlockedEntries.add(buf.readResourceLocation());
            }

            var readEntriesSize = buf.readVarInt();
            for (var i = 0; i < readEntriesSize; i++) {
                readEntries.add(buf.readResourceLocation());
            }

            this.unlockedCategories.put(bookId, unlockedCategories);
            this.unlockedEntries.put(bookId, unlockedEntries);
            this.readEntries.put(bookId, readEntries);

            return book;
        } catch (Exception e) {
            return null;
        }
    }
}
