package com.klikli_dev.modonomicon.integration;

import com.klikli_dev.modonomicon.platform.services.PatchouliHelper;
import net.minecraft.resources.ResourceLocation;

public class ForgePatchouliHelper implements PatchouliHelper {
    @Override
    public void openEntry(ResourceLocation book, ResourceLocation entry, int page) {
        ModonomiconPatchouliIntegration.openEntry(book, entry, page);
    }
}
