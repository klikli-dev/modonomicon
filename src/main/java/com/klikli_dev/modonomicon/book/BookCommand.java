/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class BookCommand {
    protected ResourceLocation id;
    protected Book book;

    protected String command;
    protected int permissionLevel;
    /**
     * -1 is unlimited.
     */
    protected int maxUses;

    public BookCommand(ResourceLocation id, String command, int permissionLevel, int maxUses) {
        this.id = id;
        this.command = command;
        this.permissionLevel = permissionLevel;
        this.maxUses = maxUses;
    }

    public static BookCommand fromJson(ResourceLocation id, JsonObject json) {
        var command = GsonHelper.getAsString(json, "command");
        var permissionLevel = GsonHelper.getAsInt(json, "permission_level", ModonomiconConstants.Data.Command.DEFAULT_PERMISSION_LEVEL);
        var maxUsages = GsonHelper.getAsInt(json, "max_uses", ModonomiconConstants.Data.Command.DEFAULT_MAX_USES);

        return new BookCommand(id, command, permissionLevel, maxUsages);
    }

    public static BookCommand fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var command = buffer.readUtf();
        var permissionLevel = (int) buffer.readByte();
        var maxUsages = buffer.readVarInt();
        return new BookCommand(id, command, permissionLevel, maxUsages);
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Book book) {
        this.book = book;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.command);
        buffer.writeByte(this.permissionLevel);
        buffer.writeVarInt(this.maxUses);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Book getBook() {
        return this.book;
    }

    public String getCommand() {
        return this.command;
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public int getMaxUses() {
        return this.maxUses;
    }
}
