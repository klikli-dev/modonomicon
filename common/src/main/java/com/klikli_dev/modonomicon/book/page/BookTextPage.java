/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BookTextPage extends BookPage implements BookPageWithSplit {
    public static final Identifier ID = Modonomicon.loc("text");
    public static final MapCodec<BookTextPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookTextHolder.CODEC.fieldOf("title").forGetter(BookTextPage::getTitle),
            BookTextHolder.CODEC.fieldOf("text").forGetter(BookTextPage::getText),
            Codec.BOOL.optionalFieldOf("use_markdown_in_title", false).forGetter(BookTextPage::useMarkdownInTitle),
            Codec.BOOL.optionalFieldOf("show_title_separator", false).forGetter(BookTextPage::showTitleSeparator),
            Codec.BOOL.optionalFieldOf("auto_scale").forGetter(page -> Optional.ofNullable(page.getAutoScaleOverride())),
            Codec.BOOL.optionalFieldOf("allow_page_split").forGetter(page -> Optional.ofNullable(page.getAllowPageSplitOverride())),
            Codec.STRING.fieldOf("id").forGetter(BookPage::getId),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
    ).apply(instance, (title, text, useMarkdownInTitle, showTitleSeparator, autoScale, allowPageSplit, id, condition) -> new BookTextPage(title, text, useMarkdownInTitle, showTitleSeparator, autoScale.orElse(null), allowPageSplit.orElse(null), id, condition)));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookTextPage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookTextPage::getTitle,
            BookTextHolder.STREAM_CODEC, BookTextPage::getText,
            ByteBufCodecs.BOOL, BookTextPage::useMarkdownInTitle,
            ByteBufCodecs.BOOL, BookTextPage::showTitleSeparator,
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), page -> Optional.ofNullable(page.getAutoScaleOverride()),
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), page -> Optional.ofNullable(page.getAllowPageSplitOverride()),
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            (title, text, useMarkdownInTitle, showTitleSeparator, autoScale, allowPageSplit, id, condition) -> new BookTextPage(title, text, useMarkdownInTitle, showTitleSeparator, autoScale.orElse(null), allowPageSplit.orElse(null), id, condition)
    );
    protected BookTextHolder title;
    protected boolean useMarkdownInTitle;
    protected boolean showTitleSeparator;
    protected BookTextHolder text;
    protected Boolean autoScale;
    protected Boolean allowPageSplit;

    public BookTextPage(BookTextHolder title, BookTextHolder text, boolean useMarkdownInTitle, boolean showTitleSeparator, String id, BookCondition condition) {
        this(title, text, useMarkdownInTitle, showTitleSeparator, null, null, id, condition);
    }

    public BookTextPage(BookTextHolder title, BookTextHolder text, boolean useMarkdownInTitle, boolean showTitleSeparator, Boolean autoScale, Boolean allowPageSplit, String id, BookCondition condition) {
        super(id, condition);
        this.title = title;
        this.text = text;
        this.useMarkdownInTitle = useMarkdownInTitle;
        this.showTitleSeparator = showTitleSeparator;
        this.autoScale = autoScale;
        this.allowPageSplit = allowPageSplit;
    }

    @Override
    public Boolean getAutoScaleOverride() {
        return this.autoScale;
    }

    @Override
    public Boolean getAllowPageSplitOverride() {
        return this.allowPageSplit;
    }

    public boolean useMarkdownInTitle() {
        return this.useMarkdownInTitle;
    }

    public boolean showTitleSeparator() {
        return this.showTitleSeparator;
    }

    public BookTextHolder getTitle() {
        return this.title;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    public boolean hasTitle() {
        return !this.title.isEmpty();
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.TEXT;
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.title.hasComponent()) {
            if (this.useMarkdownInTitle) {
                this.title = new RenderedBookTextHolder(this.title, textRenderer.render(this.title.getString()));
            } else {
                this.title = new BookTextHolder(Component.translatable(this.title.getKey())
                        .withStyle(Style.EMPTY
                                .withBold(true)
                                .withColor(this.getParentEntry().getCategory().getBook().themeData().palette().defaultTitleColor())));
            }
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }
    @Override
    public boolean matchesQuery(String query, Level level) {
        return this.title.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
