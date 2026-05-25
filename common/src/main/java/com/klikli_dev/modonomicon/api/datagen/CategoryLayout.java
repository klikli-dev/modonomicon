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

    public static CategoryLayout relativeEntryLayout() {
        return new CategoryLayout();
    }

    public EntryPlacement entry(String id) {
        return new EntryPlacement(id, null);
    }

    public EntryPlacement entry(EntryProvider provider) {
        return this.entry(provider.entryId());
    }

    public EntryPlacement entry(BookEntryModel model) {
        var key = model.getId().toString();
        this.modelKeys.put(model, key);
        return new EntryPlacement(key, model);
    }

    public Vec2 get(String id) {
        var location = this.entries.get(id);
        if (location == null) {
            throw new IllegalArgumentException("Entry '" + id + "' has no configured layout position");
        }
        return location;
    }

    public Vec2 get(EntryProvider provider) {
        return this.get(provider.entryId());
    }

    public Vec2 get(BookEntryModel model) {
        var key = this.modelKeys.getOrDefault(model, model.getId().toString());
        return this.get(key);
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

        public EntryPlacement at(int x, int y) {
            this.cursor = new Vec2(x, y);
            this.positioned = true;
            return this.commit();
        }

        public EntryPlacement rightOf(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(amount, 0);
        }

        public EntryPlacement rightOf(EntryProvider anchor, int amount) {
            return this.rightOf(anchor.entryId(), amount);
        }

        public EntryPlacement rightOf(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).rightOf(amount);
        }

        public EntryPlacement leftOf(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(-amount, 0);
        }

        public EntryPlacement leftOf(EntryProvider anchor, int amount) {
            return this.leftOf(anchor.entryId(), amount);
        }

        public EntryPlacement leftOf(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).leftOf(amount);
        }

        public EntryPlacement above(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(0, -amount);
        }

        public EntryPlacement above(EntryProvider anchor, int amount) {
            return this.above(anchor.entryId(), amount);
        }

        public EntryPlacement above(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).above(amount);
        }

        public EntryPlacement below(String anchor, int amount) {
            this.anchor(anchor);
            return this.move(0, amount);
        }

        public EntryPlacement below(EntryProvider anchor, int amount) {
            return this.below(anchor.entryId(), amount);
        }

        public EntryPlacement below(BookEntryModel anchor, int amount) {
            return this.anchor(anchor).below(amount);
        }

        public EntryPlacement rightOf(int amount) {
            return this.move(amount, 0);
        }

        public EntryPlacement leftOf(int amount) {
            return this.move(-amount, 0);
        }

        public EntryPlacement above(int amount) {
            return this.move(0, -amount);
        }

        public EntryPlacement below(int amount) {
            return this.move(0, amount);
        }

        private EntryPlacement anchor(String anchor) {
            this.cursor = CategoryLayout.this.get(anchor);
            this.positioned = true;
            return this.commit();
        }

        private EntryPlacement anchor(BookEntryModel anchor) {
            return this.anchor(anchor.getId().toString());
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
