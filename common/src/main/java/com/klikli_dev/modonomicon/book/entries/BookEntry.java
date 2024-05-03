/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2024 DaFuqs
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.entries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.*;
import com.klikli_dev.modonomicon.book.conditions.*;
import com.klikli_dev.modonomicon.book.error.*;
import com.klikli_dev.modonomicon.book.page.*;
import com.klikli_dev.modonomicon.client.gui.book.*;
import com.klikli_dev.modonomicon.client.gui.book.markdown.*;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import net.minecraft.network.*;
import net.minecraft.resources.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.level.*;

import java.util.*;

public abstract class BookEntry {

	protected ResourceLocation id;
	protected final BookEntryData data;

	protected Book book;
	protected BookCategory category;
	protected List<ResolvedBookEntryParent> parents;

	/**
	 * if this is not null, the command will be run when the entry is first read.
	 */
	protected ResourceLocation commandToRunOnFirstReadId;
	protected BookCommand commandToRunOnFirstRead;

	public BookEntry(ResourceLocation id, BookEntryData data, ResourceLocation commandToRunOnFirstReadId) {
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

	public abstract ResourceLocation getType();

	public abstract BookContentScreen openEntry(BookCategoryScreen categoryScreen);

	/**
	 * Called after build() (after loading the book jsons) to render markdown and store any errors
	 */
	public void prerenderMarkdown(BookTextRenderer textRenderer) { };

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

	public ResourceLocation getId() {
		return this.id;
	}

	/**
	 * Returns true if this entry should show up in search for the given query.
	 */
	public boolean matchesQuery(String query) {
		return data.name().toLowerCase().contains(query);
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

	public int getEntryBackgroundUIndex() {
		return this.data.entryBackgroundUIndex;
	}

	public int getEntryBackgroundVIndex() {
		return this.data.entryBackgroundVIndex;
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

	public ResourceLocation getCategoryId() {
		return this.data.categoryId;
	}

	public abstract void toNetwork(FriendlyByteBuf buf);

	/**
	 * The first two rows in "entry_textures.png" are reserved for the entry icons.
	 * the entry background is selected by querying the texture at entryBackgroundUIndex * 26 (= Y Axis / Up-Down), entryBackgroundUIndex * 26 (= X Axis / Left-Right)
	 * U index = Y Axis / Up-Down
	 * V index = X Axis / Left-Right
	 */
	public record BookEntryData(ResourceLocation categoryId, List<BookEntryParent> parents, int x, int y, String name, String description, BookIcon icon, int entryBackgroundUIndex, int entryBackgroundVIndex, BookCondition condition, boolean hideWhileLocked, boolean showWhenAnyParentUnlocked) {

		public static BookEntryData fromJson(JsonObject json, boolean autoAddReadConditions) {
			var categoryId = new ResourceLocation(GsonHelper.getAsString(json, "category"));
			var x = GsonHelper.getAsInt(json, "x");
			var y = GsonHelper.getAsInt(json, "y");

			var parents = new ArrayList<BookEntryParent>();
			if (json.has("parents")) {
				for (var parent : GsonHelper.getAsJsonArray(json, "parents")) {
					parents.add(BookEntryParent.fromJson(parent.getAsJsonObject()));
				}
			}

			var pages = new ArrayList<BookPage>();
			if (json.has("pages")) {
				for (var pageElem : GsonHelper.getAsJsonArray(json, "pages")) {
					BookErrorManager.get().setContext("Page Index: {}", pages.size());
					var pageJson = GsonHelper.convertToJsonObject(pageElem, "page");
					var type = new ResourceLocation(GsonHelper.getAsString(pageJson, "type"));
					var loader = LoaderRegistry.getPageJsonLoader(type);
					var page = loader.fromJson(pageJson);
					pages.add(page);
				}
			}

			var name = GsonHelper.getAsString(json, "name");
			var description = GsonHelper.getAsString(json, "description", "");
			var icon = BookIcon.fromJson(json.get("icon"));
			var entryBackgroundUIndex = GsonHelper.getAsInt(json, "background_u_index", 0);
			var entryBackgroundVIndex = GsonHelper.getAsInt(json, "background_v_index", 0);

			BookCondition condition = new BookNoneCondition(); //default to unlocked
			if (json.has("condition")) {
				condition = BookCondition.fromJson(json.getAsJsonObject("condition"));
			} else if(autoAddReadConditions) {
				if(parents.size() == 1) {
					condition = new BookEntryReadCondition(null, parents.get(0).getEntryId());
				} else if(parents.size() > 1) {
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

			return new BookEntryData(categoryId, parents, x, y, name, description, icon, entryBackgroundUIndex, entryBackgroundVIndex, condition, hideWhileLocked, showWhenAnyParentUnlocked);
		}

		public void toNetwork(FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(this.categoryId);
			buffer.writeUtf(this.name);
			buffer.writeUtf(this.description);
			this.icon.toNetwork(buffer);
			buffer.writeVarInt(this.x);
			buffer.writeVarInt(this.y);
			buffer.writeVarInt(this.entryBackgroundUIndex);
			buffer.writeVarInt(this.entryBackgroundVIndex);
			buffer.writeBoolean(this.hideWhileLocked);
			buffer.writeBoolean(this.showWhenAnyParentUnlocked);

			buffer.writeResourceLocation(this.condition.getType());
			this.condition.toNetwork(buffer);

			buffer.writeVarInt(this.parents.size());
			for (var parent : this.parents) {
				parent.toNetwork(buffer);
			}
		}

		public static BookEntryData fromNetwork(FriendlyByteBuf buffer) {
			var categoryId = buffer.readResourceLocation();
			var name = buffer.readUtf();
			var description = buffer.readUtf();
			var icon = BookIcon.fromNetwork(buffer);
			var x = buffer.readVarInt();
			var y = buffer.readVarInt();
			var entryBackgroundUIndex = buffer.readVarInt();
			var entryBackgroundVIndex = buffer.readVarInt();
			var hideWhileLocked = buffer.readBoolean();
			var showWhenAnyParentUnlocked = buffer.readBoolean();
			var condition = BookCondition.fromNetwork(buffer);

			var parentEntries = new ArrayList<BookEntryParent>();
			var parentCount = buffer.readVarInt();
			for (var i = 0; i < parentCount; i++) {
				parentEntries.add(BookEntryParent.fromNetwork(buffer));
			}

			return new BookEntryData(categoryId, parentEntries, x, y, name, description, icon, entryBackgroundUIndex, entryBackgroundVIndex, condition, hideWhileLocked, showWhenAnyParentUnlocked);
		}

	}

}
