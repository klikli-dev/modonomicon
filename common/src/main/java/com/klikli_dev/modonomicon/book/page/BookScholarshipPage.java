/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
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
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Shows the research requirements of a stage. Without a stage, the next stage of the entry research is shown.
 * The title and text are optional, the requirement block is titled by the stage if no title is given.
 */
public class BookScholarshipPage extends BookPage {
    public static final Identifier ID = Modonomicon.loc("scholarship");

    public static final MapCodec<BookScholarshipPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookTextHolder.CODEC.optionalFieldOf("title").forGetter(page -> Optional.ofNullable(page.title)),
            BookTextHolder.CODEC.optionalFieldOf("text").forGetter(page -> Optional.ofNullable(page.text)),
            Codec.STRING.optionalFieldOf("stage").forGetter(page -> Optional.ofNullable(page.stage)),
            Codec.STRING.fieldOf("id").forGetter(BookPage::getId),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition),
            BookPage.ASSOCIATED_ITEMS_CODEC.optionalFieldOf("associated_items", List.of()).forGetter(BookPage::getAssociatedItems)
    ).apply(instance, (title, text, stage, id, condition, associatedItems) -> new BookScholarshipPage(title.orElse(null), text.orElse(null), stage.orElse(null), id, condition, associatedItems)));

    public static final StreamCodec<RegistryFriendlyByteBuf, BookScholarshipPage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(BookTextHolder.STREAM_CODEC), page -> Optional.ofNullable(page.title),
            ByteBufCodecs.optional(BookTextHolder.STREAM_CODEC), page -> Optional.ofNullable(page.text),
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), page -> Optional.ofNullable(page.stage),
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookPage.ASSOCIATED_ITEMS_STREAM_CODEC, BookPage::getAssociatedItems,
            (title, text, stage, id, condition, associatedItems) -> new BookScholarshipPage(title.orElse(null), text.orElse(null), stage.orElse(null), id, condition, associatedItems)
    );

    @Nullable
    protected BookTextHolder title;
    @Nullable
    protected BookTextHolder text;
    @Nullable
    protected String stage;

    public BookScholarshipPage(@Nullable BookTextHolder title, @Nullable BookTextHolder text, @Nullable String stage, String id, BookCondition condition, List<Either<ItemStackTemplate, Ingredient>> associatedItems) {
        super(id, condition, associatedItems);
        this.title = title;
        this.text = text;
        this.stage = stage;
    }

    @Nullable
    public BookTextHolder getTitle() {
        return this.title;
    }

    public boolean hasTitle() {
        return this.title != null && !this.title.isEmpty();
    }

    @Nullable
    public BookTextHolder getText() {
        return this.text;
    }

    public boolean hasText() {
        return this.text != null && !this.text.isEmpty();
    }

    /**
     * The research stage id as written in the page, or null for the next stage of the entry research.
     */
    @Nullable
    public String getStage() {
        return this.stage;
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.SCHOLARSHIP;
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (this.title != null && !this.title.hasComponent()) {
            this.title = new BookTextHolder(Component.translatable(this.title.getKey())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getBook().themeData().palette().defaultTitleColor())));
        }
        if (this.text != null && !this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return (this.title != null && this.title.getString().toLowerCase().contains(query))
                || (this.text != null && this.text.getString().toLowerCase().contains(query));
    }
}
