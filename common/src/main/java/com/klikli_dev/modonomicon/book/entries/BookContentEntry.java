/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookEntryType;
import com.klikli_dev.modonomicon.registry.BookEntryTypeRegistry;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookContentEntry extends BookEntry {

    public static final Identifier ID = Modonomicon.loc("content");

    public static final MapCodec<BookContentEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(BookEntry::getId),
            BookEntry.BookEntryData.CODEC.forGetter(entry -> entry.data),
            Identifier.CODEC.optionalFieldOf("command_to_run_on_first_read").forGetter(entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId)),
            BookPage.CODEC.listOf().fieldOf("pages").forGetter(entry -> entry.pages)
    ).apply(instance, (id, data, commandToRunOnFirstReadId, pages) -> new BookContentEntry(id, data, commandToRunOnFirstReadId.orElse(null), pages)));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookContentEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BookEntry::getId,
            BookEntry.BookEntryData.STREAM_CODEC, entry -> entry.data,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId),
            BookPage.STREAM_CODEC.apply(ByteBufCodecs.list()), entry -> entry.pages,
            (id, data, commandToRunOnFirstReadId, pages) -> new BookContentEntry(id, data, commandToRunOnFirstReadId.orElse(null), pages)
    );

    protected List<BookPage> pages;

    public BookContentEntry(Identifier id, BookEntryData data, Identifier commandToRunOnFirstReadId, List<BookPage> pages) {
        super(id, data, commandToRunOnFirstReadId);
        this.pages = new ArrayList<>(pages);
    }

    @Override
    public BookEntryType<?> type() {
        return BookEntryTypeRegistry.CONTENT;
    }

    /**
     * call after loading the book jsons to finalize.
     */
    @Override
    public void build(Level level, BookCategory category) {
        super.build(level, category);

        //build pages
        int pageNum = 0;
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = pageNum;
            page.build(level, this, pageNum);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
            pageNum++;
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        for (var page : this.pages) {
            BookErrorManager.get().getContextHelper().pageNumber = page.getPageNumber();
            page.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
        }
    }

    @Override
    public List<BookPage> getPages() {
        return this.pages;
    }

    public void addPage(BookPage page) {
        this.pages.add(page);
    }

    @Override
    public List<BookPage> getUnlockedPagesFor(Player player) {
        BookUnlockStateManager unlockManager = BookUnlockStateManager.get();
        return unlockManager.getUnlockedPagesFor(player, this);
    }

    @Override
    public int getPageNumberForAnchor(String anchor) {
        var pages = this.getPages();
        for (int i = 0; i < pages.size(); i++) {
            var page = pages.get(i);
            if (anchor.equals(page.getId())) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        if (super.matchesQuery(query, level)) {
            return true;
        }

        for (var page : this.getPages()) {
            if (page.matchesQuery(query, level)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void openEntry(BookAddress address) {
        BookGuiManager.get().openContentEntry(this, address);
    }
}
