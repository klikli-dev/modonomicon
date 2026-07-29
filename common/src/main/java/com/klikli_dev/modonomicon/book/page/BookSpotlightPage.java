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
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

public class BookSpotlightPage extends BookPage {
    public static final Identifier ID = Modonomicon.loc("spotlight");
    public static final Codec<Either<ItemStackTemplate, Ingredient>> ITEM_CODEC = Codec.lazyInitialized(() -> Codec.either(ItemStackTemplate.CODEC, Ingredient.CODEC));
    public static final StreamCodec<RegistryFriendlyByteBuf, Either<ItemStackTemplate, Ingredient>> ITEM_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(ITEM_CODEC);
    public static final MapCodec<BookSpotlightPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookTextHolder.CODEC.fieldOf("title").forGetter(BookSpotlightPage::getTitle),
            BookTextHolder.CODEC.fieldOf("text").forGetter(BookSpotlightPage::getText),
            ITEM_CODEC.fieldOf("item").forGetter(BookSpotlightPage::getItem),
            Codec.STRING.fieldOf("id").forGetter(BookPage::getId),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
    ).apply(instance, BookSpotlightPage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookSpotlightPage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookSpotlightPage::getTitle,
            BookTextHolder.STREAM_CODEC, BookSpotlightPage::getText,
            ITEM_STREAM_CODEC, BookSpotlightPage::getItem,
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookSpotlightPage::new
    );
    protected BookTextHolder title;
    protected BookTextHolder text;
    protected Either<ItemStackTemplate, Ingredient> item;
    private ItemStack cachedItemStack;

    public BookSpotlightPage(BookTextHolder title, BookTextHolder text, Either<ItemStackTemplate, Ingredient> item, String id, BookCondition condition) {
        super(id, condition);
        this.title = title;
        this.text = text;
        this.item = item;
    }

    public Either<ItemStackTemplate, Ingredient> getItem() {
        return this.item;
    }

    public ItemStack getCachedItemStack() {
        if (this.cachedItemStack == null) {
            this.item.ifLeft(l -> this.cachedItemStack = l.create());
        }
        return this.cachedItemStack;
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
        return BookPageTypeRegistry.SPOTLIGHT;
    }

    @Override
    public void build(Level level, BookContentEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        if (this.title.isEmpty()) {
            //use ingredient name if we don't have a custom title
            var item = this.item.map(i -> i.create(), i -> i.display().resolveForFirstStack(SlotDisplayContext.fromLevel(level)));

            this.title = new BookTextHolder((item.getHoverName().copy())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().themeData().palette().defaultTitleColor())
                    ));
        }
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
                || this.itemStackMatchesQuery(query)
                || this.ingredientMatchesQuery(query, level)
                || this.text.getString().toLowerCase().contains(query);
    }

    protected boolean itemStackMatchesQuery(String query) {
        return this.item.mapLeft(l -> {
            return this.matchesQuery(this.getCachedItemStack(), query);
        }).left().orElse(false);
    }

    protected boolean ingredientMatchesQuery(String query, Level level) {
        return this.item.mapRight(r -> r.display().resolveForStacks(SlotDisplayContext.fromLevel(level)).stream().anyMatch(i -> this.matchesQuery(i, query))).right().orElse(false);
    }

    protected boolean matchesQuery(ItemStack stack, String query) {
        return I18n.get(stack.getItem().getDescriptionId()).toLowerCase().contains(query);
    }
}
