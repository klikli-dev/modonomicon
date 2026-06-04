/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.networking;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.research.data.ResearchDataManager;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import com.klikli_dev.modonomicon.research.networking.ResearchToastTrigger;
import com.klikli_dev.modonomicon.client.gui.toast.ResearchToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ResearchToastMessage implements Message {

    public static final Type<ResearchToastMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "research_toast"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchToastMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ResearchToastTrigger.STREAM_CODEC),
            message -> message.triggers,
            ResearchToastMessage::new
    );

    public final List<ResearchToastTrigger> triggers;

    public ResearchToastMessage(List<ResearchToastTrigger> triggers) {
        this.triggers = triggers;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (!Services.CLIENT_CONFIG.shouldShowResearchToasts()) {
            return;
        }

        var toastManager = minecraft.getToastManager();
        var data = ResearchDataManager.get().data();

        for (var trigger : this.triggers) {
            ResearchToastDefinition toastDef = null;
            switch (trigger.type()) {
                case FACT_GRANTED -> toastDef = data.factToasts().get(trigger.elementId());
                case VALUE_INCREMENTED -> toastDef = data.valueToasts().get(trigger.elementId());
                case NODE_UNLOCKED -> toastDef = data.nodeToasts().get(trigger.elementId());
                case NODE_STAGE_COMPLETED -> toastDef = data.stageToasts().get(trigger.elementId());
            }

            if (toastDef == null) {
                continue;
            }

            // Build title with args
            List<Component> args = new ArrayList<>(toastDef.titleArgs());
            if (trigger.type() == ResearchToastTrigger.ToastTriggerType.VALUE_INCREMENTED) {
                args.add(Component.literal(String.valueOf(trigger.currentValue())));
            }

            Component title = Component.translatable(toastDef.title().toString(), args.toArray());

            // Resolve description
            Identifier descId = toastDef.description() != null
                    ? toastDef.description()
                    : defaultDescriptionId(trigger.type());

            Component description = Component.translatable(descId.toString());

            var toast = new ResearchToast(description, title, toastDef.icon(), trigger.type(), trigger.elementId(), trigger.currentValue());
            toastManager.addToast(toast);
        }
    }

    private static Identifier defaultDescriptionId(ResearchToastTrigger.ToastTriggerType type) {
        switch (type) {
            case FACT_GRANTED -> {
                return Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "research.fact");
            }
            case VALUE_INCREMENTED -> {
                return Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "research.value");
            }
            case NODE_UNLOCKED -> {
                return Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "research.node");
            }
            case NODE_STAGE_COMPLETED -> {
                return Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "research.stage");
            }
            default -> {
                return Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "research");
            }
        }
    }
}
