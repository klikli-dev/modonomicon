/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.klikli_dev.modonomicon.Modonomicon;

public final class GeneratedDefaultBookThemeData {

    public static final GuiSprite DOUBLE_PAGE_BACKGROUND = sprite("content/double_page_background", 272, 178);
    public static final GuiSprite SINGLE_PAGE_BACKGROUND = sprite("content/single_page_background", 145, 178);
    public static final GuiSprite TITLE_SEPARATOR = sprite("content/title_separator", 110, 3);
    public static final GuiSprite LOCK_ICON = sprite("content/lock_icon", 16, 16);
    public static final GuiButtonSprites UNREAD_INDICATOR = button("content/unread_indicator", 11, 11);
    public static final GuiButtonSprites NEXT_PAGE_BUTTON = button("content/next_page_button", 18, 10);
    public static final GuiButtonSprites PREVIOUS_PAGE_BUTTON = button("content/previous_page_button", 18, 10);
    public static final GuiButtonSprites SMALL_NEXT_PAGE_BUTTON = button("content/small_next_page_button", 5, 7);
    public static final GuiButtonSprites SMALL_PREVIOUS_PAGE_BUTTON = button("content/small_previous_page_button", 5, 7);
    public static final GuiButtonSprites BACK_BUTTON = button("content/back_button", 18, 9);
    public static final GuiButtonSprites EXIT_BUTTON = button("content/exit_button", 12, 12);
    public static final GuiButtonSprites VISUALIZE_BUTTON = button("content/visualize_button", 11, 7);
    public static final GuiButtonSprites CATEGORY_SCROLL_UP_BUTTON = button("content/category_scroll_up_button", 14, 10);
    public static final GuiButtonSprites CATEGORY_SCROLL_DOWN_BUTTON = button("content/category_scroll_down_button", 14, 10);
    public static final GuiSprite SEARCH_FIELD_BACKGROUND = sprite("content/search_field_background", 99, 14);
    public static final GuiSprite MEDIA_FRAME = sprite("content/media_frame", 106, 106);

    public static final GuiButtonSprites CATEGORY_BUTTON = new GuiButtonSprites(
            sprite("overview/category_button_normal", 25, 20),
            sprite("overview/category_button_hover", 26, 20),
            sprite("overview/category_button_selected", 28, 20)
    );
    public static final GuiButtonSprites SEARCH_BUTTON = sameStateButton("overview/search_button", 44, 20);
    public static final GuiButtonSprites SHOW_BOOKMARKS_BUTTON = sameStateButton("overview/show_bookmarks_button", 44, 20);
    public static final GuiButtonSprites SHOW_RECENTLY_UNLOCKED_BUTTON = sameStateButton("overview/show_recently_unlocked_button", 44, 20);
    public static final GuiButtonSprites ADD_BOOKMARK_BUTTON = sameStateButton("overview/add_bookmark_button", 44, 20);
    public static final GuiButtonSprites REMOVE_BOOKMARK_BUTTON = sameStateButton("overview/remove_bookmark_button", 44, 20);
    public static final GuiButtonSprites READ_ALL_BUTTON = button("overview/read_all_button", 16, 14);
    public static final GuiButtonSprites READ_NONE_BUTTON = button("overview/read_none_button", 16, 14);
    public static final GuiButtonSprites READ_UNLOCKED_BUTTON = button("overview/read_unlocked_button", 16, 14);

    public static final GuiNineSlice FRAME = new GuiNineSlice(texture("frame/frame"), 140, 140, 50, 50, 50, 50);
    public static final GuiFrameOverlay TOP_OVERLAY = new GuiFrameOverlay(sprite("frame/top_overlay", 72, 7), 0, 4);
    public static final GuiFrameOverlay BOTTOM_OVERLAY = new GuiFrameOverlay(sprite("frame/bottom_overlay", 72, 8), 0, -4);
    public static final GuiFrameOverlay LEFT_OVERLAY = new GuiFrameOverlay(sprite("frame/left_overlay", 7, 70), 3, 0);
    public static final GuiFrameOverlay RIGHT_OVERLAY = new GuiFrameOverlay(sprite("frame/right_overlay", 8, 70), -4, 0);

    public static final GuiSprite CRAFTING_GRID = sprite("recipes/crafting_grid", 100, 62);
    public static final GuiSprite SHAPELESS_ICON = sprite("recipes/shapeless_icon", 11, 11);
    public static final GuiSprite PROCESSING_RECIPE_BACKGROUND = sprite("recipes/processing_recipe_background", 96, 24);
    public static final GuiSprite SMITHING_RECIPE_BACKGROUND = sprite("recipes/smithing_recipe_background", 96, 62);
    public static final GuiSprite SPOTLIGHT_SLOT = sprite("recipes/spotlight_slot", 66, 26);

    private GeneratedDefaultBookThemeData() {
    }

    private static GuiSprite sprite(String key, int width, int height) {
        return new GuiSprite(texture(key), width, height);
    }

    private static GuiButtonSprites button(String key, int width, int height) {
        return new GuiButtonSprites(
                sprite(key + "_normal", width, height),
                sprite(key + "_hover", width, height)
        );
    }

    private static GuiButtonSprites sameStateButton(String key, int width, int height) {
        var sprite = sprite(key, width, height);
        return new GuiButtonSprites(sprite, sprite);
    }

    private static net.minecraft.resources.Identifier texture(String key) {
        return Modonomicon.loc("textures/gui/sprites/modonomicon/default/" + key + ".png");
    }
}
