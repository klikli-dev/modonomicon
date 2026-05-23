/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class BookMultiblockPage extends BookPage {
    public static final Identifier ID = Modonomicon.loc("multiblock");
    public static final MapCodec<BookMultiblockPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookTextHolder.CODEC.fieldOf("multiblock_name").forGetter(BookMultiblockPage::getMultiblockName),
            BookTextHolder.CODEC.fieldOf("text").forGetter(BookMultiblockPage::getText),
            Identifier.CODEC.fieldOf("multiblock_id").forGetter(BookMultiblockPage::getMultiblockId),
            Codec.BOOL.optionalFieldOf("show_visualize_button", false).forGetter(BookMultiblockPage::showVisualizeButton),
            Codec.STRING.optionalFieldOf("anchor", "").forGetter(BookPage::getAnchor),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
    ).apply(instance, BookMultiblockPage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookMultiblockPage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookMultiblockPage::getMultiblockName,
            BookTextHolder.STREAM_CODEC, BookMultiblockPage::getText,
            Identifier.STREAM_CODEC, BookMultiblockPage::getMultiblockId,
            ByteBufCodecs.BOOL, BookMultiblockPage::showVisualizeButton,
            ByteBufCodecs.STRING_UTF8, BookPage::getAnchor,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookMultiblockPage::new
    );

    protected BookTextHolder multiblockName;
    protected BookTextHolder text;
    protected boolean showVisualizeButton;
    protected Identifier multiblockId;

    protected Multiblock multiblock;

    public BookMultiblockPage(BookTextHolder multiblockName, BookTextHolder text, Identifier multiblockId, boolean showVisualizeButton, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.multiblockName = multiblockName;
        this.text = text;
        this.multiblockId = multiblockId;
        this.showVisualizeButton = showVisualizeButton;
    }

    public boolean showVisualizeButton() {
        return this.showVisualizeButton;
    }

    public Multiblock getMultiblock() {
        return this.multiblock;
    }

    public BookTextHolder getMultiblockName() {
        return this.multiblockName;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    public Identifier getMultiblockId() {
        return this.multiblockId;
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.MULTIBLOCK;
    }

    @Override
    public void build(Level level, BookContentEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        this.multiblock = MultiblockDataManager.get().getMultiblock(this.multiblockId);

        if (this.multiblock == null) {
            throw new IllegalArgumentException("Invalid multiblock id " + this.multiblockId);
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.multiblockName.hasComponent()) {
            this.multiblockName = new BookTextHolder(Component.translatable(this.multiblockName.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getCategory().getBook().themeData().palette().defaultTitleColor())));
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return this.multiblockName.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
