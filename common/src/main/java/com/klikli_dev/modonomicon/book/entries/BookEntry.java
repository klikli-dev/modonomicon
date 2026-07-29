/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.data.BookEntryType;
import com.klikli_dev.modonomicon.registry.BookEntryTypeRegistry;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BookEntry {

    public static final Codec<BookEntry> CODEC = Codec.lazyInitialized(() -> BookEntryTypeRegistry.codec().dispatch(
            "type",
            BookEntry::type,
            BookEntryType::codec
    ));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntry> STREAM_CODEC = StreamCodec.recursive(codec -> BookEntryTypeRegistry.streamCodec()
                    .dispatch(
                            BookEntry::type,
                            BookEntryType::streamCodec
                    ));

    protected final BookEntryData data;
    protected Identifier id;
    protected Book book;
    protected BookCategory category;
    protected List<ResolvedBookEntryParent> parents;

    /**
     * if this is not null, the command will be run when the entry is first read.
     */
    protected Identifier commandToRunOnFirstReadId;
    protected BookCommand commandToRunOnFirstRead;

    public BookEntry(Identifier id, BookEntryData data, Identifier commandToRunOnFirstReadId) {
        this.id = id;
        this.data = data;

        this.commandToRunOnFirstReadId = commandToRunOnFirstReadId;
    }

    public int getX() {
        return this.data.x;
    }

    public int getY() {
        return this.data.y;
    }

    public static BookEntry fromJson(JsonObject json, HolderLookup.Provider provider) {
        return CODEC.parse(provider.createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE), json)
                .getOrThrow(error -> new IllegalArgumentException("Failed to decode entry: " + error));
    }

    public static BookEntry fromNetwork(RegistryFriendlyByteBuf buf) {
        return STREAM_CODEC.decode(buf);
    }

    public static void toNetwork(BookEntry entry, RegistryFriendlyByteBuf buf) {
        STREAM_CODEC.encode(buf, entry);
    }

    public abstract BookEntryType<?> type();

    public Identifier getType() {
        return this.type().id();
    }

    public abstract void openEntry(BookAddress address);

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Level level, BookCategory category) {
        this.book = category.getBook();
        this.category = category;

        //resolve parents
        var newParents = new ArrayList<ResolvedBookEntryParent>();
        for (var parent : this.data.parents) {
            var parentEntry = this.getBook().getEntry(parent.getEntryId());
            if (parentEntry == null) {
                BookErrorManager.get().error("Entry \"" + this.getId() + "\" has a parent that does not exist in this book: \"" + parent.getEntryId() + "\". This parent will be ignored");
            } else {
                newParents.add(new ResolvedBookEntryParent(parent, parentEntry));
            }
        }
        this.parents = newParents;

        if (this.commandToRunOnFirstReadId != null) {
            this.commandToRunOnFirstRead = this.getBook().getCommand(this.commandToRunOnFirstReadId);

            if (this.commandToRunOnFirstRead == null) {
                BookErrorManager.get().error("Command to run on first read \"" + this.commandToRunOnFirstReadId + "\" does not exist in this book. Set to null.");
                this.commandToRunOnFirstReadId = null;
            }
        }
    }

    public Identifier getId() {
        return this.id;
    }

    public EntryDisplayState getEntryDisplayState(Player player) {
        return BookServices.visibility().getEntryDisplayState(player, this);
    }

    /**
     * Returns true if this entry should show up in search for the given query.
     */
    public boolean matchesQuery(String query, Level level) {
        return this.data.name().toLowerCase().contains(query);
    }

    public int getPageNumberForId(String id) {
        return -1;
    }

    public List<BookPage> getPages() {
        return List.of();
    }

    public List<BookPage> getUnlockedPagesFor(Player player) {
        return List.of();
    }

    public BookCommand getCommandToRunOnFirstRead() {
        return this.commandToRunOnFirstRead;
    }

    public BookCondition getCondition() {
        return this.data.condition;
    }

    public String getName() {
        return this.data.name;
    }

    public BookCategory getCategory() {
        return this.category;
    }

    public Book getBook() {
        return this.book;
    }

    public String getDescription() {
        return this.data.description;
    }

    public List<? extends BookEntryParent> getParents() {
        return this.parents == null ? this.data.parents : this.parents;
    }

    public GuiSprite getEntryBackground() {
        return this.data.entryBackground;
    }

    public boolean showWhenAnyParentUnlocked() {
        return this.data.showWhenAnyParentUnlocked;
    }

    public boolean hideWhileLocked() {
        return this.data.hideWhileLocked;
    }

    public BookIcon getIcon() {
        return this.data.icon;
    }

    public Identifier getCategoryId() {
        return this.data.categoryId;
    }

    public int getSortNumber() {
        return this.data.sortNumber;
    }

    protected BookEntryData data() {
        return this.data;
    }

    public Identifier commandToRunOnFirstReadId() {
        return this.commandToRunOnFirstReadId;
    }

    @SuppressWarnings("unchecked")
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        ((StreamCodec<RegistryFriendlyByteBuf, BookEntry>) this.type().streamCodec().cast()).encode(buf, this);
    }

    /**
     * The entry background is selected by GUI sprite id.
     */
    public record BookEntryData(Identifier categoryId, List<BookEntryParent> parents, int x, int y, String name,
                                 String description, BookIcon icon, GuiSprite entryBackground,
                                 BookCondition condition, boolean hideWhileLocked, boolean showWhenAnyParentUnlocked,
                                 int sortNumber) {

        public static final MapCodec<BookEntryData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codecs.STRICT_IDENTIFIER.fieldOf("category").forGetter(BookEntryData::categoryId),
                Codec.list(BookEntryParent.CODEC).optionalFieldOf("parents", List.of()).forGetter(BookEntryData::parents),
                Codec.INT.fieldOf("x").forGetter(BookEntryData::x),
                Codec.INT.fieldOf("y").forGetter(BookEntryData::y),
                Codec.STRING.fieldOf("name").forGetter(BookEntryData::name),
                Codec.STRING.optionalFieldOf("description", "").forGetter(BookEntryData::description),
                BookIcon.CODEC.fieldOf("icon").forGetter(BookEntryData::icon),
                GuiSprite.CODEC.optionalFieldOf("background", GuiSprite.EMPTY).forGetter(BookEntryData::entryBackground),
                BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookEntryData::condition),
                Codec.BOOL.optionalFieldOf("hide_while_locked", false).forGetter(BookEntryData::hideWhileLocked),
                Codec.BOOL.optionalFieldOf("show_when_any_parent_unlocked", false).forGetter(BookEntryData::showWhenAnyParentUnlocked),
                Codec.INT.optionalFieldOf("sort_number", -1).forGetter(BookEntryData::sortNumber)
        ).apply(instance, BookEntryData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryData> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, BookEntryData::categoryId,
                ByteBufCodecs.collection(size -> new ArrayList<BookEntryParent>(size), BookEntryParent.STREAM_CODEC), BookEntryData::parents,
                ByteBufCodecs.INT, BookEntryData::x,
                ByteBufCodecs.INT, BookEntryData::y,
                ByteBufCodecs.STRING_UTF8, BookEntryData::name,
                ByteBufCodecs.STRING_UTF8, BookEntryData::description,
                BookIcon.STREAM_CODEC, BookEntryData::icon,
                GuiSprite.STREAM_CODEC, BookEntryData::entryBackground,
                BookCondition.STREAM_CODEC, BookEntryData::condition,
                ByteBufCodecs.BOOL, BookEntryData::hideWhileLocked,
                ByteBufCodecs.BOOL, BookEntryData::showWhenAnyParentUnlocked,
                ByteBufCodecs.INT, BookEntryData::sortNumber,
                BookEntryData::new
        );

        public static BookEntryData fromNetwork(RegistryFriendlyByteBuf buffer) {
            return STREAM_CODEC.decode(buffer);
        }

        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            STREAM_CODEC.encode(buffer, this);
        }
    }
}

