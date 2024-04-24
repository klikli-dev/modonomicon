/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BookCommandModel {
    protected BookModel book;
    protected ResourceLocation id;

    protected String command;
    protected int permissionLevel = ModonomiconConstants.Data.Command.DEFAULT_PERMISSION_LEVEL;
    protected int maxUses = ModonomiconConstants.Data.Command.DEFAULT_MAX_USES;

    @Nullable
    protected String failureMessage;

    @Nullable
    protected String successMessage;

    protected BookCommandModel(ResourceLocation id, String command) {
        this.id = id;
        this.command = command;
    }

    /**
     * @param id      The command ID, e.g. "modonomicon:rewards/random". The ID must be unique within the book.
     * @param command The minecraft command to execute.
     */
    public static BookCommandModel create(ResourceLocation id, String command) {
        return new BookCommandModel(id, command);
    }

    public BookModel getBook() {
        return this.book;
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.addProperty("command", this.command);
        json.addProperty("permission_level", this.permissionLevel);
        json.addProperty("max_uses", this.maxUses);
        if (this.failureMessage != null)
            json.addProperty("failure_message", this.failureMessage);
        if (this.successMessage != null)
            json.addProperty("success_message", this.successMessage);
        return json;
    }

    public ResourceLocation getId() {
        return this.id;
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

    public @Nullable String getFailureMessage() {
        return this.failureMessage;
    }

    public @Nullable String getSuccessMessage() {
        return this.successMessage;
    }

    /**
     * Sets the commands permission level.
     * The command will be executed with this level. Defaults to 0.
     *
     * @param permissionLevel a minecraft permission level int (0-4).
     */
    public BookCommandModel withPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
        return this;
    }

    /**
     * Sets the commands max uses.
     * The command can only be executed this many times by the same player.
     * This is useful for e.g. commands to give rewards.
     * Defaults to 1. -1 is unlimited.
     */
    public BookCommandModel withMaxUses(int maxUses) {
        this.maxUses = maxUses;
        return this;
    }

    /**
     * Sets the commands failure message.
     * If set, this message will be displayed if the command fails.
     * If not set, the default failure message will be displayed.
     * Should be a translation key.
     */
    public BookCommandModel withFailureMessage(@Nullable String failureMessage) {
        this.failureMessage = failureMessage;
        return this;
    }

    /**
     * Sets the commands success message.
     * If set, this message will be displayed if the command succeeds.
     * If not set, the no success message will be displayed.
     * Should be a translation key.
     */

    public BookCommandModel withSuccessMessage(@Nullable String successMessage) {
        this.successMessage = successMessage;
        return this;
    }
}
