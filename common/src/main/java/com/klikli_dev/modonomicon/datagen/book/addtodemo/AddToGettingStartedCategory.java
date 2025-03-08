// SPDX-FileCopyrightText: 2022 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.addtodemo;

import com.klikli_dev.modonomicon.api.datagen.AddToBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.AddToCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.datagen.book.addtodemo.gettingstarted.SecondAboutModEntry;
import net.minecraft.resources.ResourceLocation;


public class AddToGettingStartedCategory extends AddToCategoryProvider {

    public static final String CATEGORY_ID = "getting_started";

    public AddToGettingStartedCategory(AddToBookSubProvider parent) {
        super(parent);
    }


    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                //The map can, but does not have to be identical to the one of the target book.
                //in the end it is just used to identify target coordinates, so copying the target one helps
                //in this case I copied from theurgy and just added "b" in a place that I liked
                "___________________________________",
                "__________________ḍ___ď_ḑ_ḓ________",
                "___________________________________",
                "________________d___ḋ______________",
                "___________________________________",
                "________b___ç_____đ___ɖ_ᶑ__________",
                "___________________________________",
                "__________i_a_________c___ṛ_ŕ______",
                "___________________________________",
                "______________s_____ṟ_n_r_ȓ___ŗ_ʀ_ȑ",
                "___________________________________",
                "______________o_____________ř______",
                "___________________________________",
                "______________ó___________ț_ẗ______",
                "___________________________________",
                "____________ő_ò___ǒ_____ť_ţ_ƭ_ʈ_ṫ_ṭ",
                "___________________________________",
                "____________ö___ô_ơ_______ê_ě______",
                "___________________________________",
                "______________õ_________é_è_ë_ē_ė_ę",
                "___________________________________",
                "__________________Õ_____ƒ__________"
        };
    }


    @Override
    protected void generateEntries() {
        var aboutModEntry = new SecondAboutModEntry(this).generate('b');
        aboutModEntry.withParent(BookEntryParentModel.create(ResourceLocation.fromNamespaceAndPath("theurgy", "getting_started/intro")));
    }

    @Override
    public String targetCategoryId() {
        return CATEGORY_ID;
    }
}
