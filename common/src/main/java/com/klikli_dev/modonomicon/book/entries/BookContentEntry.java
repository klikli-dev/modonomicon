/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.RequirementsDisplay;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookEntryType;
import com.klikli_dev.modonomicon.registry.BookEntryTypeRegistry;
import com.klikli_dev.modonomicon.research.ResearchServices;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookContentEntry extends BookEntry {

    public static final Identifier ID = Modonomicon.loc("content");

    public static final MapCodec<BookContentEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(BookEntry::getId),
            BookEntry.BookEntryData.CODEC.forGetter(entry -> entry.data),
            Identifier.CODEC.optionalFieldOf("command_to_run_on_first_read").forGetter(entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId)),
            BookPage.CODEC.listOf().optionalFieldOf("pages", List.of()).forGetter(entry -> entry.pages),
            BookEntryTab.CODEC.listOf().optionalFieldOf("tabs", List.of()).forGetter(entry -> entry.tabs),
            BookEntryProgression.CODEC.optionalFieldOf("progression").forGetter(entry -> Optional.ofNullable(entry.progression))
    ).apply(instance, (id, data, commandToRunOnFirstReadId, pages, tabs, progression) -> new BookContentEntry(id, data, commandToRunOnFirstReadId.orElse(null), pages, tabs, progression.orElse(null))));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookContentEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BookEntry::getId,
            BookEntry.BookEntryData.STREAM_CODEC, entry -> entry.data,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), entry -> Optional.ofNullable(entry.commandToRunOnFirstReadId),
            BookPage.STREAM_CODEC.apply(ByteBufCodecs.list()), entry -> entry.pages,
            BookEntryTab.STREAM_CODEC.apply(ByteBufCodecs.list()), entry -> entry.tabs,
            ByteBufCodecs.optional(BookEntryProgression.STREAM_CODEC), entry -> Optional.ofNullable(entry.progression),
            (id, data, commandToRunOnFirstReadId, pages, tabs, progression) -> new BookContentEntry(id, data, commandToRunOnFirstReadId.orElse(null), pages, tabs, progression.orElse(null))
    );

    protected List<BookPage> pages;
    protected List<BookEntryTab> tabs;
    @Nullable
    protected BookEntryProgression progression;

    public BookContentEntry(Identifier id, BookEntryData data, Identifier commandToRunOnFirstReadId, List<BookPage> pages) {
        this(id, data, commandToRunOnFirstReadId, pages, List.of());
    }

    public BookContentEntry(Identifier id, BookEntryData data, Identifier commandToRunOnFirstReadId, List<BookPage> pages, List<BookEntryTab> tabs) {
        this(id, data, commandToRunOnFirstReadId, pages, tabs, null);
    }

    public BookContentEntry(Identifier id, BookEntryData data, Identifier commandToRunOnFirstReadId, List<BookPage> pages, List<BookEntryTab> tabs, @Nullable BookEntryProgression progression) {
        super(id, data, commandToRunOnFirstReadId);
        this.pages = new ArrayList<>(pages);
        this.tabs = new ArrayList<>(tabs);
        this.progression = progression;
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

        //tab pages are numbered after the authored pages, so they never collide with them
        for (var tab : this.tabs) {
            BookErrorManager.get().getContextHelper().pageNumber = pageNum;
            tab.getPage().build(level, this, pageNum);
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
        for (var tab : this.tabs) {
            var page = tab.getPage();
            BookErrorManager.get().getContextHelper().pageNumber = page.getPageNumber();
            page.prerenderMarkdown(textRenderer);
            BookErrorManager.get().getContextHelper().pageNumber = -1;
        }
    }

    @Override
    public List<BookPage> getPages() {
        return this.pages;
    }

    public List<BookEntryTab> getTabs() {
        return this.tabs;
    }

    /**
     * The research progression of this entry, or null if the entry has none.
     */
    @Nullable
    public BookEntryProgression getProgression() {
        return this.progression;
    }

    /**
     * The requirements display of this entry: the entry's own override, or the book default.
     */
    public RequirementsDisplay getRequirementsDisplay() {
        if (this.progression != null && this.progression.requirementsDisplay().isPresent()) {
            return this.progression.requirementsDisplay().get();
        }
        return this.getBook() != null ? this.getBook().getDefaultRequirementsDisplay() : RequirementsDisplay.FIRST_PAGE;
    }

    /**
     * True if the player completed this entry. Entries with a progression are complete once their research node is complete, other entries once they are read.
     */
    public boolean isComplete(Player player) {
        if (this.progression != null) {
            return ResearchServices.state().isNodeComplete(player, this.progression.node());
        }
        return BookServices.stateAccess().isEntryRead(player, this);
    }

    public void addPage(BookPage page) {
        this.pages.add(page);
    }

    @Override
    public List<BookPage> getUnlockedPagesFor(Player player) {
        return BookServices.visibility().getVisiblePages(player, this);
    }

    @Override
    public int getPageNumberForId(String id) {
        var pages = this.getPages();
        for (int i = 0; i < pages.size(); i++) {
            var page = pages.get(i);
            if (id.equals(page.getId())) {
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

        for (var tab : this.tabs) {
            if (tab.getPage().matchesQuery(query, level)) {
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
