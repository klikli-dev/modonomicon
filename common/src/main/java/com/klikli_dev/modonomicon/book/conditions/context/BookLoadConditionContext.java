/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.conditions.context;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

/**
 * Context for evaluating a condition at book load time, where no player or level is available.
 *
 * @param bookId     the book the element belongs to
 * @param elementId  the category, entry or page the condition is attached to
 * @param registries the registries available during datapack loading
 */
public record BookLoadConditionContext(Identifier bookId, Identifier elementId, HolderLookup.Provider registries) {
}
