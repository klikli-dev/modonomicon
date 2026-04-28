/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme.defaults;

import com.klikli_dev.modonomicon.client.gui.book.theme.BookContentTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookFrameTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookNodeTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookPaletteTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookTheme;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookThemeData;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiButtonSprites;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiFrameOverlay;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiNineSlice;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiTexture;
import com.klikli_dev.modonomicon.client.gui.book.theme.BookLayoutTheme;
import net.minecraft.resources.Identifier;

public class DefaultBookTheme implements BookTheme {

    private static final int NODE_ENTRY_BACKGROUND_WIDTH = 26;
    private static final int NODE_ENTRY_BACKGROUND_HEIGHT = 26;

    private final BookThemeData data;
    private final String textureRoot;
    private final BookContentTheme content = new BookContentTheme() {
        @Override
        public GuiSprite doublePageBackground() {
            return sprite("content/backgrounds/book/double_page_background.png", 272, 178);
        }

        @Override
        public GuiSprite singlePageBackground() {
            return sprite("content/backgrounds/book/single_page_background.png", 145, 178);
        }

        @Override
        public GuiSprite titleSeparator() {
            return sprite("content/decorations/book/title_separator.png", 110, 3);
        }

        @Override
        public GuiSprite lockIcon() {
            return sprite("content/icons/book/lock_icon.png", 16, 16);
        }

        @Override
        public GuiButtonSprites unreadIndicator() {
            return button("content/indicators/book/unread_indicator", 11, 11, true);
        }

        @Override
        public GuiButtonSprites nextPageButton() {
            return button("content/buttons/navigation/next_page_button", 18, 10, true);
        }

        @Override
        public GuiButtonSprites previousPageButton() {
            return button("content/buttons/navigation/previous_page_button", 18, 10, true);
        }

        @Override
        public GuiButtonSprites smallNextPageButton() {
            return button("content/buttons/navigation/small_next_page_button", 5, 7, true);
        }

        @Override
        public GuiButtonSprites smallPreviousPageButton() {
            return button("content/buttons/navigation/small_previous_page_button", 5, 7, true);
        }

        @Override
        public GuiButtonSprites backButton() {
            return button("content/buttons/navigation/back_button", 18, 9, true);
        }

        @Override
        public GuiButtonSprites exitButton() {
            return button("content/buttons/navigation/exit_button", 12, 12, true);
        }

        @Override
        public GuiButtonSprites visualizeButton() {
            return button("content/buttons/navigation/visualize_button", 11, 7, true);
        }

        @Override
        public GuiButtonSprites categoryScrollUpButton() {
            return button("content/buttons/category/category_scroll_up_button", 14, 10, true);
        }

        @Override
        public GuiButtonSprites categoryScrollDownButton() {
            return button("content/buttons/category/category_scroll_down_button", 14, 10, true);
        }

        @Override
        public GuiButtonSprites categoryButton() {
            return button("content/buttons/category/category_button", 44, 20, false, false);
        }

        @Override
        public GuiButtonSprites searchButton() {
            return button("content/buttons/side/search_button", 44, 20, false);
        }

        @Override
        public GuiButtonSprites showBookmarksButton() {
            return button("content/buttons/side/show_bookmarks_button", 44, 20, false);
        }

        @Override
        public GuiButtonSprites showRecentlyUnlockedButton() {
            return button("content/buttons/side/show_recently_unlocked_button", 44, 20, false);
        }

        @Override
        public GuiButtonSprites addBookmarkButton() {
            return button("content/buttons/side/add_bookmark_button", 44, 20, false);
        }

        @Override
        public GuiButtonSprites removeBookmarkButton() {
            return button("content/buttons/side/remove_bookmark_button", 44, 20, false);
        }

        @Override
        public GuiButtonSprites readAllButton() {
            return button("content/buttons/read/read_all_button", 16, 14, true);
        }

        @Override
        public GuiButtonSprites readNoneButton() {
            return button("content/buttons/read/read_none_button", 16, 14, true);
        }

        @Override
        public GuiButtonSprites readUnlockedButton() {
            return button("content/buttons/read/read_unlocked_button", 16, 14, true);
        }

        @Override
        public GuiSprite searchFieldBackground() {
            return sprite("content/fields/search/background.png", 99, 14);
        }

        @Override
        public GuiSprite mediaFrame() {
            return sprite("content/pages/media/frame.png", 106, 106);
        }

        @Override
        public GuiSprite craftingGrid() {
            return sprite("content/pages/recipes/crafting_grid.png", 100, 62);
        }

        @Override
        public GuiSprite shapelessIcon() {
            return sprite("content/pages/recipes/shapeless_icon.png", 11, 11);
        }

        @Override
        public GuiSprite processingRecipeBackground() {
            return sprite("content/pages/recipes/processing_recipe_background.png", 96, 24);
        }

        @Override
        public GuiSprite smithingRecipeBackground() {
            return sprite("content/pages/recipes/smithing_recipe_background.png", 96, 62);
        }

        @Override
        public GuiSprite spotlightSlot() {
            return sprite("content/pages/recipes/spotlight_slot.png", 66, 26);
        }
    };
    private final BookNodeTheme node = new BookNodeTheme() {
        @Override
        public GuiTexture entryBackground(String spriteId) {
            var parsed = Identifier.tryParse(spriteId);
            if (parsed != null && parsed.getPath().startsWith("textures/")) {
                return new GuiTexture(parsed, NODE_ENTRY_BACKGROUND_WIDTH, NODE_ENTRY_BACKGROUND_HEIGHT);
            }

            String relativePath = spriteId.contains("/") ? spriteId : "entry_backgrounds/" + spriteId;
            if (!relativePath.endsWith(".png")) {
                relativePath += ".png";
            }
            return new GuiTexture(texture("node/" + relativePath), NODE_ENTRY_BACKGROUND_WIDTH, NODE_ENTRY_BACKGROUND_HEIGHT);
        }

        @Override
        public GuiSprite smallCurveLeftDown() {
            return sprite("node/connections/small_curve_left_down.png", 30, 30);
        }

        @Override
        public GuiSprite smallCurveRightDown() {
            return sprite("node/connections/small_curve_right_down.png", 30, 30);
        }

        @Override
        public GuiSprite smallCurveLeftUp() {
            return sprite("node/connections/small_curve_left_up.png", 30, 30);
        }

        @Override
        public GuiSprite smallCurveRightUp() {
            return sprite("node/connections/small_curve_right_up.png", 30, 30);
        }

        @Override
        public GuiSprite largeCurveLeftDown() {
            return sprite("node/connections/large_curve_left_down.png", 60, 60);
        }

        @Override
        public GuiSprite largeCurveRightDown() {
            return sprite("node/connections/large_curve_right_down.png", 60, 60);
        }

        @Override
        public GuiSprite largeCurveLeftUp() {
            return sprite("node/connections/large_curve_left_up.png", 60, 60);
        }

        @Override
        public GuiSprite largeCurveRightUp() {
            return sprite("node/connections/large_curve_right_up.png", 60, 60);
        }

        @Override
        public GuiSprite verticalLine() {
            return sprite("node/connections/vertical_line.png", 30, 31);
        }

        @Override
        public GuiSprite horizontalLine() {
            return sprite("node/connections/horizontal_line.png", 31, 30);
        }

        @Override
        public GuiSprite upArrow() {
            return sprite("node/connections/up_arrow.png", 30, 30);
        }

        @Override
        public GuiSprite downArrow() {
            return sprite("node/connections/down_arrow.png", 30, 30);
        }

        @Override
        public GuiSprite rightArrow() {
            return sprite("node/connections/right_arrow.png", 30, 30);
        }

        @Override
        public GuiSprite leftArrow() {
            return sprite("node/connections/left_arrow.png", 30, 30);
        }
    };
    private final BookFrameTheme frame = new BookFrameTheme() {
        @Override
        public GuiNineSlice frame() {
            return new GuiNineSlice(texture("frame/frame.png"), 140, 140, 50, 50, 50, 50);
        }

        @Override
        public GuiFrameOverlay topOverlay() {
            return new GuiFrameOverlay(sprite("frame/top_overlay.png", 72, 7), 0, 4);
        }

        @Override
        public GuiFrameOverlay bottomOverlay() {
            return new GuiFrameOverlay(sprite("frame/bottom_overlay.png", 72, 8), 0, -4);
        }

        @Override
        public GuiFrameOverlay leftOverlay() {
            return new GuiFrameOverlay(sprite("frame/left_overlay.png", 7, 70), 3, 0);
        }

        @Override
        public GuiFrameOverlay rightOverlay() {
            return new GuiFrameOverlay(sprite("frame/right_overlay.png", 8, 70), -4, 0);
        }
    };

