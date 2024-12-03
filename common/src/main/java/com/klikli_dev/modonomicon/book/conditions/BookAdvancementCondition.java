/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.networking.RequestAdvancementMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class BookAdvancementCondition extends BookCondition {

    protected ResourceLocation advancementId;

    public BookAdvancementCondition(Component component, ResourceLocation advancementId) {
        super(component);
        this.advancementId = advancementId;
    }

    public static BookAdvancementCondition fromJson(ResourceLocation conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        var advancementId = ResourceLocation.parse(GsonHelper.getAsString(json, "advancement_id"));


        //default tooltip is null because we construct it on the fly from the advancement id
        Component tooltip = null;

        if (json.has("tooltip")) {
            tooltip = tooltipFromJson(json, provider);
        }

        return new BookAdvancementCondition(tooltip, advancementId);
    }

    public static BookAdvancementCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var advancementId = buffer.readResourceLocation();
        return new BookAdvancementCondition(tooltip, advancementId);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.ADVANCEMENT;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        buffer.writeResourceLocation(this.advancementId);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.getServer().getAdvancements().get(this.advancementId);
            return advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return false;
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip != null) {
            return List.of(this.tooltip);
        }

        var tooltip = Component.translatable(Tooltips.CONDITION_ADVANCEMENT, DistHelper.getAdvancementTitle(player, this.advancementId));

        return List.of(tooltip);
    }

    public static class DistHelper {
        public static long lastRequestTime = 0;
        public static Component getAdvancementTitle(Player player, ResourceLocation advancementId) {
            if (player instanceof LocalPlayer localPlayer) {
                //Problem: Advancements are not synced to the client by vanilla if they are visible - and actively removed if they are not.
                var adv = localPlayer.connection.getAdvancements().get(advancementId);

                //if not known by the player, check our local cache
                if (adv == null)
                    adv = BookDataManager.Client.get().getAdvancement(advancementId);


                //if not available locally, request from server for our local cache
                if (adv == null) {
                    //only request every second
                    if (System.currentTimeMillis() - lastRequestTime > 1000) {
                        lastRequestTime = System.currentTimeMillis();
                        Services.NETWORK.sendToServer(new RequestAdvancementMessage(advancementId));
                    }
                    return Component.translatable(Tooltips.CONDITION_ADVANCEMENT_LOADING);
                }

                if (!adv.value().display().isPresent()) //if advancement has no display we cannot show anything
                    return Component.translatable(Tooltips.CONDITION_ADVANCEMENT_HIDDEN);

                return adv.value().display().get().getTitle();
            }

            return Component.literal("Unknown"); //this should never happen -> player always must be local player
        }
    }
}
