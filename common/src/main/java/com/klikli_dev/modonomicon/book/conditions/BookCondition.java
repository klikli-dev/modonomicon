/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.data.BookConditionType;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class BookCondition {

    private static final Identifier REMOVED_ENTRY_READ = Identifier.parse("modonomicon:entry_read");
    private static final Identifier REMOVED_ENTRY_UNLOCKED = Identifier.parse("modonomicon:entry_unlocked");
    private static final Identifier REMOVED_ADVANCEMENT = Identifier.parse("modonomicon:advancement");

    public static final Codec<BookCondition> CODEC = Codec.lazyInitialized(() -> BookConditionTypeRegistry.codec().dispatch(
            "type",
            BookCondition::type,
            BookConditionType::codec
    ));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookCondition> STREAM_CODEC = StreamCodec.recursive(codec ->
            BookConditionTypeRegistry.streamCodec()
                    .dispatch(
                            BookCondition::type,
                            BookConditionType::streamCodec
                    ));

    protected Component tooltip;

    public BookCondition(Component tooltip) {
        this.tooltip = tooltip;
    }

    public static BookCondition fromJson(Identifier conditionParentId, JsonObject json, HolderLookup.Provider provider) {
        if (json.has("type")) {
            var type = Identifier.parse(json.get("type").getAsString());
            if (type.equals(REMOVED_ENTRY_READ)) {
                throw new IllegalArgumentException("Book condition type 'modonomicon:entry_read' is no longer supported. Model this progression through explicit research hooks and research nodes instead.");
            }
            if (type.equals(REMOVED_ENTRY_UNLOCKED)) {
                throw new IllegalArgumentException("Book condition type 'modonomicon:entry_unlocked' is no longer supported. Replace it with the authoritative research node for the referenced progression milestone.");
            }
            if (type.equals(REMOVED_ADVANCEMENT)) {
                throw new IllegalArgumentException("Book condition type 'modonomicon:advancement' is no longer supported. Model this progression through explicit research hooks and research nodes instead.");
            }
        }
        return CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json)
                .getOrThrow(error -> new IllegalArgumentException("Failed to decode condition for " + conditionParentId + ": " + error));
    }

    public static BookCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return STREAM_CODEC.decode(buf);
    }

    public static void toNetwork(BookCondition condition, RegistryFriendlyByteBuf buf) {
        STREAM_CODEC.encode(buf, condition);
    }

    public abstract BookConditionType<?> type();

    /**
     * Always write type before calling, ideally call {@link #toNetwork(BookCondition, RegistryFriendlyByteBuf)}
     */
    @SuppressWarnings("unchecked")
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        ((StreamCodec<RegistryFriendlyByteBuf, BookCondition>) this.type().streamCodec().cast()).encode(buffer, this);
    }

    public Identifier getType() {
        return this.type().id();
    }

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
     * Usually that is the case for BookAndCondition and BookOrCondition.
     */
    public boolean requiresMultiPassUnlockTest() {
        return false;
    }

    public List<Component> getTooltip(Player player, BookConditionContext context) {
        return this.tooltip != null ? List.of(this.tooltip) : List.of();
    }

    public Component tooltip() {
        return this.tooltip;
    }
}
