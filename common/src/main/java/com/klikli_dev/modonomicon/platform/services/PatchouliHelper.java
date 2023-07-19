/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.platform.services;

import net.minecraft.resources.ResourceLocation;

/**
 * We need that as a service because patchouli does not publish a common module, so we have to access the api in each loader.
 */
public interface PatchouliHelper {
    void openEntry(ResourceLocation book, ResourceLocation entry, int page);
}
