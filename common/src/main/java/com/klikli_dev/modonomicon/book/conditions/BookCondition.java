/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookConditionJsonLoader;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.platform.Services;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class BookCondition {

    protected Component tooltip;

    public BookCondition(Component tooltip) {
        this.tooltip = tooltip;
    }

    public static MutableComponent tooltipFromJson(JsonObject json, HolderLookup.Provider provider) {
        if (json.has("tooltip")) {
            var tooltipElement = json.get("tooltip");
            if (tooltipElement.isJsonPrimitive())
                return Component.translatable(tooltipElement.getAsString());

            Component.literal("").append(ComponentSerialization.CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), tooltipElement).getOrThrow());
        }
        return null;
    }

    public static BookCondition fromJson(Identifier conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        var type = Identifier.parse(GsonHelper.getAsString(json, "type"));
        var loader = LoaderRegistry.getConditionJsonLoader(type);
        return loader.fromJson(conditionParentId, json, provider);
    }

    public static BookCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        var type = buf.readIdentifier();
        var loader = LoaderRegistry.getConditionNetworkLoader(type);
        return loader.fromNetwork(buf);
    }

    public static void toNetwork(BookCondition condition, RegistryFriendlyByteBuf buf) {
        buf.writeIdentifier(condition.getType());
        condition.toNetwork(buf);
    }

    public abstract Identifier getType();

    /**
     * Always write type before calling, ideally call {@link #toNetwork(BookCondition, RegistryFriendlyByteBuf)}
     */
    public abstract void toNetwork(RegistryFriendlyByteBuf buffer);

    /**
     * Use this to test the condition for the given player at runtime
     */
    public abstract boolean test(BookConditionContext context, Player player);

    /**
     * Use this to test the condition at load time.
     * If this returns false, the locked content will not be loaded at all.
     * Most conditions should not override this - it was added primarily for the ModLoadedCondition, because
     * content locked behind it may not be present at all if the condition is false and loading would cause errors.
     */
    public boolean testOnLoad() {
        return true;
    }

    /**
     * If true then this condition needs to be tested multiple times during BookUnlockStates#update
     * This should be true if the condition depends on ANOTHER unlock condition.
     * Usually that is the case for BookEntryUnlockedCondition, BookAndCondition and BookOrCondition.
     * The latter two because they may contain the former.
     */
    public boolean requiresMultiPassUnlockTest() {
        return false;
    }

    public List<Component> getTooltip(Player player, BookConditionContext context) {
        return this.tooltip != null ? List.of(this.tooltip) : List.of();
    }
}
