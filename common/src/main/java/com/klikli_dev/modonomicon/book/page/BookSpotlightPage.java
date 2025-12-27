/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.Data.Page;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BookSpotlightPage extends BookPage {
    /**
     * A custom codec that still uses the "item" field instead of "id" for backwards comp,
     */
    public static final Codec<ItemStack> CUSTOM_ITEM_STACK_CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create((builder) -> builder.group(
                    Item.CODEC.fieldOf("item").forGetter(ItemStack::typeHolder),
                    ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
            ).apply(builder, ItemStack::new))
    );
    
    /**
     * We allow both vanilla item stack syntax and our custom syntax.
     */
    public static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(CUSTOM_ITEM_STACK_CODEC, ItemStack.CODEC));

    /**
     * We allow both ingredients and item stacks.
     */
    public static final Codec<Either<ItemStack, Ingredient>> ITEM_CODEC = Codec.lazyInitialized(() -> Codec.either(ITEM_STACK_CODEC, Ingredient.CODEC));

    public static final StreamCodec<RegistryFriendlyByteBuf, Either<ItemStack, Ingredient>> ITEM_STREAM_CODEC = new StreamCodec<>() {


        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, Either<ItemStack, Ingredient> item) {
            item.ifRight(i -> {
                buf.writeBoolean(true);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, i);
            });
            item.ifLeft(i -> {
                buf.writeBoolean(false);
                ItemStack.STREAM_CODEC.encode(buf, i);
            });
        }

        @Override
        public @NotNull Either<ItemStack, Ingredient> decode(@NotNull RegistryFriendlyByteBuf buf) {
            boolean isIngredient = buf.readBoolean();
            if (isIngredient) {
                return Either.right(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            } else {
                return Either.left(ItemStack.STREAM_CODEC.decode(buf));
            }
        }
    };

    protected BookTextHolder title;
    protected BookTextHolder text;
    protected Either<ItemStack, Ingredient> item;

    public BookSpotlightPage(BookTextHolder title, BookTextHolder text, Either<ItemStack, Ingredient> item, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.title = title;
        this.text = text;
        this.item = item;
    }

    public static BookSpotlightPage fromJson(Identifier entryId, JsonObject json, HolderLookup.Provider provider) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY, provider);
        var item = ITEM_CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json.get("item")).result().get();
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY, provider);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(entryId, json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookSpotlightPage(title, text, item, anchor, condition);
    }

    public static BookSpotlightPage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var item = ITEM_STREAM_CODEC.decode(buffer);
        var text = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookSpotlightPage(title, text, item, anchor, condition);
    }

    public Either<ItemStack, Ingredient> getItem() {
        return this.item;
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
    public Identifier getType() {
        return Page.SPOTLIGHT;
    }

    @Override
    public void build(Level level, BookContentEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        if (this.title.isEmpty()) {
            //use ingredient name if we don't have a custom title
            var item = this.item.map(i -> i, i -> i.display().resolveForFirstStack(SlotDisplayContext.fromLevel(level)));

            this.title = new BookTextHolder(((MutableComponent) item.getHoverName())
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())
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
                            .withColor(this.getParentEntry().getBook().getDefaultTitleColor())));
        }
        if (!this.text.hasComponent()) {
            this.text = new RenderedBookTextHolder(this.text, textRenderer.render(this.text.getString()));
        }
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        this.title.toNetwork(buffer);
        ITEM_STREAM_CODEC.encode(buffer, this.item);
        this.text.toNetwork(buffer);
        super.toNetwork(buffer);
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return this.title.getString().toLowerCase().contains(query)
                || this.itemStackMatchesQuery(query)
                || this.ingredientMatchesQuery(query, level)
                || this.text.getString().toLowerCase().contains(query);
    }

    protected boolean itemStackMatchesQuery(String query) {
        return this.item.mapLeft(l -> this.matchesQuery(l, query)).left().orElse(false);
    }

    protected boolean ingredientMatchesQuery(String query, Level level) {
        return this.item.mapRight(r -> r.display().resolveForStacks(SlotDisplayContext.fromLevel(level)).stream().anyMatch(i -> this.matchesQuery(i, query))).right().orElse(false);
    }

    protected boolean matchesQuery(ItemStack stack, String query) {
        return I18n.get(stack.getItem().getDescriptionId()).toLowerCase().contains(query);
    }
}
