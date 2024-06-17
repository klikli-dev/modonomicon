/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.CategoryVisualState;
import com.klikli_dev.modonomicon.bookstate.visual.EntryVisualState;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.networking.RequestSyncBookStatesMessage;
import com.klikli_dev.modonomicon.networking.SyncBookVisualStatesMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.List;

public class BookVisualStateManager {

    private static final BookVisualStateManager instance = new BookVisualStateManager();
    public BookStatesSaveData saveData;

    public static BookVisualStateManager get() {
        return instance;
    }

    private BookVisualStates getStateFor(Player player) {
        this.getSaveDataIfNecessary(player);
        return this.saveData.getVisualStates(player.getUUID());
    }

    public BookVisualState getBookStateFor(Player player, Book book) {
        return this.getStateFor(player).getBookState(book);
    }

    public CategoryVisualState getCategoryStateFor(Player player, BookCategory category) {
        return this.getStateFor(player).getCategoryState(category);
    }

    public EntryVisualState getEntryStateFor(Player player, BookEntry entry) {
        return this.getStateFor(player).getEntryState(entry);
    }

    public List<BookAddress> getBookmarksFor(Player player, Book book) {
        return this.getStateFor(player).getBookmarks(book);
    }

    public void setEntryStateFor(ServerPlayer player, BookEntry entry, EntryVisualState state) {
        this.getStateFor(player).setEntryState(entry, state);
        this.saveData.setDirty();
    }

    public void setCategoryStateFor(ServerPlayer player, BookCategory category, CategoryVisualState state) {
        this.getStateFor(player).setCategoryState(category, state);
        this.saveData.setDirty();
    }

    public void setBookStateFor(ServerPlayer player, Book book, BookVisualState state) {
        this.getStateFor(player).setBookState(book, state);
        this.saveData.setDirty();
    }

    public void setBookmarksFor(ServerPlayer player, Book book, List<BookAddress> bookmarks) {
        this.getStateFor(player).setBookmarks(book, bookmarks);
        this.saveData.setDirty();
    }

    public void addBookmarkFor(Player player, Book book, BookAddress bookmark) {
        this.getStateFor(player).addBookmark(book, bookmark);
        this.saveData.setDirty();
    }

    public boolean removeBookmarkFor(Player player, Book book, BookAddress bookmark) {
        var result = this.getStateFor(player).removeBookmark(book, bookmark);
        this.saveData.setDirty();
        return result;
    }

    public void syncFor(ServerPlayer player) {
        Services.NETWORK.sendTo(player, new SyncBookVisualStatesMessage(this.getStateFor(player)));
    }

    /**
     * On the server will use the player reference to obtain the BookStatesSaveData from the overworld data storage.
     * Won't do anything on the client, clients get their save data set by the server via packet.
     */
    private void getSaveDataIfNecessary(Player player) {
        if (this.saveData == null) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.saveData = serverPlayer.getServer().overworld().getDataStorage().computeIfAbsent(
                        new SavedData.Factory<>(BookStatesSaveData::new, BookStatesSaveData::load, DataFixTypes.PLAYER),
                        BookStatesSaveData.ID
                );
            } else {
                //this should not happen, we set an empty object to prevent a crash
                this.saveData = new BookStatesSaveData();
                //and we request a sync
                Services.NETWORK.sendToServer(RequestSyncBookStatesMessage.INSTANCE);
                Modonomicon.LOG.error("Tried to get Modonomicon save data for player on client side, but was not set. This should not happen. Requesting a sync from the server. Please re-open the book in a few seconds to see your progress.");
            }
        }
    }


}
