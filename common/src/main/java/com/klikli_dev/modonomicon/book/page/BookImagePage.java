/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.ImageDisplayMode;
import com.klikli_dev.modonomicon.book.ImageScaleMode;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookImagePage extends BookPage implements BookPageWithSplit {
    public static final Identifier ID = Modonomicon.loc("image");
    private static final Codec<List<Identifier>> IMAGE_LIST_CODEC = Codec.list(Identifier.CODEC);
    public static final MapCodec<BookImagePage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookTextHolder.CODEC.fieldOf("title").forGetter(BookImagePage::getTitle),
            BookTextHolder.CODEC.fieldOf("text").forGetter(BookImagePage::getText),
            IMAGE_LIST_CODEC.fieldOf("images").forGetter(BookImagePage::imagesAsList),
            Codec.BOOL.optionalFieldOf("border", false).forGetter(BookImagePage::hasBorder),
            Codec.BOOL.optionalFieldOf("use_legacy_rendering", false).forGetter(BookImagePage::useLegacyRendering),
            ImageDisplayMode.CODEC.optionalFieldOf("display_mode", ImageDisplayMode.DEFAULT).forGetter(BookImagePage::getDisplayMode),
            ImageScaleMode.CODEC.optionalFieldOf("image_scale_mode", ImageScaleMode.SCALE_TO_FIT).forGetter(BookImagePage::getImageScaleMode),
            Codec.BOOL.optionalFieldOf("auto_scale").forGetter(page -> Optional.ofNullable(page.getAutoScaleOverride())),
            Codec.BOOL.optionalFieldOf("allow_page_split").forGetter(page -> Optional.ofNullable(page.getAllowPageSplitOverride())),
            Codec.STRING.fieldOf("id").forGetter(BookPage::getId),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
    ).apply(instance, (title, text, images, border, useLegacyRendering, displayMode, imageScaleMode, autoScale, allowPageSplit, id, condition) -> new BookImagePage(title, text, images.toArray(Identifier[]::new), border, useLegacyRendering, displayMode, imageScaleMode, autoScale.orElse(null), allowPageSplit.orElse(null), id, condition)));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookImagePage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookImagePage::getTitle,
            BookTextHolder.STREAM_CODEC, BookImagePage::getText,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), BookImagePage::imagesAsList,
            ByteBufCodecs.BOOL, BookImagePage::hasBorder,
            ByteBufCodecs.BOOL, BookImagePage::useLegacyRendering,
            ImageDisplayMode.STREAM_CODEC, BookImagePage::getDisplayMode,
            ImageScaleMode.STREAM_CODEC, BookImagePage::getImageScaleMode,
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), page -> Optional.ofNullable(page.getAutoScaleOverride()),
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), page -> Optional.ofNullable(page.getAllowPageSplitOverride()),
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            (title, text, images, border, useLegacyRendering, displayMode, imageScaleMode, autoScale, allowPageSplit, id, condition) -> new BookImagePage(title, text, images.toArray(Identifier[]::new), border, useLegacyRendering, displayMode, imageScaleMode, autoScale.orElse(null), allowPageSplit.orElse(null), id, condition)
    );
    protected BookTextHolder title;
    protected BookTextHolder text;
    protected Identifier[] images;
    protected boolean border;
    protected boolean useLegacyRendering;
    protected ImageDisplayMode displayMode;
    protected ImageScaleMode imageScaleMode;
    protected Boolean autoScale;
    protected Boolean allowPageSplit;

    public BookImagePage(BookTextHolder title, BookTextHolder text, Identifier[] images, boolean border, boolean useLegacyRendering, ImageDisplayMode displayMode, ImageScaleMode imageScaleMode, String id, BookCondition condition) {
        this(title, text, images, border, useLegacyRendering, displayMode, imageScaleMode, null, null, id, condition);
    }

    public BookImagePage(BookTextHolder title, BookTextHolder text, Identifier[] images, boolean border, boolean useLegacyRendering, ImageDisplayMode displayMode, ImageScaleMode imageScaleMode, Boolean autoScale, Boolean allowPageSplit, String id, BookCondition condition) {
        super(id, condition);
        this.title = title;
        this.text = text;
        this.images = images;
        this.border = border;
        this.useLegacyRendering = useLegacyRendering;
        this.displayMode = displayMode;
        this.imageScaleMode = imageScaleMode;
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

    public Identifier[] getImages() {
        return this.images;
    }

    public List<Identifier> imagesAsList() {
        return Arrays.asList(this.images);
    }

    public boolean hasBorder() {
        return this.border;
    }

    public boolean useLegacyRendering() {
        return this.useLegacyRendering;
    }

    public ImageDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public ImageScaleMode getImageScaleMode() {
        return this.imageScaleMode;
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
        return BookPageTypeRegistry.IMAGE;
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.title.hasComponent()) {
            this.title = new BookTextHolder(Component.translatable(this.title.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().themeData().palette().defaultTitleColor())));
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
