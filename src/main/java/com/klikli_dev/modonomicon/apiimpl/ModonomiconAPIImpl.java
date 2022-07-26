/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.apiimpl;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.datagen.BookLangHelper;
import com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import net.minecraft.resources.ResourceLocation;

public class ModonomiconAPIImpl implements ModonomiconAPI {
    public boolean isStub() {
        return false;
    }

    @Override
    public BookLangHelper getLangHelper(String modid) {
        return new BookLangHelper(modid);
    }

    @Override
    public EntryLocationHelper getEntryLocationHelper() {
        return new EntryLocationHelper();
    }

    @Override
    public Multiblock getMultiblock(ResourceLocation id) {
        return MultiblockDataManager.get().getMultiblock(id);
    }
}
