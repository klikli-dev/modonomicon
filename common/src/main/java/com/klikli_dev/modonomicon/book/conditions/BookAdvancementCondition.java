/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.klikli_dev.modonomicon.networking.RequestAdvancementMessage;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class BookAdvancementCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("advancement");
    public static final MapCodec<BookAdvancementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            Identifier.CODEC.fieldOf("advancement_id").forGetter(condition -> condition.advancementId)
    ).apply(instance, (tooltip, advancementId) -> new BookAdvancementCondition(tooltip.orElse(null), advancementId)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookAdvancementCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            Identifier.STREAM_CODEC, condition -> condition.advancementId,
            (tooltip, advancementId) -> new BookAdvancementCondition(tooltip.orElse(null), advancementId)
    );

    protected Identifier advancementId;

    public BookAdvancementCondition(Component component, Identifier advancementId) {
        super(component);
        this.advancementId = advancementId;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.ADVANCEMENT;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (Services.SERVER_CONFIG.disableAdvancementLocking()) {
            return true;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.level().getServer().getAdvancements().get(this.advancementId);
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
        public static Component getAdvancementTitle(Player player, Identifier advancementId) {
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
