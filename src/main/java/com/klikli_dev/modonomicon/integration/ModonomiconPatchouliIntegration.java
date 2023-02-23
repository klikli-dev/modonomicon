/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import vazkii.patchouli.api.PatchouliAPI;

public class ModonomiconPatchouliIntegration {

    public static void openEntry(ResourceLocation book, ResourceLocation entry, int page) {
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliHelper.openEntry(book, entry, page);
        } else {
            Modonomicon.LOGGER.warn("Attempted to open patchouli entry {} in book {} without patchouli installed!", entry, book);
        }
    }

    public static class PatchouliHelper {
        public static void openEntry(ResourceLocation book, ResourceLocation entry, int page) {
            PatchouliAPI.get().openBookEntry(book, entry, page);
        }
    }
}
