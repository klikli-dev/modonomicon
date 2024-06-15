// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import net.minecraft.world.item.Items;

/**
 * This is a one-size-fits all category provider for leaflets.
 * There is no need for leaflets to actually implement category providers, they all use this "dummy one"
 */
class LeafletCategoryProvider extends CategoryProvider{
    public static final String ID = "leaflet";

    public LeafletCategoryProvider(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[0];
    }

    @Override
    protected void generateEntries() {
        //entries are generated in the leaflet subprovider instead.
    }

    @Override
    protected String categoryName() {
        return "Leaflet";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    protected String categoryId() {
        return ID;
    }
}
