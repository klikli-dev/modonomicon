/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookLoadConditionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class BookNotCondition extends BookCondition {

    protected BookCondition child;

    public BookNotCondition(Component component, BookCondition child) {
        super(component);
        if (child == null)
            throw new IllegalArgumentException("NotCondition must have a child.");
        this.child = child;
    }

    public static BookNotCondition fromJson(ResourceLocation conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        if (!json.has("child") || !json.get("child").isJsonObject())
            throw new JsonSyntaxException("NotCondition must have a child that is a JsonObject.");
        var child = BookCondition.fromJson(conditionParentId, json.getAsJsonObject("child"), provider);
        var tooltip = tooltipFromJson(json, provider);
        return new BookNotCondition(tooltip, child);
    }

    public static BookNotCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var child = BookCondition.fromNetwork(buffer);
        return new BookNotCondition(tooltip, child);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.NOT;
    }

    @Override
    public boolean requiresMultiPassUnlockTest() {
        return this.child.requiresMultiPassUnlockTest();
    }

    public BookCondition child() {
        return this.child;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        BookCondition.toNetwork(this.child, buffer);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return !this.child.test(context, player);
    }

    @Override
    public boolean testOnLoad() {
        // Legacy load time check: always load and decide at runtime. Naively returning
        // !child.testOnLoad() would prevent loading for the main use case: most conditions
        // (e.g. advancement) return true on load to defer the decision to runtime, and inverting
        // that to false would drop the content entirely. Runtime test() still hides it correctly.
        return true;
    }

    @Override
    public boolean supportsLoadTime() {
        return this.child.supportsLoadTime();
    }

    @Override
    public boolean testOnLoad(BookLoadConditionContext context) {
        return !this.child.testOnLoad(context);
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        // Do not forward the child's tooltip: it describes the non-negated requirement
        // (e.g. "Requires X") which is misleading once the logic is inverted.
        // Authors can set an explicit tooltip instead.
        return super.getTooltip(player, context);
    }
}
