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
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import com.klikli_dev.modonomicon.util.EntityUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class BookEntityPage extends BookPage {
    public static final Identifier ID = Modonomicon.loc("entity");
    public static final MapCodec<BookEntityPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BookTextHolder.CODEC.fieldOf("entity_name").forGetter(BookEntityPage::getEntityName),
            BookTextHolder.CODEC.fieldOf("text").forGetter(BookEntityPage::getText),
            Codec.STRING.fieldOf("entity_id").forGetter(BookEntityPage::getEntityId),
            Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(BookEntityPage::getScale),
            Codec.FLOAT.optionalFieldOf("offset", 0f).forGetter(BookEntityPage::getOffset),
            Codec.BOOL.optionalFieldOf("rotate", true).forGetter(BookEntityPage::doesRotate),
            Codec.FLOAT.optionalFieldOf("default_rotation", -45f).forGetter(BookEntityPage::getDefaultRotation),
            Codec.STRING.optionalFieldOf("anchor", "").forGetter(BookPage::getAnchor),
            BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
    ).apply(instance, BookEntityPage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntityPage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookEntityPage::getEntityName,
            BookTextHolder.STREAM_CODEC, BookEntityPage::getText,
            ByteBufCodecs.STRING_UTF8, BookEntityPage::getEntityId,
            ByteBufCodecs.FLOAT, BookEntityPage::getScale,
            ByteBufCodecs.FLOAT, BookEntityPage::getOffset,
            ByteBufCodecs.BOOL, BookEntityPage::doesRotate,
            ByteBufCodecs.FLOAT, BookEntityPage::getDefaultRotation,
            ByteBufCodecs.STRING_UTF8, BookPage::getAnchor,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookEntityPage::new
    );

    protected BookTextHolder entityName;
    protected BookTextHolder text;

    //is string, because we allow appending nbt 
    protected String entityId;
    protected float scale = 1.0f;
    protected float offset = 0f;
    protected boolean rotate = true;
    protected float defaultRotation = -45f;


    public BookEntityPage(BookTextHolder entityName, BookTextHolder text, String entityId, float scale, float offset, boolean rotate, float defaultRotation, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.entityName = entityName;
        this.text = text;
        this.entityId = entityId;
        this.scale = scale;
        this.offset = offset;
        this.rotate = rotate;
        this.defaultRotation = defaultRotation;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public float getScale() {
        return this.scale;
    }

    public float getOffset() {
        return this.offset;
    }

    public boolean doesRotate() {
        return this.rotate;
    }

    public float getDefaultRotation() {
        return this.defaultRotation;
    }

    public BookTextHolder getEntityName() {
        return this.entityName;
    }

    public BookTextHolder getText() {
        return this.text;
    }

    @Override
    public BookPageType<?> type() {
        return BookPageTypeRegistry.ENTITY;
    }

    @Override
    public void build(Level level, BookContentEntry parentEntry, int pageNum) {
        super.build(level, parentEntry, pageNum);

        if (this.entityName.isEmpty()) {
            //use entity name if we don't have a custom title
            this.entityName = new BookTextHolder(Component.translatable(EntityUtil.getEntityName(this.entityId))
                    .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(this.getParentEntry().getBook().themeData().palette().defaultTitleColor())
                    ));
        }
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);

        if (!this.entityName.hasComponent()) {
            this.entityName = new BookTextHolder(Component.translatable(this.entityName.getKey())
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
        return this.entityName.getString().toLowerCase().contains(query)
                || this.text.getString().toLowerCase().contains(query);
    }
}
