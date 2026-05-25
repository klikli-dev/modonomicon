/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryLayoutTest {

    @Test
    void resolvesLocationsById() {
        var layout = CategoryLayout.relativeEntryLayout();
        layout.entry("intro").at(6, 7);
        layout.entry("rod").rightOf("intro", 4).below(1);

        assertLocation(layout.get("intro"), 6, 7);
        assertLocation(layout.get("rod"), 10, 8);
    }

    @Test
    void resolvesLocationsByEntryProviderReference() {
        var layout = CategoryLayout.relativeEntryLayout();
        var intro = new TestEntryProvider("intro");
        var rod = new TestEntryProvider("rod");

        layout.entry(intro).at(0, 0);
        layout.entry(rod).rightOf(intro, 4).below(2);

        assertLocation(layout.get(rod), 4, 2);
    }

    @Test
    void mutatesBookEntryModelsAfterGeneration() {
        var layout = CategoryLayout.relativeEntryLayout();
        var intro = BookEntryModel.create(Identifier.parse("modonomicon:test/intro"), "book.modonomicon.test.intro").withLocation(0, 0);
        var chalk = BookEntryModel.create(Identifier.parse("modonomicon:test/chalk"), "book.modonomicon.test.chalk").withLocation(0, 0);

        layout.entry(intro).at(2, 3);
        layout.entry(chalk).rightOf(intro, 4).below(1);

        assertEquals(2, intro.getX());
        assertEquals(3, intro.getY());
        assertEquals(6, chalk.getX());
        assertEquals(4, chalk.getY());
    }

    @Test
    void supportsChainingAfterAbsolutePlacement() {
        var layout = CategoryLayout.relativeEntryLayout();
        layout.entry("intro").at(1, 1).rightOf(2).below(3);

        assertLocation(layout.get("intro"), 3, 4);
    }

    @Test
    void throwsForUnknownAnchor() {
        var layout = CategoryLayout.relativeEntryLayout();

        assertThrows(IllegalArgumentException.class, () -> layout.entry("rod").rightOf("intro", 4));
    }

    private static void assertLocation(net.minecraft.world.phys.Vec2 actual, float x, float y) {
        assertEquals(x, actual.x);
        assertEquals(y, actual.y);
    }

    private static final class TestEntryProvider extends EntryProvider {
        private final String id;

        private TestEntryProvider(String id) {
            super(new TestParentProvider());
            this.id = id;
        }

        @Override
        protected void generatePages() {
        }

        @Override
        protected String entryName() {
            return this.id;
        }

        @Override
        protected GuiSprite entryBackground() {
            return null;
        }

        @Override
        protected BookIconModel entryIcon() {
            return null;
        }

        @Override
        protected String entryId() {
            return this.id;
        }
    }

    private static final class TestParentProvider extends CategoryProviderBase {

        private TestParentProvider() {
            super(new DummyRootProvider(), "modonomicon", (key, value) -> {
            }, java.util.Map.of(), new BookContextHelper("modonomicon"), new ConditionHelper());
        }

        @Override
        public String categoryId() {
            return "test";
        }

        @Override
        public CategoryEntryMap entryMap() {
            return new CategoryEntryMap();
        }

        @Override
        public BookEntryModel add(BookEntryModel entry) {
            return entry;
        }

        @Override
        public java.util.List<BookEntryModel> add(java.util.List<BookEntryModel> entries) {
            return entries;
        }
    }

    private static final class DummyRootProvider extends ModonomiconProviderBase {

        private DummyRootProvider() {
            super("modonomicon", (key, value) -> {
            }, java.util.Map.of(), new BookContextHelper("modonomicon"), new ConditionHelper());
        }
    }
}
