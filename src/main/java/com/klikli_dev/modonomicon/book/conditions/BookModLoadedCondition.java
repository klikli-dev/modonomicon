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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class BookModLoadedCondition extends BookCondition {

    protected String modId;

    public BookModLoadedCondition(Component component, String modId) {
        super(component);
        this.modId = modId;
    }

    public static BookModLoadedCondition fromJson(JsonObject json) {
        var modId = GsonHelper.getAsString(json, "mod_id");

        //default tooltip
        var tooltip = Component.translatable(Tooltips.CONDITION_MOD_LOADED,
                "mods." + modId + ".name");

        if (json.has("tooltip")) {
            tooltip = tooltipFromJson(json);
        }

        return new BookModLoadedCondition(tooltip, modId);
    }

    public static BookModLoadedCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? buffer.readComponent() : null;
        var modId = buffer.readUtf();
        return new BookModLoadedCondition(tooltip, modId);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.MOD_LOADED;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            buffer.writeComponent(this.tooltip);
        }
        buffer.writeUtf(this.modId);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return ModList.get().isLoaded(this.modId);
    }
}
