/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import net.minecraft.world.phys.Vec2;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryLayout {

    private final Map<String, Vec2> entries = new LinkedHashMap<>();
    private final Map<BookEntryModel, String> modelKeys = new IdentityHashMap<>();

    /**
     * Creates a layout helper for placing category entries relative to one another in screen space.
     * <p>
     * Coordinates use the same semantics as rendered category pages: increasing {@code x} moves an entry
     * to the right, and increasing {@code y} moves it further down the screen. Consequently,
     * {@code above(...)} decreases {@code y}, while {@code below(...)} increases {@code y}.
     */
    public static CategoryLayout relativeEntryLayout() {
        return new CategoryLayout();
    }

    /**
     * Starts configuring the layout position for the entry with the given id.
     * <p>
     * Use {@link EntryPlacement#at(int, int)} to place it at an absolute screen-space position, or anchor it
     * relative to another entry via {@code rightOf}, {@code leftOf}, {@code above}, or {@code below}.
     */
    public EntryPlacement entry(String id) {
        return new EntryPlacement(id, null);
    }

    /**
     * Starts configuring the layout position for the given provider's {@link EntryProvider#entryId()}.
     */
    public EntryPlacement entry(EntryProvider provider) {
        return this.entry(provider.entryId());
    }

    /**
     * Starts configuring the layout position for an already-generated entry model.
     * <p>
     * If the layout already contains an explicit position for the model id, that position is used as the
     * starting cursor. Otherwise, the model's current {@code x}/{@code y} location is used.
     * <p>
     * This makes it possible to mutate generated entries after creation while preserving the same screen-space
     * coordinate semantics: larger {@code y} values are lower on the page.
     */
    public EntryPlacement entry(BookEntryModel model) {
        var key = model.getId().toString();
        this.modelKeys.put(model, key);
        var placement = new EntryPlacement(key, model);
        placement.cursor = this.get(model);
        placement.positioned = true;
        return placement.commit();
    }

    /**
     * Returns the explicitly configured screen-space position for the given entry id.
     * <p>
     * This method only reads positions stored in the layout itself. If no position was configured, it throws
     * so datagen fails fast instead of silently choosing a fallback.
     */
    public Vec2 get(String id) {
        var location = this.entries.get(id);
        if (location == null) {
            throw new IllegalArgumentException("Entry '" + id + "' has no configured layout position");
        }
        return location;
    }

    /**
     * Returns the explicitly configured screen-space position for the given provider's entry id.
     */
    public Vec2 get(EntryProvider provider) {
        return this.get(provider.entryId());
    }

    /**
     * Resolves the effective screen-space position for an already-generated entry model.
     * <p>
     * If the layout contains an explicit position for the model id, that configured value wins. Otherwise,
     * the model's current coordinates are returned.
     * <p>
     * Screen-space coordinates follow the rendered category page: increasing {@code y} means further down,
     * so an entry "above" another one has a smaller {@code y} value.
     */
    public Vec2 get(BookEntryModel model) {
        var key = this.modelKeys.getOrDefault(model, model.getId().toString());
        var location = this.entries.get(key);
        if (location == null) {
            return new Vec2(model.getX(), model.getY());
        }
        return location;
    }

    public final class EntryPlacement {
        private final String key;
        private final BookEntryModel model;
        private Vec2 cursor = Vec2.ZERO;
        private boolean positioned;

        private EntryPlacement(String key, BookEntryModel model) {
            this.key = key;
            this.model = model;
        }

        /**
         * Places this entry at an absolute screen-space position.
         * <p>
         * Positive {@code x} moves right. Positive {@code y} moves down.
         */
        public EntryPlacement at(int x, int y) {
            this.cursor = new Vec2(x, y);
            this.positioned = true;
            return this.commit();
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it to the right by {@code amount}.
         */
        public EntryPlacement rightOf(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(amount, 0);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it to the right by {@code amount}.
         */
        public EntryPlacement rightOf(EntryProvider anchor, int amount) {
            return this.rightOf(anchor.entryId(), amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it to the right by {@code amount}.
         */
        public EntryPlacement rightOf(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).rightOf(amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it to the left by {@code amount}.
         */
        public EntryPlacement leftOf(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(-amount, 0);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it to the left by {@code amount}.
         */
        public EntryPlacement leftOf(EntryProvider anchor, int amount) {
            return this.leftOf(anchor.entryId(), amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it to the left by {@code amount}.
         */
        public EntryPlacement leftOf(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).leftOf(amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it upward by {@code amount}.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving upward decreases {@code y}.
         */
        public EntryPlacement above(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(0, -amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it upward by {@code amount}.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving upward decreases {@code y}.
         */
        public EntryPlacement above(EntryProvider anchor, int amount) {
            return this.above(anchor.entryId(), amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it upward by {@code amount}.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving upward decreases {@code y}.
         */
        public EntryPlacement above(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).above(amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it downward by {@code amount}.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving downward increases {@code y}.
         */
        public EntryPlacement below(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(0, amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it downward by {@code amount}.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving downward increases {@code y}.
         */
        public EntryPlacement below(EntryProvider anchor, int amount) {
            return this.below(anchor.entryId(), amount);
        }

        /**
         * Anchors this entry to {@code anchor} and then moves it downward by {@code amount}.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving downward increases {@code y}.
         */
        public EntryPlacement below(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).below(amount);
        }

        /**
         * Moves this entry to the right relative to its current cursor.
         */
        public EntryPlacement rightOf(int amount) {
            return this.move(amount, 0);
        }

        /**
         * Moves this entry to the left relative to its current cursor.
         */
        public EntryPlacement leftOf(int amount) {
            return this.move(-amount, 0);
        }

        /**
         * Moves this entry upward relative to its current cursor.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving upward decreases {@code y}.
         */
        public EntryPlacement above(int amount) {
            return this.move(0, -amount);
        }

        /**
         * Moves this entry downward relative to its current cursor.
         * <p>
         * Because layout coordinates are screen-space coordinates, moving downward increases {@code y}.
         */
        public EntryPlacement below(int amount) {
            return this.move(0, amount);
        }

        private EntryPlacement anchor(String anchor) {
            this.cursor = CategoryLayout.this.get(anchor);
            this.positioned = true;
            return this.commit();
        }

        private EntryPlacement anchor(BookEntryModel anchor) {
            this.cursor = CategoryLayout.this.get(anchor);
            this.positioned = true;
            return this.commit();
        }

        private EntryPlacement move(int dx, int dy) {
            if (!this.positioned) {
                throw new IllegalStateException("Entry '" + this.key + "' must be anchored before applying chained movement");
            }

            this.cursor = new Vec2(this.cursor.x + dx, this.cursor.y + dy);
            return this.commit();
        }

        private EntryPlacement commit() {
            CategoryLayout.this.entries.put(this.key, this.cursor);
            if (this.model != null) {
                this.model.withLocation((int) this.cursor.x, (int) this.cursor.y);
            }
            return this;
        }
    }
}
