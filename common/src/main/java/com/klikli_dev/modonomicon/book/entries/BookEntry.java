/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.conditions.BookAndCondition;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookEntryReadCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class BookEntry {

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

    public abstract Identifier getType();

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
        var isEntryUnlocked = BookUnlockStateManager.get().isUnlockedFor(player, this);

        // if an entry has its unlock condition met, it will always shop up
        if (isEntryUnlocked) {
            return EntryDisplayState.UNLOCKED;
        }

        // if an entry does have parents, it might be hidden
        if (!this.getParents().isEmpty()) {
            var anyParentsUnlocked = false;
            var allParentsUnlocked = true;
            for (var parent : this.getParents()) {
                if (!BookUnlockStateManager.get().isUnlockedFor(player, parent.getEntry())) {
                    allParentsUnlocked = false;
                } else {
                    anyParentsUnlocked = true;
                }
            }

            if (this.showWhenAnyParentUnlocked() && !anyParentsUnlocked)
                return EntryDisplayState.HIDDEN;

            if (!this.showWhenAnyParentUnlocked() && !allParentsUnlocked)
                return EntryDisplayState.HIDDEN;
        }

        // either the entry does not have any parents or any/all parents are unlocked
        return this.hideWhileLocked() ? EntryDisplayState.HIDDEN : EntryDisplayState.LOCKED;
    }

    /**
     * Returns true if this entry should show up in search for the given query.
     */
    public boolean matchesQuery(String query, Level level) {
        return this.data.name().toLowerCase().contains(query);
    }

    public int getPageNumberForAnchor(String anchor) {
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

    public abstract void toNetwork(RegistryFriendlyByteBuf buf);

    /**
     * The entry background is selected by GUI sprite id.
     */
    public record BookEntryData(Identifier categoryId, List<BookEntryParent> parents, int x, int y, String name,
                                String description, BookIcon icon, GuiSprite entryBackground,
                                BookCondition condition, boolean hideWhileLocked, boolean showWhenAnyParentUnlocked,
                                int sortNumber) {

        public static BookEntryData fromJson(Identifier id, JsonObject json, boolean autoAddReadConditions, HolderLookup.Provider provider) {
            var categoryIdPath = GsonHelper.getAsString(json, "category");
            var categoryId = categoryIdPath.contains(":") ?
                    Identifier.parse(categoryIdPath) :
                    Identifier.fromNamespaceAndPath(id.getNamespace(), categoryIdPath);

            var x = GsonHelper.getAsInt(json, "x");
            var y = GsonHelper.getAsInt(json, "y");

            var parents = new ArrayList<BookEntryParent>();
            if (json.has("parents")) {
                for (var parent : GsonHelper.getAsJsonArray(json, "parents")) {
                    parents.add(BookEntryParent.fromJson(id, parent.getAsJsonObject()));
                }
            }

            var pages = new ArrayList<BookPage>();
            if (json.has("pages")) {
                for (var pageElem : GsonHelper.getAsJsonArray(json, "pages")) {
                    BookErrorManager.get().setContext("Page Index: {}", pages.size());
                    var pageJson = GsonHelper.convertToJsonObject(pageElem, "page");
                    var type = Identifier.parse(GsonHelper.getAsString(pageJson, "type"));
                    var loader = LoaderRegistry.getPageJsonLoader(type);

                    var page = loader.fromJson(id, pageJson, provider);
                    pages.add(page);
                }
            }

            var name = GsonHelper.getAsString(json, "name");
            var description = GsonHelper.getAsString(json, "description", "");
            var icon = BookIcon.fromJson(json.get("icon"), provider);
            var entryBackground = json.has("background") ? GuiSprite.fromJson(json.get("background")) : GuiSprite.EMPTY;

            BookCondition condition = new BookNoneCondition(); //default to unlocked
            if (json.has("condition")) {
                condition = BookCondition.fromJson(id, json.getAsJsonObject("condition"), provider);
            } else if (autoAddReadConditions) {
                if (parents.size() == 1) {
                    condition = new BookEntryReadCondition(null, parents.get(0).getEntryId());
                } else if (parents.size() > 1) {
                    var conditions = parents.stream().map(parent -> new BookEntryReadCondition(null, parent.getEntryId())).toList();
                    condition = new BookAndCondition(null, conditions.toArray(new BookEntryReadCondition[0]));
                }
            }
            var hideWhileLocked = GsonHelper.getAsBoolean(json, "hide_while_locked", false);

            /**
             * If true, the entry will show (locked) as soon as any parent is unlocked.
             * If false, the entry will only show (locked) as soon as all parents are unlocked.
             */
            var showWhenAnyParentUnlocked = GsonHelper.getAsBoolean(json, "show_when_any_parent_unlocked", false);

            var sortNumber = GsonHelper.getAsInt(json, "sort_number", -1);

            return new BookEntryData(categoryId, parents, x, y, name, description, icon, entryBackground, condition, hideWhileLocked, showWhenAnyParentUnlocked, sortNumber);
        }

        public static BookEntryData fromNetwork(RegistryFriendlyByteBuf buffer) {
            var categoryId = buffer.readIdentifier();
            var name = buffer.readUtf();
            var description = buffer.readUtf();
            var icon = BookIcon.fromNetwork(buffer);
            var x = buffer.readVarInt();
            var y = buffer.readVarInt();
            var entryBackground = GuiSprite.fromNetwork(buffer);
            var hideWhileLocked = buffer.readBoolean();
            var showWhenAnyParentUnlocked = buffer.readBoolean();
            var condition = BookCondition.fromNetwork(buffer);

            var parentEntries = new ArrayList<BookEntryParent>();
            var parentCount = buffer.readVarInt();
            for (var i = 0; i < parentCount; i++) {
                parentEntries.add(BookEntryParent.fromNetwork(buffer));
            }

            var sortNumber = buffer.readVarInt();

            return new BookEntryData(categoryId, parentEntries, x, y, name, description, icon, entryBackground, condition, hideWhileLocked, showWhenAnyParentUnlocked, sortNumber);
        }

        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            buffer.writeIdentifier(this.categoryId);
            buffer.writeUtf(this.name);
            buffer.writeUtf(this.description);
            this.icon.toNetwork(buffer);
            buffer.writeVarInt(this.x);
            buffer.writeVarInt(this.y);
            this.entryBackground.toNetwork(buffer);
            buffer.writeBoolean(this.hideWhileLocked);
            buffer.writeBoolean(this.showWhenAnyParentUnlocked);

            buffer.writeIdentifier(this.condition.getType());
            this.condition.toNetwork(buffer);

            buffer.writeVarInt(this.parents.size());
            for (var parent : this.parents) {
                parent.toNetwork(buffer);
            }

            buffer.writeVarInt(this.sortNumber);
        }
    }
}

