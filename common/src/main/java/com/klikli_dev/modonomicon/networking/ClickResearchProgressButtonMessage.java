/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.bookstate.BookServices;
import com.klikli_dev.modonomicon.bookstate.BookVisualStateManager;
import com.klikli_dev.modonomicon.bookstate.visual.BookVisibilitySnapshots;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.research.ResearchServices;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ClickResearchProgressButtonMessage implements Message {

    public static final Type<ClickResearchProgressButtonMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "click_research_progress_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClickResearchProgressButtonMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            (m) -> m.bookId,
            ByteBufCodecs.BOOL,
            (m) -> m.progressAll,
            ClickResearchProgressButtonMessage::new
    );

    public Identifier bookId;
    public boolean progressAll;

    public ClickResearchProgressButtonMessage(Identifier bookId, boolean progressAll) {
        this.bookId = bookId;
        this.progressAll = progressAll;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        var book = BookDataManager.get().getBook(this.bookId);
        if (book == null) {
            return;
        }

        var before = BookVisibilitySnapshots.collect(player, book);
        boolean researchChanged = false;

        for (var entry : book.getEntries().values()) {
            if (!this.progressAll && !BookServices.visibility().isVisible(player, entry)) {
                continue;
            }
            researchChanged |= ResearchServices.hooks().onEntryViewedOnce(player, entry.getId());
        }

        if (researchChanged) {
            BookVisualStateManager.get().updateVisibilityDrivenUnread(player, book, before, BookVisibilitySnapshots.collect(player, book));
            ResearchServices.state().syncFor(player);
            BookVisualStateManager.get().syncFor(player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
