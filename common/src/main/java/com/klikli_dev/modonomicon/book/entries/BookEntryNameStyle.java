// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.book.entries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

/**
 * Styling of the permanently rendered entry name on node screens (see {@code render_name}).
 * Nests the name-related entry fields so the entry record stays within RecordCodecBuilder's group limit.
 *
 * @param renderName where the name renders, or NONE to only show it in the hover tooltip.
 * @param showBeforeUnlock if true, a locked entry's name renders under the grey overlay. Defaults to false.
 * @param backgroundColor ARGB tint of the name background, multiplied with the theme default tint.
 * @param background background sprite override, or empty to use the theme default.
 * @param textColor ARGB color of the name text, multiplied with the theme default text color.
 */
public record BookEntryNameStyle(EntryNameRenderType renderName, boolean showBeforeUnlock,
                                 int backgroundColor, GuiSprite background, int textColor) {

    public static final BookEntryNameStyle DEFAULT = new BookEntryNameStyle(
            EntryNameRenderType.NONE, false, 0xFFFFFFFF, GuiSprite.EMPTY, 0xFFFFFFFF);

    public static final MapCodec<BookEntryNameStyle> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntryNameRenderType.CODEC.optionalFieldOf("render_name", EntryNameRenderType.NONE).forGetter(BookEntryNameStyle::renderName),
            Codec.BOOL.optionalFieldOf("show_name_before_unlock", false).forGetter(BookEntryNameStyle::showBeforeUnlock),
            ExtraCodecs.STRING_ARGB_COLOR.optionalFieldOf("name_background_color", 0xFFFFFFFF).forGetter(BookEntryNameStyle::backgroundColor),
            GuiSprite.CODEC.optionalFieldOf("name_background", GuiSprite.EMPTY).forGetter(BookEntryNameStyle::background),
            ExtraCodecs.STRING_ARGB_COLOR.optionalFieldOf("name_text_color", 0xFFFFFFFF).forGetter(BookEntryNameStyle::textColor)
    ).apply(instance, BookEntryNameStyle::new));

    public static final Codec<BookEntryNameStyle> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryNameStyle> STREAM_CODEC = StreamCodec.composite(
            EntryNameRenderType.STREAM_CODEC, BookEntryNameStyle::renderName,
            ByteBufCodecs.BOOL, BookEntryNameStyle::showBeforeUnlock,
            ByteBufCodecs.INT, BookEntryNameStyle::backgroundColor,
            GuiSprite.STREAM_CODEC, BookEntryNameStyle::background,
            ByteBufCodecs.INT, BookEntryNameStyle::textColor,
            BookEntryNameStyle::new
    );
}
