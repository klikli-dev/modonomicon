/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Condition;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Tooltips;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.bookstate.BookUnlockStateManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class BookEntryUnlockedCondition extends BookCondition {

    protected ResourceLocation entryId;

    public BookEntryUnlockedCondition(Component tooltip, ResourceLocation entryId) {
        super(tooltip);
        this.entryId = entryId;
    }

    public static BookEntryUnlockedCondition fromJson(ResourceLocation conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        var entryPath = GsonHelper.getAsString(json, "entry_id");
        var entryId = entryPath.contains(":") ?
                ResourceLocation.parse(entryPath) :
                ResourceLocation.fromNamespaceAndPath(conditionParentId.getNamespace(), entryPath);
        var tooltip = tooltipFromJson(json, provider);

        return new BookEntryUnlockedCondition(tooltip, entryId);
    }

    public static BookEntryUnlockedCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? ComponentSerialization.STREAM_CODEC.decode(buffer) : null;
        var entryId = buffer.readResourceLocation();
        return new BookEntryUnlockedCondition(tooltip, entryId);
    }

    @Override
    public ResourceLocation getType() {
        return Condition.ENTRY_UNLOCKED;
    }

    @Override
    public boolean requiresMultiPassUnlockTest() {
        return true;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            ComponentSerialization.STREAM_CODEC.encode(buffer, this.tooltip);
        }
        buffer.writeResourceLocation(this.entryId);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (context == null || context.getBook() == null) {
            //Can happen if the book was not (yet) built, e.g. after /reload before books are rebuilt,
            //or if book loading failed. Never crash a server tick over this, fail closed and log.
            Modonomicon.LOG.error("BookEntryUnlockedCondition tested with null book for entry '{}'. The book was likely not built yet (e.g. after /reload) or failed to load. Returning false to avoid a crash.", this.entryId);
            return false;
        }
        var entry = context.getBook().getEntry(this.entryId);
        if (entry == null) {
            //Do not throw here: this runs during server ticks and would crash the server.
            //Instead record a book error so the book shows an error screen, log, and fail closed.
            var message = "Entry with id " + this.entryId + " not found in book " + context.getBook().getId() + " for BookEntryUnlockedCondition. This happened while trying to unlock " + context;
            Modonomicon.LOG.error(message);
            BookErrorManager.get().error(context.getBook().getId(), message);
            return false;
        }
        return BookUnlockStateManager.get().isUnlockedFor(player, entry);
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        if (this.tooltip == null && context instanceof BookConditionEntryContext entryContext
                && entryContext.getBook() != null && entryContext.getBook().getEntry(this.entryId) != null) {
            this.tooltip = Component.translatable(Tooltips.CONDITION_ENTRY_UNLOCKED, Component.translatable(entryContext.getBook().getEntry(this.entryId).getName()));
        }
        return super.getTooltip(player, context);
    }
}
