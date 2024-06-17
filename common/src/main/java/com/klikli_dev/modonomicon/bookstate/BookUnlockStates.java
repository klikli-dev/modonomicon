/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionCategoryContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionPageContext;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class BookUnlockStates {
    public static final Codec<BookUnlockStates> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, Codecs.set(ResourceLocation.CODEC)).fieldOf("readEntries").forGetter((s) -> s.readEntries),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.unboundedMap(ResourceLocation.CODEC, Codecs.set(Codec.INT))).fieldOf("unlockedPages").forGetter((s) -> s.unlockedPages),
            Codec.unboundedMap(ResourceLocation.CODEC, Codecs.set(ResourceLocation.CODEC)).fieldOf("unlockedEntries").forGetter((s) -> s.unlockedEntries),
            Codec.unboundedMap(ResourceLocation.CODEC, Codecs.set(ResourceLocation.CODEC)).fieldOf("unlockedCategories").forGetter((s) -> s.unlockedCategories),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)).fieldOf("usedCommands").forGetter((s) -> s.usedCommands)
    ).apply(instance, BookUnlockStates::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookUnlockStates> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);


    /**
     * Map Book ID to read entry IDs
     */
    public Map<ResourceLocation, Set<ResourceLocation>> readEntries;

    /**
     * Map Book ID to entry IDs to lists of unlocked pages
     */
    public Map<ResourceLocation, Map<ResourceLocation, Set<Integer>>> unlockedPages;

    /**
     * Map Book ID to unlocked entry IDs
     */
    public Map<ResourceLocation, Set<ResourceLocation>> unlockedEntries;

    /**
     * Map Book ID to unlocked categories IDs
     */
    public Map<ResourceLocation, Set<ResourceLocation>> unlockedCategories;

    /**
     * Map Book ID to commands used. This is never wiped to avoid reusing reward commands.
     */
    public Map<ResourceLocation, Map<ResourceLocation, Integer>> usedCommands;

    public BookUnlockStates() {
        this(Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap());
    }

    public BookUnlockStates(Map<ResourceLocation, Set<ResourceLocation>> readEntries,
                            Map<ResourceLocation, Map<ResourceLocation, Set<Integer>>> unlockedPages,
                            Map<ResourceLocation, Set<ResourceLocation>> unlockedEntries,
                            Map<ResourceLocation, Set<ResourceLocation>> unlockedCategories,
                            Map<ResourceLocation, Map<ResourceLocation, Integer>> usedCommands) {
        this.readEntries = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(readEntries));

        this.unlockedPages = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        unlockedPages.forEach((bookId, entryPagesMap) -> {
            var innerMap = this.unlockedPages.computeIfAbsent(bookId, k -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
            entryPagesMap.forEach((entryId, pages) -> innerMap.put(entryId, new ObjectOpenHashSet<>(pages)));
        });

        this.unlockedEntries = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        unlockedEntries.forEach((bookId, entries) -> this.unlockedEntries.put(bookId, new ObjectOpenHashSet<>(entries)));

        this.unlockedCategories = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        unlockedCategories.forEach((bookId, categories) -> this.unlockedCategories.put(bookId, new ObjectOpenHashSet<>(categories)));

        this.usedCommands = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        usedCommands.forEach((bookId, commandUsesMap) -> {
            var innerMap = this.usedCommands.computeIfAbsent(bookId, k -> Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>()));
            innerMap.putAll(commandUsesMap);
        });
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
                        this.unlockedCategories.computeIfAbsent(book.getId(), k -> new ObjectOpenHashSet<>()).add(category.getId());
                    else if (category.getCondition().requiresMultiPassUnlockTest())
                        //if the condition is not met AND it requires a multi pass unlock test we store it to test again later
                        //this is because if the condition depends on an unlock that may happen later in the first pass it should unlock this condition alongside
                        conditionsThatRequireMultipass.add(Map.entry(category.getCondition(), categoryContext));
                } catch (Exception e) {
                    BookErrorManager.get().error("Error while testing category condition", e);
                }


                for (var entry : category.getEntries().values()) {
                    BookErrorManager.get().setContext("Entry to perform condition test on: {}", entry.getId().toString()
                    );

                    try {
                        var entryContext = BookConditionContext.of(book, entry);
                        if (entry.getCondition().test(entryContext, owner))
                            this.unlockedEntries.computeIfAbsent(book.getId(), k -> new ObjectOpenHashSet<>()).add(entry.getId());
                        else if (entry.getCondition().requiresMultiPassUnlockTest())
                            //if the condition is not met AND it requires a multi pass unlock test we store it to test again later
                            //this is because if the condition depends on an unlock that may happen later in the first pass it should unlock this condition alongside
                            conditionsThatRequireMultipass.add(Map.entry(entry.getCondition(), entryContext));
                    } catch (Exception e) {
                        BookErrorManager.get().error("Error while testing entry condition", e);
                    }

                    for (var page : entry.getPages()) {
                        BookErrorManager.get().setContext("Page to perform condition test on: {}",
                                page.getPageNumber()
                        );

                        try {
                            var pageContext = BookConditionContext.of(book, page);
                            var pageCondition = page.getCondition();
                            if (pageCondition.test(pageContext, owner)) {
                                var pages = this.unlockedPages.computeIfAbsent(book.getId(), k -> new Object2ObjectOpenHashMap<>())
                                        .computeIfAbsent(entry.getId(), k -> new ObjectOpenHashSet<>());
                                if (!pages.contains(page.getPageNumber())) {
                                    pages.add(page.getPageNumber());
                                    this.readEntries.computeIfAbsent(book.getId(), k -> new ObjectOpenHashSet<>()).remove(entry.getId());
                                }
                            } else if (pageCondition.requiresMultiPassUnlockTest()) {
                                //if the condition is not met AND it requires a multi pass unlock test we store it to test again later
                                //this is because if the condition depends on an unlock that may happen later in the first pass it should unlock this condition alongside
                                conditionsThatRequireMultipass.add(Map.entry(pageCondition, pageContext));
                            }
                        } catch (Exception e) {
                            BookErrorManager.get().error("Error while testing page condition", e);
                        }
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
                        if (condition.getValue() instanceof BookConditionPageContext pageContext) {
                            var pages = this.unlockedPages.computeIfAbsent(pageContext.getBook().getId(), k -> new Object2ObjectOpenHashMap<>())
                                    .computeIfAbsent(pageContext.getEntry().getId(), k -> new ObjectOpenHashSet<>());
                            if (!pages.contains(pageContext.getPage().getPageNumber())) {
                                pages.add(pageContext.getPage().getPageNumber());
                                this.readEntries.computeIfAbsent(pageContext.getBook().getId(), k -> new ObjectOpenHashSet<>()).remove(pageContext.getEntry().getId());
                            }
                        } else if (condition.getValue() instanceof BookConditionEntryContext entryContext) {
                            this.unlockedEntries.computeIfAbsent(entryContext.getBook().getId(), k -> new ObjectOpenHashSet<>()).add(entryContext.getEntry().getId());
                        } else if (condition.getValue() instanceof BookConditionCategoryContext categoryContext) {
                            this.unlockedCategories.computeIfAbsent(categoryContext.getBook().getId(), k -> new ObjectOpenHashSet<>()).add(categoryContext.getCategory().getId());
                        }

                        //make sure to iterate again now -> could unlock further conditions depending on this unlock
                        unlockedAny = true;

                        //remove the condition from the list, so it is not checked again
                        iter.remove();

                    } catch (Exception e) {
                        BookErrorManager.get().error("Error while testing condition", e);
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

        this.readEntries.computeIfAbsent(entry.getBook().getId(), k -> new ObjectOpenHashSet<>()).add(entry.getId());

        var command = entry.getCommandToRunOnFirstRead();
        if (command != null) {
            command.execute(player);
        }

        return true;
    }

    public void setRun(BookCommand command) {
        if (command.getBook() == null)
            return;

        var uses = this.usedCommands.getOrDefault(command.getBook().getId(), new Object2ObjectOpenHashMap<>()).getOrDefault(command.getId(), 0);
        this.usedCommands.computeIfAbsent(command.getBook().getId(), k -> new Object2ObjectOpenHashMap<>()).put(command.getId(), uses + 1);
    }

    public boolean canRun(BookCommand command) {
        if (command.getBook() == null)
            return false;

        if (command.getMaxUses() == -1) //unlimited uses
            return true;

        return this.usedCommands.getOrDefault(command.getBook().getId(), new Object2ObjectOpenHashMap<>()).getOrDefault(command.getId(), 0) < command.getMaxUses();
    }

    public boolean isRead(BookEntry entry) {
        if (entry.getBook() == null)
            return false;
        return this.readEntries.getOrDefault(entry.getBook().getId(), new ObjectOpenHashSet<>()).contains(entry.getId());
    }

    public List<BookPage> getUnlockedPagesIn(BookEntry entry) {
        var unlockedPageNumbers = this.unlockedPages.getOrDefault(entry.getBook().getId(), new Object2ObjectOpenHashMap<>())
                .getOrDefault(entry.getId(), new ObjectOpenHashSet<>());
        return entry.getPages().stream().filter(page -> unlockedPageNumbers.contains(page.getPageNumber())).toList();
    }

    public boolean isUnlocked(BookPage page) {
        if (page.getBook() == null)
            return false;
        return this.unlockedPages.getOrDefault(page.getBook().getId(), new Object2ObjectOpenHashMap<>())
                .getOrDefault(page.getParentEntry().getId(), new ObjectOpenHashSet<>())
                .contains(page.getPageNumber());
    }

    public boolean isUnlocked(BookEntry entry) {
        if (entry.getBook() == null)
            return false;
        return this.unlockedEntries.getOrDefault(entry.getBook().getId(), new ObjectOpenHashSet<>()).contains(entry.getId());
    }

    public boolean isUnlocked(BookCategory category) {
        if (category.getBook() == null)
            return false;
        return this.unlockedCategories.getOrDefault(category.getBook().getId(), new ObjectOpenHashSet<>()).contains(category.getId());
    }

    public void reset(Book book) {
        this.readEntries.remove(book.getId());
        this.unlockedPages.remove(book.getId());
        this.unlockedEntries.remove(book.getId());
        this.unlockedCategories.remove(book.getId());
        //Do not reset the commands!
    }

    public List<ResourceLocation> getBooks() {
        var books = new ObjectOpenHashSet<ResourceLocation>();
        books.addAll(this.readEntries.keySet());
        books.addAll(this.unlockedPages.keySet());
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

        var unlockedPages = this.unlockedPages.getOrDefault(book.getId(), Map.of());
        buf.writeVarInt(unlockedPages.size());
        unlockedPages.forEach((entry, pages) -> {
            buf.writeResourceLocation(entry);
            buf.writeVarInt(pages.size());
            pages.forEach(buf::writeVarInt);
        });

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

            var unlockedCategories = new ObjectOpenHashSet<ResourceLocation>();
            var unlockedEntries = new ObjectOpenHashSet<ResourceLocation>();
            var unlockedPages = new Object2ObjectOpenHashMap<ResourceLocation, Set<Integer>>();
            var readEntries = new ObjectOpenHashSet<ResourceLocation>();

            var unlockedCategoriesSize = buf.readVarInt();
            for (var i = 0; i < unlockedCategoriesSize; i++) {
                unlockedCategories.add(buf.readResourceLocation());
            }

            var unlockedEntriesSize = buf.readVarInt();
            for (var i = 0; i < unlockedEntriesSize; i++) {
                unlockedEntries.add(buf.readResourceLocation());
            }

            var unlockedPagesSize = buf.readVarInt();
            for (var i = 0; i < unlockedPagesSize; i++) {
                var entryId = buf.readResourceLocation();
                var unlockedPagesForEntry = new ObjectOpenHashSet<Integer>();
                unlockedPages.put(entryId, unlockedPagesForEntry);

                var pagesSize = buf.readVarInt();
                for (var j = 0; j < pagesSize; j++) {
                    unlockedPagesForEntry.add(buf.readVarInt());
                }
            }

            var readEntriesSize = buf.readVarInt();
            for (var i = 0; i < readEntriesSize; i++) {
                readEntries.add(buf.readResourceLocation());
            }

            unlockedCategories.trim();
            unlockedPages.trim();
            unlockedPages.trim();
            readEntries.trim();

            this.unlockedCategories.put(bookId, unlockedCategories);
            this.unlockedEntries.put(bookId, unlockedEntries);
            this.unlockedPages.put(bookId, unlockedPages);
            this.readEntries.put(bookId, readEntries);

            return book;
        } catch (Exception e) {
            return null;
        }
    }
}
