/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.platform.services.PatchouliHelper;
import net.minecraft.resources.ResourceLocation;

public class NeoPatchouliHelper implements PatchouliHelper {
    @Override
    public void openEntry(ResourceLocation book, ResourceLocation entry, int page) {
        ModonomiconPatchouliIntegration.openEntry(book, entry, page);
    }
}
