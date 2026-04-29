/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme.defaults;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.client.gui.book.theme.*;
import com.klikli_dev.modonomicon.platform.Services;
import com.klikli_dev.modonomicon.platform.services.PlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class DefaultBookTheme implements BookTheme {

    private static final String DEFAULT_TEXTURE_ROOT = "textures/gui/sprites/modonomicon/themes/default/";

    private final BookThemeData data;
    private final String textureRoot;

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
    private final GuiSprite craftingRecipeBackground;
    private final GuiSprite craftingGrid;
    private final GuiSprite craftingSlot;
    private final GuiSprite craftingArrow;
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
            return DefaultBookTheme.this.doublePageBackground;
        }

        @Override
        public GuiSprite singlePageBackground() {
            return DefaultBookTheme.this.singlePageBackground;
        }

        @Override
        public GuiSprite titleSeparator() {
            return DefaultBookTheme.this.titleSeparator;
        }

        @Override
        public GuiSprite lockIcon() {
            return DefaultBookTheme.this.lockIcon;
        }

        @Override
        public GuiButtonSprites unreadIndicator() {
            return DefaultBookTheme.this.unreadIndicator;
        }

        @Override
        public GuiButtonSprites nextPageButton() {
            return DefaultBookTheme.this.nextPageButton;
        }

        @Override
        public GuiButtonSprites previousPageButton() {
            return DefaultBookTheme.this.previousPageButton;
        }

        @Override
        public GuiButtonSprites smallNextPageButton() {
            return DefaultBookTheme.this.smallNextPageButton;
        }

        @Override
        public GuiButtonSprites smallPreviousPageButton() {
            return DefaultBookTheme.this.smallPreviousPageButton;
        }

        @Override
        public GuiButtonSprites backButton() {
            return DefaultBookTheme.this.backButton;
        }

        @Override
        public GuiButtonSprites exitButton() {
            return DefaultBookTheme.this.exitButton;
        }

        @Override
        public GuiButtonSprites visualizeButton() {
            return DefaultBookTheme.this.visualizeButton;
        }

        @Override
        public GuiButtonSprites categoryScrollUpButton() {
            return DefaultBookTheme.this.categoryScrollUpButton;
        }

        @Override
        public GuiButtonSprites categoryScrollDownButton() {
            return DefaultBookTheme.this.categoryScrollDownButton;
        }

        @Override
        public GuiButtonSprites categoryButton() {
            return DefaultBookTheme.this.categoryButton;
        }

        @Override
        public GuiButtonSprites searchButton() {
            return DefaultBookTheme.this.searchButton;
        }

        @Override
        public GuiButtonSprites showBookmarksButton() {
            return DefaultBookTheme.this.showBookmarksButton;
        }

        @Override
        public GuiButtonSprites showRecentlyUnlockedButton() {
            return DefaultBookTheme.this.showRecentlyUnlockedButton;
        }

        @Override
        public GuiButtonSprites addBookmarkButton() {
            return DefaultBookTheme.this.addBookmarkButton;
        }

        @Override
        public GuiButtonSprites removeBookmarkButton() {
            return DefaultBookTheme.this.removeBookmarkButton;
        }

        @Override
        public GuiButtonSprites readAllButton() {
            return DefaultBookTheme.this.readAllButton;
        }

        @Override
        public GuiButtonSprites readNoneButton() {
            return DefaultBookTheme.this.readNoneButton;
        }

        @Override
        public GuiButtonSprites readUnlockedButton() {
            return DefaultBookTheme.this.readUnlockedButton;
        }

        @Override
        public GuiSprite searchFieldBackground() {
            return DefaultBookTheme.this.searchFieldBackground;
        }

        @Override
        public GuiSprite mediaFrame() {
            return DefaultBookTheme.this.mediaFrame;
        }

        @Override
        public GuiSprite craftingRecipeBackground() {
            return DefaultBookTheme.this.craftingRecipeBackground;
        }

        @Override
        public GuiSprite craftingGrid() {
            return DefaultBookTheme.this.craftingGrid;
        }

        @Override
        public GuiSprite craftingSlot() {
            return DefaultBookTheme.this.craftingSlot;
        }

        @Override
        public GuiSprite craftingArrow() {
            return DefaultBookTheme.this.craftingArrow;
        }

        @Override
        public GuiSprite shapelessIcon() {
            return DefaultBookTheme.this.shapelessIcon;
        }

        @Override
        public GuiSprite processingRecipeBackground() {
            return DefaultBookTheme.this.processingRecipeBackground;
        }

        @Override
        public GuiSprite smithingRecipeBackground() {
            return DefaultBookTheme.this.smithingRecipeBackground;
        }

        @Override
        public GuiSprite spotlightSlot() {
            return DefaultBookTheme.this.spotlightSlot;
        }
    };
    private final BookNodeTheme node = new BookNodeTheme() {
        @Override
        public GuiSprite smallCurveLeftDown() {
            return DefaultBookTheme.this.smallCurveLeftDown;
        }

        @Override
        public GuiSprite smallCurveRightDown() {
            return DefaultBookTheme.this.smallCurveRightDown;
        }

        @Override
        public GuiSprite smallCurveLeftUp() {
            return DefaultBookTheme.this.smallCurveLeftUp;
        }

        @Override
        public GuiSprite smallCurveRightUp() {
            return DefaultBookTheme.this.smallCurveRightUp;
        }

        @Override
        public GuiSprite largeCurveLeftDown() {
            return DefaultBookTheme.this.largeCurveLeftDown;
        }

        @Override
        public GuiSprite largeCurveRightDown() {
            return DefaultBookTheme.this.largeCurveRightDown;
        }

        @Override
        public GuiSprite largeCurveLeftUp() {
            return DefaultBookTheme.this.largeCurveLeftUp;
        }

        @Override
        public GuiSprite largeCurveRightUp() {
            return DefaultBookTheme.this.largeCurveRightUp;
        }

        @Override
        public GuiSprite verticalLine() {
            return DefaultBookTheme.this.verticalLine;
        }

        @Override
        public GuiSprite horizontalLine() {
            return DefaultBookTheme.this.horizontalLine;
        }

        @Override
        public GuiSprite upArrow() {
            return DefaultBookTheme.this.upArrow;
        }

        @Override
        public GuiSprite downArrow() {
            return DefaultBookTheme.this.downArrow;
        }

        @Override
        public GuiSprite rightArrow() {
            return DefaultBookTheme.this.rightArrow;
        }

        @Override
        public GuiSprite leftArrow() {
            return DefaultBookTheme.this.leftArrow;
        }
    };
    private final BookFrameTheme frame = new BookFrameTheme() {
        @Override
        public GuiNineSlice frame() {
            return DefaultBookTheme.this.frameSprite;
        }

        @Override
        public GuiFrameOverlay topOverlay() {
            return DefaultBookTheme.this.topOverlay;
        }

        @Override
        public GuiFrameOverlay bottomOverlay() {
            return DefaultBookTheme.this.bottomOverlay;
        }

        @Override
        public GuiFrameOverlay leftOverlay() {
            return DefaultBookTheme.this.leftOverlay;
        }

        @Override
        public GuiFrameOverlay rightOverlay() {
            return DefaultBookTheme.this.rightOverlay;
        }
    };

    public DefaultBookTheme(BookThemeData data) {
        this.data = data;
        this.textureRoot = "textures/gui/sprites/modonomicon/themes/" + data.id().getPath() + "/";
        this.doublePageBackground = this.sprite("content/backgrounds/book/double_page_background.png", 272, 178);
        this.singlePageBackground = this.sprite("content/backgrounds/book/single_page_background.png", 145, 178);
        this.titleSeparator = this.sprite("content/decorations/book/title_separator.png", 110, 3);
        this.lockIcon = this.sprite("content/icons/book/lock_icon.png", 16, 16);
        this.unreadIndicator = this.button("content/indicators/book/unread_indicator", 11, 11, true);
        this.nextPageButton = this.button("content/buttons/navigation/next_page_button", 18, 10, true);
        this.previousPageButton = this.button("content/buttons/navigation/previous_page_button", 18, 10, true);
        this.smallNextPageButton = this.button("content/buttons/navigation/small_next_page_button", 5, 7, true);
        this.smallPreviousPageButton = this.button("content/buttons/navigation/small_previous_page_button", 5, 7, true);
        this.backButton = this.button("content/buttons/navigation/back_button", 18, 9, true);
        this.exitButton = this.button("content/buttons/navigation/exit_button", 12, 12, true);
        this.visualizeButton = this.button("content/buttons/navigation/visualize_button", 11, 7, true);
        this.categoryScrollUpButton = this.button("content/buttons/category/category_scroll_up_button", 14, 10, true);
        this.categoryScrollDownButton = this.button("content/buttons/category/category_scroll_down_button", 14, 10, true);
        this.categoryButton = this.button("content/buttons/category/category_button", 44, 20, false, false);
        this.searchButton = this.button("content/buttons/side/search_button", 44, 20, false);
        this.showBookmarksButton = this.button("content/buttons/side/show_bookmarks_button", 44, 20, false);
        this.showRecentlyUnlockedButton = this.button("content/buttons/side/show_recently_unlocked_button", 44, 20, false);
        this.addBookmarkButton = this.button("content/buttons/side/add_bookmark_button", 44, 20, false);
        this.removeBookmarkButton = this.button("content/buttons/side/remove_bookmark_button", 44, 20, false);
        this.readAllButton = this.button("content/buttons/read/read_all_button", 16, 14, true);
        this.readNoneButton = this.button("content/buttons/read/read_none_button", 16, 14, true);
        this.readUnlockedButton = this.button("content/buttons/read/read_unlocked_button", 16, 14, true);
        this.searchFieldBackground = this.sprite("content/fields/search/background.png", 99, 14);
        this.mediaFrame = this.sprite("content/pages/media/frame.png", 106, 106);
        this.craftingRecipeBackground = this.sprite("content/pages/recipes/crafting_recipe_background.png", 100, 62);
        this.craftingGrid = this.sprite("content/pages/recipes/crafting_grid.png", 60, 60);
        this.craftingSlot = this.sprite("content/pages/recipes/crafting_slot.png", 22, 22);
        this.craftingArrow = this.sprite("content/pages/recipes/crafting_arrow.png", 9, 9);
        this.shapelessIcon = this.sprite("content/pages/recipes/shapeless_icon.png", 11, 11);
        this.processingRecipeBackground = this.sprite("content/pages/recipes/processing_recipe_background.png", 96, 24);
        this.smithingRecipeBackground = this.sprite("content/pages/recipes/smithing_recipe_background.png", 96, 62);
        this.spotlightSlot = this.sprite("content/pages/recipes/spotlight_slot.png", 66, 26);
        this.smallCurveLeftDown = this.sprite("node/connections/small_curve_left_down.png", 30, 30);
        this.smallCurveRightDown = this.sprite("node/connections/small_curve_right_down.png", 30, 30);
        this.smallCurveLeftUp = this.sprite("node/connections/small_curve_left_up.png", 30, 30);
        this.smallCurveRightUp = this.sprite("node/connections/small_curve_right_up.png", 30, 30);
        this.largeCurveLeftDown = this.sprite("node/connections/large_curve_left_down.png", 60, 60);
        this.largeCurveRightDown = this.sprite("node/connections/large_curve_right_down.png", 60, 60);
        this.largeCurveLeftUp = this.sprite("node/connections/large_curve_left_up.png", 60, 60);
        this.largeCurveRightUp = this.sprite("node/connections/large_curve_right_up.png", 60, 60);
        this.verticalLine = this.sprite("node/connections/vertical_line.png", 30, 31);
        this.horizontalLine = this.sprite("node/connections/horizontal_line.png", 31, 30);
        this.upArrow = this.sprite("node/connections/up_arrow.png", 30, 30);
        this.downArrow = this.sprite("node/connections/down_arrow.png", 30, 30);
        this.rightArrow = this.sprite("node/connections/right_arrow.png", 30, 30);
        this.leftArrow = this.sprite("node/connections/left_arrow.png", 30, 30);
        this.frameSprite = new GuiNineSlice(this.texture("frame/frame.png"), 140, 140, 50, 50, 50, 50);
        this.topOverlay = new GuiFrameOverlay(this.sprite("frame/top_overlay.png", 72, 7), 0, 4);
        this.bottomOverlay = new GuiFrameOverlay(this.sprite("frame/bottom_overlay.png", 72, 8), 0, -4);
        this.leftOverlay = new GuiFrameOverlay(this.sprite("frame/left_overlay.png", 7, 70), 3, 0);
        this.rightOverlay = new GuiFrameOverlay(this.sprite("frame/right_overlay.png", 8, 70), -4, 0);
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
        return this.button(path, width, height, hasHover, false);
    }

    private GuiButtonSprites button(String path, int width, int height, boolean hasHover, boolean hasPressed) {
        var normal = this.sprite(path + "_normal.png", width, height);
        var hover = hasHover ? this.sprite(path + "_hover.png", width, height) : normal;
        var pressed = hasPressed ? this.sprite(path + "_pressed.png", width, height) : null;
        return new GuiButtonSprites(normal, hover, pressed);
    }

    private Identifier texture(String relativePath) {
        Identifier themed = Identifier.fromNamespaceAndPath(this.data.id().getNamespace(), this.textureRoot + relativePath);

        //on client we check if the theme folder has the requested texture and serve it
        if (Services.PLATFORM.getPhysicalSide() == PlatformHelper.PhysicalSide.CLIENT) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null || minecraft.getResourceManager() == null || minecraft.getResourceManager().getResource(themed).isPresent()) {
                return themed;
            }
        }

        //otherwise we fall back to the default theme.
        //on server the actual texture does not matter and should never be accessed anyway, so we serve the defaults always.
        return Modonomicon.loc(DEFAULT_TEXTURE_ROOT + relativePath);
    }

}
