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
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class BookModLoadedCondition extends BookCondition {

    public static final Identifier ID = Modonomicon.loc("mod_loaded");
    public static final MapCodec<BookModLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(condition -> Optional.ofNullable(condition.tooltip())),
            Codec.STRING.fieldOf("mod_id").forGetter(condition -> condition.modId)
    ).apply(instance, (tooltip, modId) -> new BookModLoadedCondition(tooltip.orElse(null), modId)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookModLoadedCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.TRUSTED_STREAM_CODEC), condition -> Optional.ofNullable(condition.tooltip()),
            ByteBufCodecs.STRING_UTF8, condition -> condition.modId,
            (tooltip, modId) -> new BookModLoadedCondition(tooltip.orElse(null), modId)
    );

    protected String modId;

    public BookModLoadedCondition(Component component, String modId) {
        super(component);
        this.modId = modId;
    }

    @Override
    public BookConditionType<?> type() {
        return BookConditionTypeRegistry.MOD_LOADED;
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
