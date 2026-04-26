/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;

public final class DefaultBookTheme implements BookTheme {

    public static final DefaultBookTheme INSTANCE = new DefaultBookTheme();

    private static final BookContentTheme CONTENT = new BookContentTheme() {
        @Override
        public GuiSprite doublePageBackground() {
            return GeneratedDefaultBookThemeData.DOUBLE_PAGE_BACKGROUND;
        }

        @Override
        public GuiSprite singlePageBackground() {
            return GeneratedDefaultBookThemeData.SINGLE_PAGE_BACKGROUND;
        }

        @Override
        public GuiSprite titleSeparator() {
            return GeneratedDefaultBookThemeData.TITLE_SEPARATOR;
        }

        @Override
        public GuiSprite lockIcon() {
            return GeneratedDefaultBookThemeData.LOCK_ICON;
        }

        @Override
        public GuiButtonSprites unreadIndicator() {
            return GeneratedDefaultBookThemeData.UNREAD_INDICATOR;
        }

        @Override
        public GuiButtonSprites nextPageButton() {
            return GeneratedDefaultBookThemeData.NEXT_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites previousPageButton() {
            return GeneratedDefaultBookThemeData.PREVIOUS_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites smallNextPageButton() {
            return GeneratedDefaultBookThemeData.SMALL_NEXT_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites smallPreviousPageButton() {
            return GeneratedDefaultBookThemeData.SMALL_PREVIOUS_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites backButton() {
            return GeneratedDefaultBookThemeData.BACK_BUTTON;
        }

        @Override
        public GuiButtonSprites exitButton() {
            return GeneratedDefaultBookThemeData.EXIT_BUTTON;
        }

        @Override
        public GuiButtonSprites visualizeButton() {
            return GeneratedDefaultBookThemeData.VISUALIZE_BUTTON;
        }

        @Override
        public GuiButtonSprites categoryScrollUpButton() {
            return GeneratedDefaultBookThemeData.CATEGORY_SCROLL_UP_BUTTON;
        }

        @Override
        public GuiButtonSprites categoryScrollDownButton() {
            return GeneratedDefaultBookThemeData.CATEGORY_SCROLL_DOWN_BUTTON;
        }

        @Override
        public GuiSprite searchFieldBackground() {
            return GeneratedDefaultBookThemeData.SEARCH_FIELD_BACKGROUND;
        }

        @Override
        public GuiSprite mediaFrame() {
            return GeneratedDefaultBookThemeData.MEDIA_FRAME;
        }
    };

    private static final BookOverviewTheme OVERVIEW = new BookOverviewTheme() {
        @Override
        public GuiButtonSprites categoryButton() {
            return GeneratedDefaultBookThemeData.CATEGORY_BUTTON;
        }

        @Override
        public GuiButtonSprites searchButton() {
            return GeneratedDefaultBookThemeData.SEARCH_BUTTON;
        }

        @Override
        public GuiButtonSprites showBookmarksButton() {
            return GeneratedDefaultBookThemeData.SHOW_BOOKMARKS_BUTTON;
        }

        @Override
        public GuiButtonSprites showRecentlyUnlockedButton() {
            return GeneratedDefaultBookThemeData.SHOW_RECENTLY_UNLOCKED_BUTTON;
        }

        @Override
        public GuiButtonSprites addBookmarkButton() {
            return GeneratedDefaultBookThemeData.ADD_BOOKMARK_BUTTON;
        }

        @Override
        public GuiButtonSprites removeBookmarkButton() {
            return GeneratedDefaultBookThemeData.REMOVE_BOOKMARK_BUTTON;
        }

        @Override
        public GuiButtonSprites readAllButton() {
            return GeneratedDefaultBookThemeData.READ_ALL_BUTTON;
        }

        @Override
        public GuiButtonSprites readNoneButton() {
            return GeneratedDefaultBookThemeData.READ_NONE_BUTTON;
        }

        @Override
        public GuiButtonSprites readUnlockedButton() {
            return GeneratedDefaultBookThemeData.READ_UNLOCKED_BUTTON;
        }
    };

    private static final BookFrameTheme FRAME = new BookFrameTheme() {
        @Override
        public GuiNineSlice frame() {
            return GeneratedDefaultBookThemeData.FRAME;
        }

        @Override
        public GuiFrameOverlay topOverlay() {
            return GeneratedDefaultBookThemeData.TOP_OVERLAY;
        }

        @Override
        public GuiFrameOverlay bottomOverlay() {
            return GeneratedDefaultBookThemeData.BOTTOM_OVERLAY;
        }

        @Override
        public GuiFrameOverlay leftOverlay() {
            return GeneratedDefaultBookThemeData.LEFT_OVERLAY;
        }

        @Override
        public GuiFrameOverlay rightOverlay() {
            return GeneratedDefaultBookThemeData.RIGHT_OVERLAY;
        }
    };

    private static final BookRecipeTheme RECIPES = new BookRecipeTheme() {
        @Override
        public GuiSprite craftingGrid() {
            return GeneratedDefaultBookThemeData.CRAFTING_GRID;
        }

        @Override
        public GuiSprite shapelessIcon() {
            return GeneratedDefaultBookThemeData.SHAPELESS_ICON;
        }

        @Override
        public GuiSprite processingRecipeBackground() {
            return GeneratedDefaultBookThemeData.PROCESSING_RECIPE_BACKGROUND;
        }

        @Override
        public GuiSprite smithingRecipeBackground() {
            return GeneratedDefaultBookThemeData.SMITHING_RECIPE_BACKGROUND;
        }

        @Override
        public GuiSprite spotlightSlot() {
            return GeneratedDefaultBookThemeData.SPOTLIGHT_SLOT;
        }
    };

    private static final BookLayoutTheme LAYOUT = new BookLayoutTheme() {
        @Override
        public int bookTextOffsetX() {
            return 0;
        }

        @Override
        public int bookTextOffsetY() {
            return 0;
        }

        @Override
        public int bookTextOffsetWidth() {
            return 0;
        }

        @Override
        public int bookTextOffsetHeight() {
            return 0;
        }

        @Override
        public int categoryButtonXOffset() {
            return 0;
        }

        @Override
        public int categoryButtonYOffset() {
            return 0;
        }

        @Override
        public int searchButtonXOffset() {
            return 0;
        }

        @Override
        public int searchButtonYOffset() {
            return 0;
        }

        @Override
        public int readAllButtonYOffset() {
            return 0;
        }

        @Override
        public float categoryButtonIconScale() {
            return 1.0f;
        }
    };

    private static final BookPaletteTheme PALETTE = new BookPaletteTheme() {
        @Override
        public int defaultTitleColor() {
            return ModonomiconConstants.Data.Book.DEFAULT_TITLE_COLOR;
        }

        @Override
        public int defaultTextColor() {
            return ModonomiconConstants.Data.Book.DEFAULT_TEXT_COLOR;
        }
    };

    private DefaultBookTheme() {
    }

    @Override
    public BookContentTheme content() {
        return CONTENT;
    }

    @Override
    public BookOverviewTheme overview() {
        return OVERVIEW;
    }

    @Override
    public BookFrameTheme frame() {
        return FRAME;
    }

    @Override
    public BookRecipeTheme recipes() {
        return RECIPES;
    }

    @Override
    public BookLayoutTheme layout() {
        return LAYOUT;
    }

    @Override
    public BookPaletteTheme palette() {
        return PALETTE;
    }
}
