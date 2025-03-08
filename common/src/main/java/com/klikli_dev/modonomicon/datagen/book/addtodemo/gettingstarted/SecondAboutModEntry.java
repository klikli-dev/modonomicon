// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.datagen.book.addtodemo.gettingstarted;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class SecondAboutModEntry extends EntryProvider {

    public static final String ENTRY_ID = "second_about_mod";

    public SecondAboutModEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("about", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Art of Alchemy");
        this.pageText("""
                Welcome, dear reader, to Theurgy, a mod that explores the ancient and revered art of classical alchemy. As you embark on your journey through the noble art of transformation, you will be equipped with divination rods to make finding resources in the world easier.
                """);

        this.page("about2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Art of Alchemy");
        this.pageText("""
                Through diligent study and practice, you will then learn to use alchemical devices to refine, replicate, and transform resources into new and useful materials. Along the way, you will have the opportunity to craft alchemical devices and equipment to aid you in your endeavors.
                """);

        this.page("about3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Art of Alchemy");
        this.pageText("""
                As a final note, alchemists are guided by reason and logic, not superstition or magic. Our experiments are based on careful observation, meticulous record-keeping, and rigorous testing. We do not claim to possess supernatural powers, but rather seek to harness the natural forces of the world around us to achieve our goals.
                """);

        this.page("features", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Features");
        this.pageText("""
                - Divination rods to find ores
                - Ore refining (= more ingots per ore/raw metal)
                - Item transformation (convert items into other items)
                """);

        this.page("features2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Features");
        this.pageText("""
                - Future: Item Transportation
                - Future: Weapons and Equipment
                - Future: Devices to assist in common tasks
                """);
    }

    @Override
    protected String entryName() {
        return "The Art of Alchemy";
    }

    @Override
    protected String entryDescription() {
        return "About this Mod";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.QUARTZ);
    }

    @Override
    protected String entryId() {
        return ENTRY_ID;
    }
}