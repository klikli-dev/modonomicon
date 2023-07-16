/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.resources.ResourceLocation;

public class ModonomiconPatchouliIntegration {

    public static void openEntry(ResourceLocation book, ResourceLocation entry, int page) {
        if (Services.PLATFORM.isModLoaded("patchouli")) {
            PatchouliHelper.openEntry(book, entry, page);
        } else {
            Modonomicon.LOG.warn("Attempted to open patchouli entry {} in book {} without patchouli installed!", entry, book);
        }
    }

    public static class PatchouliHelper {
        public static void openEntry(ResourceLocation book, ResourceLocation entry, int page) {
            //TODO: Enable patchouli
//            PatchouliAPI.get().openBookEntry(book, entry, page);
        }
    }
}
