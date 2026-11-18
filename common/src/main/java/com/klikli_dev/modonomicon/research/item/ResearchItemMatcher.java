/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.item;

import com.klikli_dev.modonomicon.book.associated.AssociatedItemMatcher;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

/**
 * Matches an item stack either by an exact item template (item plus component subset, see {@link AssociatedItemMatcher})
 * or by an item tag.
 *
 * <p>JSON shape: a plain item stack template object such as {@code {"id": "minecraft:stick"}} matches that item,
 * an object with a {@code tag} key such as {@code {"tag": "c:ingots"}} matches any item in that tag.
 *
 * @param value the template or tag to match against
 */
public record ResearchItemMatcher(Either<ItemStackTemplate, TagKey<Item>> value) {

    private static final Codec<TagKey<Item>> TAG_FIELD_CODEC = TagKey.codec(Registries.ITEM).fieldOf("tag").codec();

    public static final Codec<ResearchItemMatcher> CODEC = Codec.either(ItemStackTemplate.CODEC, TAG_FIELD_CODEC)
            .xmap(ResearchItemMatcher::new, ResearchItemMatcher::value);

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchItemMatcher> STREAM_CODEC = ByteBufCodecs.either(
                    ItemStackTemplate.STREAM_CODEC,
                    TagKey.streamCodec(Registries.ITEM).cast()
            )
            .map(ResearchItemMatcher::new, ResearchItemMatcher::value);

    /**
     * Creates a matcher for the given item stack template (item plus component subset).
     */
    public static ResearchItemMatcher of(ItemStackTemplate template) {
        return new ResearchItemMatcher(Either.left(template));
    }

    /**
     * Creates a matcher for the item of the given item-like, without components.
     */
    public static ResearchItemMatcher of(ItemLike item) {
        return of(new ItemStackTemplate(item.asItem()));
    }

    /**
     * Creates a matcher for any item in the given tag.
     */
    public static ResearchItemMatcher tag(TagKey<Item> tag) {
        return new ResearchItemMatcher(Either.right(tag));
    }

    /**
     * Returns true if the given stack is matched by this matcher.
     */
    public boolean matches(ItemStack stack) {
        return this.value.map(
                template -> AssociatedItemMatcher.matches(template, stack),
                tag -> stack.is(tag)
        );
    }

    /**
     * Returns the item id for template matchers, or null for tag matchers.
     * Used as a fast-path index key; tag matchers must not be indexed by item.
     */
    public @Nullable Identifier itemId() {
        return this.value.left()
                .flatMap(template -> template.item().unwrapKey())
                .map(ResourceKey::identifier)
                .orElse(null);
    }

    /**
     * Returns a stack suitable for displaying this matcher as an icon.
     * For tags this is the first item of the tag, or an empty stack if the tag has no items.
     */
    public ItemStack displayStack() {
        return this.value.map(
                ItemStackTemplate::create,
                tag -> {
                    for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                        return new ItemStack(holder);
                    }
                    return ItemStack.EMPTY;
                }
        );
    }

    /**
     * Returns a display name for this matcher. Templates use the item's name, tags use {@code #namespace:path}.
     */
    public Component displayName() {
        return this.value.map(
                template -> template.create().getHoverName(),
                tag -> Component.literal("#" + tag.location())
        );
    }
}
