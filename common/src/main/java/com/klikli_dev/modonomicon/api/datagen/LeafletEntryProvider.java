// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public abstract class LeafletEntryProvider extends EntryProvider {
    public static final String ID = "leaflet";

    public LeafletEntryProvider(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected String entryName() {
        //we assume people don't mess with leaflet provider structure
        //If you do and you end up here with an error, just override this method and return an entry name safely :)
        var book = (LeafletSubProvider) this.parent.parent;
        return book.bookName();
    }

    @Override
    protected String entryDescription() {
        return ""; //Irrelevant, because it will never be rendered
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
