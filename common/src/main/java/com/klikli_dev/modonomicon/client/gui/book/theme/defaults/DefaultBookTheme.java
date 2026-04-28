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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBookTheme implements BookTheme {

    private static final int NODE_ENTRY_BACKGROUND_WIDTH = 26;
    private static final int NODE_ENTRY_BACKGROUND_HEIGHT = 26;

    private final BookThemeData data;
    private final String textureRoot;
    private final Map<String, GuiTexture> entryBackgroundCache = new ConcurrentHashMap<>();

    private final GuiSprite doublePageBackground;
    private final GuiSprite singlePageBackground;
    private final GuiSprite titleSeparator;
    private final GuiSprite lockIcon;
    private final GuiButtonSprites unreadIndicator;
    private final GuiButtonSprites nextPageButton;
    private final GuiButtonSprites previousPageButton;
    private final GuiButtonSprites smallNextPageButton;
    private final GuiButtonSprites smallPreviousPageButton;
    private final GuiButtonSprites backButton;
    private final GuiButtonSprites exitButton;
    private final GuiButtonSprites visualizeButton;
    private final GuiButtonSprites categoryScrollUpButton;
    private final GuiButtonSprites categoryScrollDownButton;
    private final GuiButtonSprites categoryButton;
    private final GuiButtonSprites searchButton;
    private final GuiButtonSprites showBookmarksButton;
    private final GuiButtonSprites showRecentlyUnlockedButton;
    private final GuiButtonSprites addBookmarkButton;
    private final GuiButtonSprites removeBookmarkButton;
    private final GuiButtonSprites readAllButton;
    private final GuiButtonSprites readNoneButton;
    private final GuiButtonSprites readUnlockedButton;
    private final GuiSprite searchFieldBackground;
    private final GuiSprite mediaFrame;
    private final GuiSprite craftingGrid;
    private final GuiSprite shapelessIcon;
    private final GuiSprite processingRecipeBackground;
    private final GuiSprite smithingRecipeBackground;
    private final GuiSprite spotlightSlot;
    private final GuiSprite smallCurveLeftDown;
    private final GuiSprite smallCurveRightDown;
    private final GuiSprite smallCurveLeftUp;
    private final GuiSprite smallCurveRightUp;
    private final GuiSprite largeCurveLeftDown;
    private final GuiSprite largeCurveRightDown;
    private final GuiSprite largeCurveLeftUp;
    private final GuiSprite largeCurveRightUp;
    private final GuiSprite verticalLine;
    private final GuiSprite horizontalLine;
    private final GuiSprite upArrow;
    private final GuiSprite downArrow;
    private final GuiSprite rightArrow;
    private final GuiSprite leftArrow;
    private final GuiNineSlice frameSprite;
    private final GuiFrameOverlay topOverlay;
    private final GuiFrameOverlay bottomOverlay;
    private final GuiFrameOverlay leftOverlay;
    private final GuiFrameOverlay rightOverlay;

    private final BookContentTheme content = new BookContentTheme() {
        @Override
        public GuiSprite doublePageBackground() {
            return doublePageBackground;
        }

        @Override
        public GuiSprite singlePageBackground() {
            return singlePageBackground;
        }

        @Override
        public GuiSprite titleSeparator() {
            return titleSeparator;
        }

        @Override
        public GuiSprite lockIcon() {
            return lockIcon;
        }

        @Override
        public GuiButtonSprites unreadIndicator() {
            return unreadIndicator;
        }

        @Override
        public GuiButtonSprites nextPageButton() {
            return nextPageButton;
        }

        @Override
        public GuiButtonSprites previousPageButton() {
            return previousPageButton;
        }

        @Override
        public GuiButtonSprites smallNextPageButton() {
            return smallNextPageButton;
        }

        @Override
        public GuiButtonSprites smallPreviousPageButton() {
            return smallPreviousPageButton;
        }

        @Override
        public GuiButtonSprites backButton() {
            return backButton;
        }

        @Override
        public GuiButtonSprites exitButton() {
            return exitButton;
        }

        @Override
        public GuiButtonSprites visualizeButton() {
            return visualizeButton;
        }

        @Override
        public GuiButtonSprites categoryScrollUpButton() {
            return categoryScrollUpButton;
        }

        @Override
        public GuiButtonSprites categoryScrollDownButton() {
            return categoryScrollDownButton;
        }

        @Override
        public GuiButtonSprites categoryButton() {
            return categoryButton;
        }

        @Override
        public GuiButtonSprites searchButton() {
            return searchButton;
        }

        @Override
        public GuiButtonSprites showBookmarksButton() {
            return showBookmarksButton;
        }

        @Override
        public GuiButtonSprites showRecentlyUnlockedButton() {
            return showRecentlyUnlockedButton;
        }

        @Override
        public GuiButtonSprites addBookmarkButton() {
            return addBookmarkButton;
        }

        @Override
        public GuiButtonSprites removeBookmarkButton() {
            return removeBookmarkButton;
        }

        @Override
        public GuiButtonSprites readAllButton() {
            return readAllButton;
        }

        @Override
        public GuiButtonSprites readNoneButton() {
            return readNoneButton;
        }

        @Override
        public GuiButtonSprites readUnlockedButton() {
            return readUnlockedButton;
        }

        @Override
        public GuiSprite searchFieldBackground() {
            return searchFieldBackground;
        }

        @Override
        public GuiSprite mediaFrame() {
            return mediaFrame;
        }

        @Override
        public GuiSprite craftingGrid() {
            return craftingGrid;
        }

        @Override
        public GuiSprite shapelessIcon() {
            return shapelessIcon;
        }

        @Override
        public GuiSprite processingRecipeBackground() {
            return processingRecipeBackground;
        }

        @Override
        public GuiSprite smithingRecipeBackground() {
            return smithingRecipeBackground;
        }

        @Override
        public GuiSprite spotlightSlot() {
            return spotlightSlot;
        }
    };
    private final BookNodeTheme node = new BookNodeTheme() {
        @Override
        public GuiTexture entryBackground(String spriteId) {
            return entryBackgroundCache.computeIfAbsent(spriteId, DefaultBookTheme.this::createEntryBackground);
        }

        @Override
        public GuiSprite smallCurveLeftDown() {
            return smallCurveLeftDown;
        }

        @Override
        public GuiSprite smallCurveRightDown() {
            return smallCurveRightDown;
        }

        @Override
        public GuiSprite smallCurveLeftUp() {
            return smallCurveLeftUp;
        }

        @Override
        public GuiSprite smallCurveRightUp() {
            return smallCurveRightUp;
        }

        @Override
        public GuiSprite largeCurveLeftDown() {
            return largeCurveLeftDown;
        }

        @Override
        public GuiSprite largeCurveRightDown() {
            return largeCurveRightDown;
        }

        @Override
        public GuiSprite largeCurveLeftUp() {
            return largeCurveLeftUp;
        }

        @Override
        public GuiSprite largeCurveRightUp() {
            return largeCurveRightUp;
        }

        @Override
        public GuiSprite verticalLine() {
            return verticalLine;
        }

        @Override
        public GuiSprite horizontalLine() {
            return horizontalLine;
        }

        @Override
        public GuiSprite upArrow() {
            return upArrow;
        }

        @Override
        public GuiSprite downArrow() {
            return downArrow;
        }

        @Override
        public GuiSprite rightArrow() {
            return rightArrow;
        }

        @Override
        public GuiSprite leftArrow() {
            return leftArrow;
        }
    };
    private final BookFrameTheme frame = new BookFrameTheme() {
        @Override
        public GuiNineSlice frame() {
            return frameSprite;
        }

        @Override
        public GuiFrameOverlay topOverlay() {
            return topOverlay;
        }

        @Override
        public GuiFrameOverlay bottomOverlay() {
            return bottomOverlay;
        }

        @Override
        public GuiFrameOverlay leftOverlay() {
            return leftOverlay;
        }

        @Override
        public GuiFrameOverlay rightOverlay() {
            return rightOverlay;
        }
    };

    public DefaultBookTheme(BookThemeData data) {
        this.data = data;
        this.textureRoot = "textures/gui/sprites/modonomicon/themes/" + data.id().getPath() + "/";
        this.doublePageBackground = sprite("content/backgrounds/book/double_page_background.png", 272, 178);
        this.singlePageBackground = sprite("content/backgrounds/book/single_page_background.png", 145, 178);
        this.titleSeparator = sprite("content/decorations/book/title_separator.png", 110, 3);
        this.lockIcon = sprite("content/icons/book/lock_icon.png", 16, 16);
        this.unreadIndicator = button("content/indicators/book/unread_indicator", 11, 11, true);
        this.nextPageButton = button("content/buttons/navigation/next_page_button", 18, 10, true);
        this.previousPageButton = button("content/buttons/navigation/previous_page_button", 18, 10, true);
        this.smallNextPageButton = button("content/buttons/navigation/small_next_page_button", 5, 7, true);
        this.smallPreviousPageButton = button("content/buttons/navigation/small_previous_page_button", 5, 7, true);
        this.backButton = button("content/buttons/navigation/back_button", 18, 9, true);
        this.exitButton = button("content/buttons/navigation/exit_button", 12, 12, true);
        this.visualizeButton = button("content/buttons/navigation/visualize_button", 11, 7, true);
        this.categoryScrollUpButton = button("content/buttons/category/category_scroll_up_button", 14, 10, true);
        this.categoryScrollDownButton = button("content/buttons/category/category_scroll_down_button", 14, 10, true);
        this.categoryButton = button("content/buttons/category/category_button", 44, 20, false, false);
        this.searchButton = button("content/buttons/side/search_button", 44, 20, false);
        this.showBookmarksButton = button("content/buttons/side/show_bookmarks_button", 44, 20, false);
        this.showRecentlyUnlockedButton = button("content/buttons/side/show_recently_unlocked_button", 44, 20, false);
        this.addBookmarkButton = button("content/buttons/side/add_bookmark_button", 44, 20, false);
        this.removeBookmarkButton = button("content/buttons/side/remove_bookmark_button", 44, 20, false);
        this.readAllButton = button("content/buttons/read/read_all_button", 16, 14, true);
        this.readNoneButton = button("content/buttons/read/read_none_button", 16, 14, true);
        this.readUnlockedButton = button("content/buttons/read/read_unlocked_button", 16, 14, true);
        this.searchFieldBackground = sprite("content/fields/search/background.png", 99, 14);
        this.mediaFrame = sprite("content/pages/media/frame.png", 106, 106);
        this.craftingGrid = sprite("content/pages/recipes/crafting_grid.png", 100, 62);
        this.shapelessIcon = sprite("content/pages/recipes/shapeless_icon.png", 11, 11);
        this.processingRecipeBackground = sprite("content/pages/recipes/processing_recipe_background.png", 96, 24);
        this.smithingRecipeBackground = sprite("content/pages/recipes/smithing_recipe_background.png", 96, 62);
        this.spotlightSlot = sprite("content/pages/recipes/spotlight_slot.png", 66, 26);
        this.smallCurveLeftDown = sprite("node/connections/small_curve_left_down.png", 30, 30);
        this.smallCurveRightDown = sprite("node/connections/small_curve_right_down.png", 30, 30);
        this.smallCurveLeftUp = sprite("node/connections/small_curve_left_up.png", 30, 30);
        this.smallCurveRightUp = sprite("node/connections/small_curve_right_up.png", 30, 30);
        this.largeCurveLeftDown = sprite("node/connections/large_curve_left_down.png", 60, 60);
        this.largeCurveRightDown = sprite("node/connections/large_curve_right_down.png", 60, 60);
        this.largeCurveLeftUp = sprite("node/connections/large_curve_left_up.png", 60, 60);
        this.largeCurveRightUp = sprite("node/connections/large_curve_right_up.png", 60, 60);
        this.verticalLine = sprite("node/connections/vertical_line.png", 30, 31);
        this.horizontalLine = sprite("node/connections/horizontal_line.png", 31, 30);
        this.upArrow = sprite("node/connections/up_arrow.png", 30, 30);
        this.downArrow = sprite("node/connections/down_arrow.png", 30, 30);
        this.rightArrow = sprite("node/connections/right_arrow.png", 30, 30);
        this.leftArrow = sprite("node/connections/left_arrow.png", 30, 30);
        this.frameSprite = new GuiNineSlice(texture("frame/frame.png"), 140, 140, 50, 50, 50, 50);
        this.topOverlay = new GuiFrameOverlay(sprite("frame/top_overlay.png", 72, 7), 0, 4);
        this.bottomOverlay = new GuiFrameOverlay(sprite("frame/bottom_overlay.png", 72, 8), 0, -4);
        this.leftOverlay = new GuiFrameOverlay(sprite("frame/left_overlay.png", 7, 70), 3, 0);
        this.rightOverlay = new GuiFrameOverlay(sprite("frame/right_overlay.png", 8, 70), -4, 0);
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

    private GuiTexture createEntryBackground(String spriteId) {
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
}
