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
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class BookModLoadedCondition extends BookCondition {

    protected String modId;

    public BookModLoadedCondition(Component component, String modId) {
        super(component);
        this.modId = modId;
    }

    public static BookModLoadedCondition fromJson(JsonObject json, HolderLookup.Provider provider) {
        var modId = GsonHelper.getAsString(json, "mod_id");

        //default tooltip
        var tooltip = Component.translatable(Tooltips.CONDITION_MOD_LOADED, modId);

        if (json.has("tooltip")) {
            tooltip = tooltipFromJson(json, provider);
        }

        return new BookModLoadedCondition(tooltip, modId);
    }

    public static BookModLoadedCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var modId = buffer.readUtf();
        return new BookModLoadedCondition(tooltip, modId);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.MOD_LOADED;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        buffer.writeUtf(this.modId);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        return Services.PLATFORM.isModLoaded(this.modId);
    }

    @Override
    public boolean testOnLoad() {
        return Services.PLATFORM.isModLoaded(this.modId);
    }
}
