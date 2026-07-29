/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.apiimpl;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.book.runtime.RuntimeBookContentBatch;
import com.klikli_dev.modonomicon.api.datagen.BookContextHelper;
import com.klikli_dev.modonomicon.api.datagen.CategoryEntryMap;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.book.runtime.RuntimeBookContentBatchImpl;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModonomiconAPIImpl implements ModonomiconAPI {
    public boolean isStub() {
        return false;
    }

    @Override
    public BookContextHelper getContextHelper(String modid) {
        return new BookContextHelper(modid);
    }

    @Override
    public CategoryEntryMap getEntryMap() {
        return new CategoryEntryMap();
    }

    @Override
    public Multiblock getMultiblock(Identifier id) {
        return MultiblockDataManager.get().getMultiblock(id);
    }

    @Override
    public RuntimeBookContentBatch openRuntimeContentBatch() {
        return new RuntimeBookContentBatchImpl();
    }

    @Override
    public @Nullable MultiblockPreviewData getCurrentPreviewMultiblock() {
        return MultiblockPreviewRenderer.getMultiblockPreviewData();
    }
}
