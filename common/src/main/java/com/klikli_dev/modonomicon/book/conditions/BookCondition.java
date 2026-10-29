/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookLoadConditionContext;
import com.klikli_dev.modonomicon.data.BookConditionJsonLoader;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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

            return Component.Serializer.fromJson(tooltipElement, provider);
        }
        return null;
    }

    /**
     * @deprecated use {@link #fromJson(ResourceLocation, JsonObject, HolderLookup.Provider)} instead.
     */
    @Deprecated
    public static BookCondition fromJson(JsonObject json, HolderLookup.Provider provider) {
        //TODO(BookPageLoading): when replacing jsonloader with bookconditionjsonloader remove the backwards compat

        //this is really just praying, if conditions are used that require the parent id this will fail
        //however, the parent will only be used if someone runs datagen.
        return fromJson(null, json, provider);
    }


    public static BookCondition fromJson(ResourceLocation conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        var type = ResourceLocation.parse(GsonHelper.getAsString(json, "type"));
        var loader = LoaderRegistry.getConditionJsonLoader(type);
        //TODO(BookPageLoading): when replacing jsonloader with bookconditionjsonloader remove the backwards compat
        if(loader instanceof BookConditionJsonLoader<? extends BookCondition> bookConditionJsonLoader) {
            return bookConditionJsonLoader.fromJson(conditionParentId, json, provider);
        } else {
            return loader.fromJson(json, provider);
        }
    }

    public static BookCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        var type = buf.readResourceLocation();
        var loader = LoaderRegistry.getConditionNetworkLoader(type);
        return loader.fromNetwork(buf);
    }

    public static void toNetwork(BookCondition condition, RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(condition.getType());
        condition.toNetwork(buf);
    }

    public abstract ResourceLocation getType();

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
     *
     * @deprecated Only kept for backwards compatibility with books that use "condition" instead of
     * "load_condition". New books should use "load_condition" with conditions that
     * {@link #supportsLoadTime() support load time evaluation}.
     */
    @Deprecated
    public boolean testOnLoad() {
        return true;
    }

    /**
     * Whether this condition can be evaluated at load time via {@link #testOnLoad(BookLoadConditionContext)},
     * i.e. without a player or level. Only such conditions may be used as "load_condition".
     */
    public boolean supportsLoadTime() {
        return false;
    }

    /**
     * Tests the condition at load time. If this returns false, the content is removed entirely, as if it was never defined.
     * Only called if {@link #supportsLoadTime()} returns true.
     */
    public boolean testOnLoad(BookLoadConditionContext context) {
        throw new UnsupportedOperationException("Condition type '" + this.getType() + "' cannot be evaluated at load time.");
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
