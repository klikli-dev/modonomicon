/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.networking.RequestSyncBookStatesMessage;
import com.klikli_dev.modonomicon.networking.SyncBookUnlockStatesMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BookUnlockStateManager {

    private static final BookUnlockStateManager instance = new BookUnlockStateManager();
    public BookStatesSaveData saveData;

    public static BookUnlockStateManager get() {
        return instance;
    }

    private BookUnlockStates getStateFor(Player player) {
        this.getSaveDataIfNecessary(player);
        return this.saveData.getUnlockStates(player.getUUID());
    }

    public String getUnlockCodeFor(Player player, Book book) {
        return this.getStateFor(player).getUnlockCode(book);
    }

    public Book applyUnlockCodeFor(ServerPlayer player, String unlockCode) {
        var book = this.getStateFor(player).applyUnlockCode(unlockCode);
        if (book != null) {
            this.saveData.setDirty();
            this.syncFor(player);
        }
        return book;
    }

    public void syncFor(ServerPlayer player) {
        Services.NETWORK.sendTo(player, new SyncBookUnlockStatesMessage(this.getStateFor(player)));
    }

    public void updateAndSyncFor(ServerPlayer player) {
        if (BookDataManager.get().areBooksBuilt()) {
            this.getStateFor(player).update(player);
            this.saveData.setDirty();
            this.syncFor(player);
        } else {
            //we have some edge cases where RecipesUpdatedEvent is fired after EntityJoinLevelEvent.
            //in SP this means that books are not built yet when updateAndSyncFor is called for the first time.
            //so we poll until it is available.
            var timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    player.server.execute(() -> {
                        BookUnlockStateManager.this.updateAndSyncFor(player);
                    });
                }
            }, 1000);

        }
    }

    public List<ResourceLocation> getBooksFor(Player player) {
        return this.getStateFor(player).getBooks();
    }

    public void resetFor(ServerPlayer player, Book book) {
        this.getStateFor(player).reset(book);
        this.saveData.setDirty();
        this.syncFor(player);
    }

    public boolean isUnlockedFor(Player player, BookCategory category) {
        return this.getStateFor(player).isUnlocked(category);
    }

    public boolean isUnlockedFor(Player player, BookEntry entry) {
        return this.getStateFor(player).isUnlocked(entry);
    }

    public boolean isUnlockedFor(Player player, BookPage page) {
        return this.getStateFor(player).isUnlocked(page);
    }

    public List<BookPage> getUnlockedPagesFor(Player player, BookEntry entry) {
        return this.getStateFor(player).getUnlockedPagesIn(entry);
    }

    public boolean isReadFor(Player player, BookEntry entry) {
        return this.getStateFor(player).isRead(entry);
    }

    public boolean canRunFor(Player player, BookCommand command) {
        return this.getStateFor(player).canRun(command);
    }

    /**
     * Sets the given command as run (counts up the run count).
     * Modifies state, but needs to be run on client side to update the book gui quickly.
     * Does not call syncFor, needs to be done by the caller side if needed.
     */
    public void setRunFor(Player player, BookCommand command) {
        this.getStateFor(player).setRun(command);
    }

    /**
     * Modifies state, but does not call syncFor, needs to be done by the caller side if needed.
     */
    public boolean readFor(ServerPlayer player, BookEntry entry) {
        return this.getStateFor(player).read(entry, player);
    }

    public void onAdvancement(ServerPlayer player) {
        this.updateAndSyncFor(player);
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
