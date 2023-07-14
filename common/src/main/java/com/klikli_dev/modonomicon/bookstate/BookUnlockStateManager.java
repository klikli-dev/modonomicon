package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.book.BookCommand;
import com.klikli_dev.modonomicon.book.BookEntry;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.registry.CapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BookUnlockStateManager {

    private static final BookUnlockStateManager instance = new BookUnlockStateManager();
    public BookStatesSaveData saveData;

    public static BookUnlockStateManager get() {
        return instance;
    }

    private BookUnlockStates getStateFor(Player player){
        this.getSaveDataIfNecessary(player);
        return this.saveData.unlockStates.get(player.getUUID());
    }

    public String getUnlockCodeFor(Player player, Book book) {
        return this.getStateFor(player).getUnlockCode(book);
    }

    public Book applyUnlockCodeFor(ServerPlayer player, String unlockCode) {
        var book = this.getStateFor(player).applyUnlockCode(unlockCode);
        if (book != null) {
            this.saveData.setDirty();
            this.syncFor(player);
            this.saveData.sync(player);
        }
        return book;
    }


    public void syncFor(ServerPlayer player) {


        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(c -> c.sync(player));
    }

    public static void updateAndSyncFor(ServerPlayer player) {
        if (BookDataManager.get().areBooksBuilt()) {
            player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(capability -> {
                capability.update(player);
                capability.sync(player);
            });
        } else {
            //we have some edge cases where RecipesUpdatedEvent is fired after EntityJoinLevelEvent.
            //in SP this means that books are not built yet when updateAndSyncFor is called for the first time.
            //so we poll until it is available.
            var timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    player.server.execute(() -> {
                        updateAndSyncFor(player);
                    });
                }
            }, 1000);

        }
    }

    public static List<ResourceLocation> getBooksFor(Player player) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.getBooks()).orElse(Collections.emptyList());
    }

    public static void resetFor(Player player, Book book) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(c -> c.reset(book));
    }

    public static boolean isUnlockedFor(Player player, BookCategory category) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isUnlocked(category)).orElse(false);
    }

    public static boolean isUnlockedFor(Player player, BookEntry entry) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isUnlocked(entry)).orElse(false);
    }

    public static boolean isReadFor(Player player, BookEntry entry) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isRead(entry)).orElse(false);
    }

    public static boolean canRunFor(Player player, BookCommand command) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.canRun(command)).orElse(false);
    }

    public static void setRunFor(Player player, BookCommand command) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(c -> c.setRun(command));
    }

    public static void onAdvancement(final AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverplayer) {
            updateAndSyncFor(serverplayer);
        }
    }

    /**
     * On the server will use the player reference to obtain the BookStatesSaveData from the overworld data storage.
     * Won't do anything on the client, clients get their save data set by the server via packet.
     */
    private void getSaveDataIfNecessary(Player player) {
        if (this.saveData == null && player instanceof ServerPlayer serverPlayer) {
            this.saveData = serverPlayer.getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(
                    BookStatesSaveData::load,
                    BookStatesSaveData::new, BookStatesSaveData.ID);
        }
    }


}
