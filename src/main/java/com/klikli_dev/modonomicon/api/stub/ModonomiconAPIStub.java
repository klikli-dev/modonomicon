/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.stub;


import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.datagen.BookLangHelper;
import com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import net.minecraft.resources.ResourceLocation;

public class ModonomiconAPIStub implements ModonomiconAPI {
    private static final ModonomiconAPIStub instance = new ModonomiconAPIStub();

    private ModonomiconAPIStub() {
    }

    public static ModonomiconAPIStub get() {
        return instance;
    }

    @Override
    public boolean isStub() {
        return true;
    }

    @Override
    public BookLangHelper getLangHelper(String modid) {
        return null;
    }

    @Override
    public EntryLocationHelper getEntryLocationHelper() {
        return null;
    }

    @Override
    public Multiblock getMultiblock(ResourceLocation id) {
        return null;
    }
}
