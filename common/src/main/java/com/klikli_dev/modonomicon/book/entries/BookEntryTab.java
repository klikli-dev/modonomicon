/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A side bookmark on the left or right edge of an entry. Clicking it opens {@link #getPage()} as a single page overlay.
 * The page is not part of the entry's regular pages.
 * Tabs can be authored inline in the entry json or as files in the entry's tabs directory.
 */
public class BookEntryTab {

    public static final Codec<BookEntryTab> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("id").forGetter(tab -> Optional.ofNullable(tab.id)),
            BookPage.CODEC.fieldOf("page").forGetter(BookEntryTab::getPage),
            BookIcon.CODEC.optionalFieldOf("icon").forGetter(tab -> Optional.ofNullable(tab.icon)),
            ComponentSerialization.CODEC.optionalFieldOf("title").forGetter(tab -> Optional.ofNullable(tab.title)),
            EntryTabSide.CODEC.optionalFieldOf("side", EntryTabSide.RIGHT).forGetter(BookEntryTab::getSide),
            Codec.STRING.optionalFieldOf("anchor").forGetter(tab -> Optional.ofNullable(tab.anchor)),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookEntryTab::getCondition),
            Codec.INT.optionalFieldOf("sort_number", -1).forGetter(BookEntryTab::getSortNumber)
    ).apply(instance, (id, page, icon, title, side, anchor, condition, sortNumber) -> new BookEntryTab(
            id.orElse(null), page, icon.orElse(null), title.orElse(null), side, anchor.orElse(null), condition, sortNumber)));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryTab> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), tab -> Optional.ofNullable(tab.id),
            BookPage.STREAM_CODEC, BookEntryTab::getPage,
            ByteBufCodecs.optional(BookIcon.STREAM_CODEC), tab -> Optional.ofNullable(tab.icon),
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), tab -> Optional.ofNullable(tab.title),
            EntryTabSide.STREAM_CODEC, BookEntryTab::getSide,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), tab -> Optional.ofNullable(tab.anchor),
            BookCondition.STREAM_CODEC, BookEntryTab::getCondition,
            ByteBufCodecs.VAR_INT, BookEntryTab::getSortNumber,
            (id, page, icon, title, side, anchor, condition, sortNumber) -> new BookEntryTab(
                    id.orElse(null), page, icon.orElse(null), title.orElse(null), side, anchor.orElse(null), condition, sortNumber)
    );

    @Nullable
    protected final String id;
    protected final BookPage page;
    @Nullable
    protected final BookIcon icon;
    @Nullable
    protected final Component title;
    protected final EntryTabSide side;
    @Nullable
    protected final String anchor;
    protected final BookCondition condition;
    protected final int sortNumber;

    public BookEntryTab(@Nullable String id, BookPage page, @Nullable BookIcon icon, @Nullable Component title, EntryTabSide side,
                        @Nullable String anchor, BookCondition condition, int sortNumber) {
        this.id = id;
        this.page = page;
        this.icon = icon;
        this.title = title;
        this.side = side;
        this.anchor = anchor;
        this.condition = condition;
        this.sortNumber = sortNumber;
    }

    public static BookEntryTab fromJson(Identifier entryId, JsonObject json, HolderLookup.Provider provider) {
        return CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json)
                .getOrThrow(error -> new IllegalArgumentException("Failed to decode tab for " + entryId + ": " + error));
    }

    /**
     * @return the id of the tab, i.e. the file name for file tabs. Null for inline tabs without an id.
     */
    @Nullable
    public String getId() {
        return this.id;
    }

    public BookPage getPage() {
        return this.page;
    }

    /**
     * The explicitly authored icon, or null if the tab should derive its icon from the page.
     */
    @Nullable
    public BookIcon getIcon() {
        return this.icon;
    }

    @Nullable
    public Component getTitle() {
        return this.title;
    }

    public EntryTabSide getSide() {
        return this.side;
    }

    /**
     * The id of the page the tab is only visible for, while that page is on the open spread. Null if not anchored.
     */
    @Nullable
    public String getAnchor() {
        return this.anchor;
    }

    public BookCondition getCondition() {
        return this.condition;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    /**
     * Whether the tab is available to the player: its page is visible and its condition is met.
     * Does not consider the anchor, i.e. whether the anchored page is on the open spread.
     */
    public boolean isAvailableFor(Player player, BookContentEntry entry) {
        if (!BookServices.visibility().isVisible(player, this.page)) {
            return false;
        }
        return this.condition.test(BookConditionContext.of(entry.getBook(), entry), player);
    }

    /**
     * The icon item derived from the page (e.g. the recipe output), or {@link ItemStack#EMPTY}.
     */
    public ItemStack getPageIconStack() {
        return this.page.getTabIcon();
    }
}
