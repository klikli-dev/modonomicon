/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.associated;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.entries.BookEntryTab;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Client-side reverse index from item to book entry/page for associated-item tooltips.
 * <p>
 * Entries and pages can declare {@code associated_items} as exact item templates or
 * ingredients (like spotlight pages); hovering a matching stack shows
 * a linked-page tooltip and holding the open key jumps to the book location.
 * Page-level associations take precedence over entry-level ones.
 * Pages of entry tabs are indexed as well, opening them shows the tab's spread with the tab open.
 * <p>
 * The index is rebuilt lazily: {@link BookDataManager} marks it dirty on (re-)load and sync,
 * the next lookup triggers a rebuild. Lookup itself is O(1) by item plus a short component
 * subset check over candidates sharing the item. Ingredient associations are kept in a
 * separate list and tested with {@link Ingredient#test(ItemStack)}.
 */
public class AssociatedItemLookup {

    /**
     * Points at an entry ({@code pageNumber < 0}), a specific page of an entry, or the page of an entry tab ({@code tabIndex >= 0}).
     * For tab associations {@code pageNumber} is the page the tab is anchored to (or the first page for entry-wide tabs),
     * so opening the association shows the spread the tab is visible on.
     */
    public record Association(Identifier bookId, Identifier entryId, int pageNumber, int tabIndex) {
        public Association(Identifier bookId, Identifier entryId, int pageNumber) {
            this(bookId, entryId, pageNumber, -1);
        }

        public boolean isTab() {
            return this.tabIndex >= 0;
        }
    }

    private record IngredientAssociation(Association association, Ingredient ingredient) {
    }

    private static final AssociatedItemLookup INSTANCE = new AssociatedItemLookup();

    private final Map<Item, List<Association>> byItem =
            Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    private final List<IngredientAssociation> byIngredient =
            Collections.synchronizedList(new ArrayList<>());

    private boolean dirty = true;

    public static AssociatedItemLookup get() {
        return INSTANCE;
    }

    /**
     * Marks the index stale. Called by {@link BookDataManager} on (re-)load and datapack sync.
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * Finds the best association for the given stack, or null if none matches.
     * A page-level match wins over an entry-level match.
     */
    public Association find(ItemStack stack) {
        return this.find(stack, association -> true);
    }

    /**
     * Finds the best association for the given stack that passes the given filter, or null if none matches.
     * A page-level match wins over an entry-level match.
     * The filter is evaluated on every lookup, so it can check state that changes after the index was built
     * (e.g. whether a tab can currently be opened). Rejected associations are skipped in favour of other matches.
     */
    public Association find(ItemStack stack, Predicate<Association> filter) {
        if (stack.isEmpty()) {
            return null;
        }

        this.ensureBuilt();

        Association entryFallback = null;

        List<Association> candidates = this.byItem.get(stack.getItem());
        if (candidates != null) {
            for (var candidate : candidates) {
                if (!this.matches(stack, candidate) || !filter.test(candidate)) {
                    continue;
                }
                if (candidate.pageNumber() >= 0) {
                    return candidate;
                }
                if (entryFallback == null) {
                    entryFallback = candidate;
                }
            }
        }

        synchronized (this.byIngredient) {
            for (var ingredientAssociation : this.byIngredient) {
                if (!AssociatedItemMatcher.matches(ingredientAssociation.ingredient(), stack)) {
                    continue;
                }
                var association = ingredientAssociation.association();
                // ensure the book/entry/page still exists; matches() would also succeed
                // because the tested ingredient belongs to it, but we avoid a second scan here
                if (this.resolveEntry(association) == null) {
                    continue;
                }
                if (association.pageNumber() >= 0 && this.resolvePage(association) == null) {
                    continue;
                }
                if (!filter.test(association)) {
                    continue;
                }
                if (association.pageNumber() >= 0) {
                    return association;
                }
                if (entryFallback == null) {
                    entryFallback = association;
                }
            }
        }
        return entryFallback;
    }

    /**
     * Resolves the entry for an association, or null if the book/entry is gone.
     */
    public BookEntry resolveEntry(Association association) {
        Book book = BookDataManager.get().getBook(association.bookId());
        if (book == null) {
            return null;
        }
        return book.getEntry(association.entryId());
    }

    /**
     * Resolves the tab for a tab association, or null if unavailable.
     */
    public BookEntryTab resolveTab(Association association) {
        if (!association.isTab()) {
            return null;
        }
        BookEntry entry = this.resolveEntry(association);
        if (!(entry instanceof BookContentEntry contentEntry)) {
            return null;
        }
        var tabs = contentEntry.getTabs();
        if (association.tabIndex() >= tabs.size()) {
            return null;
        }
        return tabs.get(association.tabIndex());
    }

    /**
     * Resolves the page for a page-level association, or null if unavailable.
     * For tab associations this is the page of the tab.
     */
    public BookPage resolvePage(Association association) {
        if (association.pageNumber() < 0) {
            return null;
        }
        if (association.isTab()) {
            var tab = this.resolveTab(association);
            return tab != null ? tab.getPage() : null;
        }
        BookEntry entry = this.resolveEntry(association);
        if (!(entry instanceof BookContentEntry contentEntry)) {
            return null;
        }
        var pages = contentEntry.getPages();
        if (association.pageNumber() >= pages.size()) {
            return null;
        }
        return pages.get(association.pageNumber());
    }

    private boolean matches(ItemStack stack, Association association) {
        BookEntry entry = this.resolveEntry(association);
        if (entry == null) {
            return false;
        }

        if (association.pageNumber() >= 0) {
            BookPage page = this.resolvePage(association);
            if (page == null) {
                return false;
            }
            return matchesAny(page.getAssociatedItems(), stack);
        }

        return matchesAny(entry.getAssociatedItems(), stack);
    }

    private static boolean matchesAny(List<Either<ItemStackTemplate, Ingredient>> associated, ItemStack stack) {
        for (var entry : associated) {
            if (entry == null) {
                continue;
            }
            if (AssociatedItemMatcher.matches(entry, stack)) {
                return true;
            }
        }
        return false;
    }

    private void ensureBuilt() {
        if (this.dirty) {
            this.rebuild();
        }
    }

    private void rebuild() {
        this.byItem.clear();
        synchronized (this.byIngredient) {
            this.byIngredient.clear();

            for (var book : BookDataManager.get().getBooks().values()) {
                for (var entry : book.getEntries().values()) {
                    this.indexEntry(book.getId(), entry);
                }
            }
        }

        this.dirty = false;
    }

    private void indexEntry(Identifier bookId, BookEntry entry) {
        for (var associated : entry.getAssociatedItems()) {
            if (associated == null) {
                continue;
            }
            associated.ifLeft(template -> {
                Item item = template.item().value();
                this.byItem.computeIfAbsent(item, k -> new ArrayList<>())
                        .add(new Association(bookId, entry.getId(), -1));
            });
            associated.ifRight(ingredient -> this.byIngredient.add(
                    new IngredientAssociation(new Association(bookId, entry.getId(), -1), ingredient)));
        }

        if (!(entry instanceof BookContentEntry contentEntry)) {
            return;
        }

        for (var page : contentEntry.getPages()) {
            this.indexPage(page, new Association(bookId, entry.getId(), page.getPageNumber()));
        }

        var tabs = contentEntry.getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            var tab = tabs.get(i);
            this.indexPage(tab.getPage(), new Association(bookId, entry.getId(), this.getTabPageNumber(contentEntry, tab), i));
        }
    }

    private void indexPage(BookPage page, Association association) {
        for (var associated : page.getAssociatedItems()) {
            if (associated == null) {
                continue;
            }
            associated.ifLeft(template -> {
                Item item = template.item().value();
                this.byItem.computeIfAbsent(item, k -> new ArrayList<>())
                        .add(association);
            });
            associated.ifRight(ingredient ->
                    this.byIngredient.add(new IngredientAssociation(association, ingredient)));
        }
    }

    /**
     * @return the number of the page the tab is anchored to, or the first page for entry-wide tabs or unknown anchors.
     */
    private int getTabPageNumber(BookContentEntry entry, BookEntryTab tab) {
        if (tab.getAnchor() != null) {
            for (var page : entry.getPages()) {
                if (tab.getAnchor().equals(page.getId())) {
                    return page.getPageNumber();
                }
            }
        }
        return 0;
    }
}