    public DefaultBookTheme(BookThemeData data) {
        this.data = data;
        this.textureRoot = "textures/gui/sprites/modonomicon/themes/" + data.id().getPath() + "/";
    }

    @Override
    public BookContentTheme content() {
        return this.content;
    }

    @Override
    public BookLayoutTheme layout() {
        return this.data.layout();
    }

    @Override
    public BookNodeTheme node() {
        return this.node;
    }

    @Override
    public BookFrameTheme frame() {
        return this.frame;
    }

    @Override
    public BookPaletteTheme palette() {
        return this.data.palette();
    }

    private GuiSprite sprite(String relativePath, int width, int height) {
        return new GuiSprite(this.texture(relativePath), width, height);
    }

    private GuiButtonSprites button(String path, int width, int height, boolean hasHover) {
        return button(path, width, height, hasHover, false);
    }

    private GuiButtonSprites button(String path, int width, int height, boolean hasHover, boolean hasPressed) {
        var normal = sprite(path + "_normal.png", width, height);
        var hover = hasHover ? sprite(path + "_hover.png", width, height) : normal;
        var pressed = hasPressed ? sprite(path + "_pressed.png", width, height) : null;
        return new GuiButtonSprites(normal, hover, pressed);
    }

    private Identifier texture(String relativePath) {
        return Identifier.fromNamespaceAndPath(this.data.id().getNamespace(), this.textureRoot + relativePath);
    }
}
