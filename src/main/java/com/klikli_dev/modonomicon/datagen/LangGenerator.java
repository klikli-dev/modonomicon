/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.datagen;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonimiconConstants;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.function.Supplier;

public abstract class LangGenerator extends LanguageProvider {
    public LangGenerator(DataGenerator generator, String locale) {
        super(generator, Modonomicon.MODID, locale);
    }

    public void addItemSuffix(Supplier<? extends Item> key, String suffix, String name) {
        this.add(key.get().getDescriptionId() + suffix, name);
    }

    protected String itemKey(String id) {
        return "item." + Modonomicon.MODID + "." + id;
    }

    protected void advancementTitle(String name, String s) {
        this.add(AdvancementsGenerator.title(name).getKey(), s);
    }

    protected void advancementDescr(String name, String s) {
        this.add(AdvancementsGenerator.descr(name).getKey(), s);
    }


    public static final class English extends LangGenerator {

        public English(DataGenerator generator) {
            super(generator, "en_us");
        }

        private void addMisc() {
            this.add(ModonimiconConstants.I18n.ITEM_GROUP, "Modonomicon");

            //buttons
            this.add("modonomicon.gui.button.previous_page", "Previous Page");
            this.add("modonomicon.gui.button.next_page", "Next Page");
            this.add("modonomicon.gui.button.exit", "Exit");

            //sounds
            this.add("modonomicon.subtitle.turn_page", "Turn Page");
        }

        private void addItems() {
            this.addItem(ItemRegistry.MODONOMICON, "Modonomicon");
        }

        private void addAdvancements() {
            this.advancementTitle("root", "Modonomicon");
            this.advancementDescr("root", "The book of all books!");
        }

        private void addBooks(){
            //TODO: convert this into a real data gen for books
            this.add("modonomicon.test_book.title", "Test Book");

            this.add("modonomicon.test.entries.test_category.test_entry.description", "Test Description");
            this.add("modonomicon.test.sections.test_category.test_entry.page0.title", "**Bold**");
            this.add("modonomicon.test.sections.test_category.test_entry.page0.text",
                    """
                    This is a **test** text.
                    We have a newline here.
                    - List item 
                    - List item 2
                    - List item 3
                    
                    And this is a super long line where we hope it will be automatically wrapped into a new line otherwise that is super-bad.      
                    """);
            this.add("modonomicon.test.sections.test_category.test_entry.page1.title", "*[#](55FF55)Colorful Italics*[#]()");
            this.add("modonomicon.test.sections.test_category.test_entry.page1.text",
                    """
                   And this is our page two.    
                    """);
            this.add("modonomicon.test.sections.test_category.test_entry.page2.title", "Page 3");
            this.add("modonomicon.test.sections.test_category.test_entry.page2.text",
                    """
                   And this is our page three.    
                    """);
        }

        protected void addTranslations() {
            this.addMisc();
            this.addItems();
            this.addAdvancements();
            this.addBooks();
        }
    }
}