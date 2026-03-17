/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BookCommandModel {
    protected BookModel book;
    protected Identifier id;

    protected String command;
    protected int maxUses = ModonomiconConstants.Data.Command.DEFAULT_MAX_USES;
    protected boolean suppressOutput;

    @Nullable
    protected String failureMessage;

    @Nullable
    protected String successMessage;

    protected Set<Identifier> allowedEntries = new HashSet<>();

    protected BookCommandModel(Identifier id, String command) {
        this.id = id;
        this.command = command;
    }

    /**
     * @param id      The command ID, e.g. "modonomicon:rewards/random". The ID must be unique within the book.
     * @param command The minecraft command to execute.
     */
    public static BookCommandModel create(Identifier id, String command) {
        return new BookCommandModel(id, command);
    }

    public BookModel getBook() {
        return this.book;
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.addProperty("command", this.command);
        json.addProperty("max_uses", this.maxUses);
        json.addProperty("suppress_output", this.suppressOutput);
        if (this.failureMessage != null)
            json.addProperty("failure_message", this.failureMessage);
        if (this.successMessage != null)
            json.addProperty("success_message", this.successMessage);
        if (this.allowedEntries != null && !this.allowedEntries.isEmpty()) {
            var arr = new JsonArray();
            for (var entry : this.allowedEntries) {
                arr.add(entry.toString());
            }
            json.add("allowed_entries", arr);
        }
        return json;
    }

    public Identifier getId() {
        return this.id;
    }

    public String getCommand() {
        return this.command;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public boolean shouldSuppressOutput() {
        return this.suppressOutput;
    }

    public @Nullable String getFailureMessage() {
        return this.failureMessage;
    }

    public @Nullable String getSuccessMessage() {
        return this.successMessage;
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
     * Sets whether the command's default output should be suppressed to silence it towards the player.
     * Defaults to false.
     */
    public BookCommandModel withSuppressOutput(boolean suppressOutput) {
        this.suppressOutput = suppressOutput;
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

    /**
     * Sets the entries that are allowed to execute this command.
     * If set, only these entries will be able to execute the command.
     * If not set, all entries will be able to execute the command.
     */
    public BookCommandModel withAllowedEntries(Set<Identifier> allowedEntries) {
        this.allowedEntries = allowedEntries == null ? new HashSet<>() : new HashSet<>(allowedEntries);
        return this;
    }

    /**
     * Adds a single allowed entry by Identifier.
     */
    public BookCommandModel withAllowedEntry(Identifier entry) {
        this.allowedEntries.add(entry);
        return this;
    }

    /**
     * Adds a single allowed entry by String (converted to Identifier).
     */
    public BookCommandModel withAllowedEntry(String entry) {
        this.allowedEntries.add(Identifier.parse(entry));
        return this;
    }
}
