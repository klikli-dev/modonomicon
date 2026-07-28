/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.util.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public class BookEntryParent {
    public static final Codec<BookEntryParent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.STRICT_IDENTIFIER.fieldOf("entry").forGetter(BookEntryParent::getEntryId),
            Codec.BOOL.optionalFieldOf("draw_arrow", true).forGetter(BookEntryParent::drawArrow),
            Codec.BOOL.optionalFieldOf("line_enabled", true).forGetter(BookEntryParent::isLineEnabled),
            Codec.BOOL.optionalFieldOf("line_reversed", false).forGetter(BookEntryParent::isLineReversed)
    ).apply(instance, (entryId, drawArrow, lineEnabled, lineReversed) -> {
        var parent = new BookEntryParent(entryId);
        parent.drawArrow = drawArrow;
        parent.lineEnabled = lineEnabled;
        parent.lineReversed = lineReversed;
        return parent;
    }));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntryParent> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    protected Identifier entryId;
    protected boolean drawArrow = true;
    protected boolean lineEnabled = true;
    protected boolean lineReversed = false;

    public BookEntryParent(Identifier entry) {
        this.entryId = entry;
    }

    /**
     * Creates a new BookEntryParent from the given json object.
     * @param ownerEntryId the entry id of the entry that contains this parent information. This is the CHILD not the parent.
     * @param json the json object to read from
     */
    public static BookEntryParent fromJson(Identifier ownerEntryId, JsonObject json) {
        var parentEntryId = Codecs.parseStrictIdentifier(GsonHelper.getAsString(json, "entry"));

        var parent = new BookEntryParent(parentEntryId);
        parent.drawArrow = GsonHelper.getAsBoolean(json, "draw_arrow", parent.drawArrow);
        parent.lineEnabled = GsonHelper.getAsBoolean(json, "line_enabled", parent.lineEnabled);
        parent.lineReversed = GsonHelper.getAsBoolean(json, "line_reversed", parent.lineReversed);
        return parent;
    }

    public static BookEntryParent fromNetwork(FriendlyByteBuf buffer) {
        return STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, this);
    }

    public BookEntry getEntry() {
        throw new UnsupportedOperationException("BookEntryParent is not resolved yet.");
    }

    public Identifier getEntryId() {
        return this.entryId;
    }

    public boolean drawArrow() {
        return this.drawArrow;
    }

    public boolean isLineEnabled() {
        return this.lineEnabled;
    }

    public boolean isLineReversed() {
        return this.lineReversed;
    }
}
