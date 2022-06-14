/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonimiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.Book;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class BookAdvancementCondition extends BookCondition {

    protected ResourceLocation advancementId;

    public BookAdvancementCondition(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    public static BookAdvancementCondition fromJson(JsonObject json) {
        var advancementId = new ResourceLocation(GsonHelper.getAsString(json, "advancement_id"));
        return new BookAdvancementCondition(advancementId);
    }

    public static BookAdvancementCondition fromNetwork(FriendlyByteBuf buffer) {
        var advancementId = buffer.readResourceLocation();
        return new BookAdvancementCondition(advancementId);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.ADVANCEMENT;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.advancementId);
    }

    @Override
    public boolean test(Book book, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.getServer().getAdvancements().getAdvancement(this.advancementId);
            return advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        } else {
            var conn = Minecraft.getInstance().getConnection();
            if (conn != null) {
                var cm = conn.getAdvancements();
                var adv = cm.getAdvancements().get(this.advancementId);
                if (adv != null) {
                    var progress = cm.progress.get(adv);
                    return progress != null && progress.isDone();
                }
            }
        }

        return false;
    }
}
