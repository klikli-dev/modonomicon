/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.theme;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.Book;
import net.minecraft.resources.Identifier;

public final class DefaultBookTheme implements BookTheme {

    public static final DefaultBookTheme INSTANCE = new DefaultBookTheme();

    private static final BookContentTheme CONTENT = new BookContentTheme() {
        @Override
        public GuiSprite doublePageBackground() {
            return GeneratedDefaultBookThemeData.CONTENT_DOUBLE_PAGE_BACKGROUND;
        }

        @Override
        public GuiSprite singlePageBackground() {
            return GeneratedDefaultBookThemeData.CONTENT_SINGLE_PAGE_BACKGROUND;
        }

        @Override
        public GuiSprite titleSeparator() {
            return GeneratedDefaultBookThemeData.CONTENT_TITLE_SEPARATOR;
        }

        @Override
        public GuiSprite lockIcon() {
            return GeneratedDefaultBookThemeData.CONTENT_LOCK_ICON;
        }

        @Override
        public GuiButtonSprites unreadIndicator() {
            return GeneratedDefaultBookThemeData.CONTENT_UNREAD_INDICATOR;
        }

        @Override
        public GuiButtonSprites nextPageButton() {
            return GeneratedDefaultBookThemeData.CONTENT_NEXT_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites previousPageButton() {
            return GeneratedDefaultBookThemeData.CONTENT_PREVIOUS_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites smallNextPageButton() {
            return GeneratedDefaultBookThemeData.CONTENT_SMALL_NEXT_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites smallPreviousPageButton() {
            return GeneratedDefaultBookThemeData.CONTENT_SMALL_PREVIOUS_PAGE_BUTTON;
        }

        @Override
        public GuiButtonSprites backButton() {
            return GeneratedDefaultBookThemeData.CONTENT_BACK_BUTTON;
        }

        @Override
        public GuiButtonSprites exitButton() {
            return GeneratedDefaultBookThemeData.CONTENT_EXIT_BUTTON;
        }

        @Override
        public GuiButtonSprites visualizeButton() {
            return GeneratedDefaultBookThemeData.CONTENT_VISUALIZE_BUTTON;
        }

        @Override
        public GuiButtonSprites categoryScrollUpButton() {
            return GeneratedDefaultBookThemeData.CONTENT_CATEGORY_SCROLL_UP_BUTTON;
        }

        @Override
        public GuiButtonSprites categoryScrollDownButton() {
            return GeneratedDefaultBookThemeData.CONTENT_CATEGORY_SCROLL_DOWN_BUTTON;
        }

        @Override
        public GuiSprite searchFieldBackground() {
            return GeneratedDefaultBookThemeData.CONTENT_SEARCH_FIELD_BACKGROUND;
        }

        @Override
        public GuiSprite mediaFrame() {
            return GeneratedDefaultBookThemeData.CONTENT_MEDIA_FRAME;
        }
    };

    private static final BookOverviewTheme OVERVIEW = new BookOverviewTheme() {
        @Override
        public GuiButtonSprites categoryButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_CATEGORY_BUTTON;
        }

        @Override
        public GuiButtonSprites searchButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_SEARCH_BUTTON;
        }

        @Override
        public GuiButtonSprites showBookmarksButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_SHOW_BOOKMARKS_BUTTON;
        }

        @Override
        public GuiButtonSprites showRecentlyUnlockedButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_SHOW_RECENTLY_UNLOCKED_BUTTON;
        }

        @Override
        public GuiButtonSprites addBookmarkButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_ADD_BOOKMARK_BUTTON;
        }

        @Override
        public GuiButtonSprites removeBookmarkButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_REMOVE_BOOKMARK_BUTTON;
        }

        @Override
        public GuiButtonSprites readAllButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_READ_ALL_BUTTON;
        }

        @Override
        public GuiButtonSprites readNoneButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_READ_NONE_BUTTON;
        }

        @Override
        public GuiButtonSprites readUnlockedButton() {
            return GeneratedDefaultBookThemeData.OVERVIEW_READ_UNLOCKED_BUTTON;
        }
    };

    private static final BookNodeTheme NODE = new BookNodeTheme() {
        @Override
        public GuiTexture entryBackground(String spriteId) {
            return new GuiTexture(Identifier.parse(spriteId), 26, 26);
        }

        @Override
        public GuiSprite smallCurveLeftDown() {
            return GeneratedDefaultBookThemeData.NODE_SMALL_CURVE_LEFT_DOWN;
        }

        @Override
        public GuiSprite smallCurveRightDown() {
            return GeneratedDefaultBookThemeData.NODE_SMALL_CURVE_RIGHT_DOWN;
        }

        @Override
        public GuiSprite smallCurveLeftUp() {
            return GeneratedDefaultBookThemeData.NODE_SMALL_CURVE_LEFT_UP;
        }

        @Override
        public GuiSprite smallCurveRightUp() {
            return GeneratedDefaultBookThemeData.NODE_SMALL_CURVE_RIGHT_UP;
        }

        @Override
        public GuiSprite largeCurveLeftDown() {
            return GeneratedDefaultBookThemeData.NODE_LARGE_CURVE_LEFT_DOWN;
        }

        @Override
        public GuiSprite largeCurveRightDown() {
            return GeneratedDefaultBookThemeData.NODE_LARGE_CURVE_RIGHT_DOWN;
        }

        @Override
        public GuiSprite largeCurveLeftUp() {
            return GeneratedDefaultBookThemeData.NODE_LARGE_CURVE_LEFT_UP;
        }

        @Override
        public GuiSprite largeCurveRightUp() {
            return GeneratedDefaultBookThemeData.NODE_LARGE_CURVE_RIGHT_UP;
        }

        @Override
        public GuiSprite verticalLine() {
            return GeneratedDefaultBookThemeData.NODE_VERTICAL_LINE;
        }

        @Override
        public GuiSprite horizontalLine() {
            return GeneratedDefaultBookThemeData.NODE_HORIZONTAL_LINE;
        }

        @Override
        public GuiSprite upArrow() {
            return GeneratedDefaultBookThemeData.NODE_UP_ARROW;
        }

        @Override
        public GuiSprite downArrow() {
            return GeneratedDefaultBookThemeData.NODE_DOWN_ARROW;
        }

        @Override
        public GuiSprite rightArrow() {
            return GeneratedDefaultBookThemeData.NODE_RIGHT_ARROW;
        }

        @Override
        public GuiSprite leftArrow() {
            return GeneratedDefaultBookThemeData.NODE_LEFT_ARROW;
        }
    };

    private static final BookFrameTheme FRAME = new BookFrameTheme() {
        @Override
        public GuiNineSlice frame() {
            return GeneratedDefaultBookThemeData.FRAME_FRAME;
        }

        @Override
        public GuiFrameOverlay topOverlay() {
            return GeneratedDefaultBookThemeData.FRAME_TOP_OVERLAY;
        }

        @Override
        public GuiFrameOverlay bottomOverlay() {
            return GeneratedDefaultBookThemeData.FRAME_BOTTOM_OVERLAY;
        }

        @Override
        public GuiFrameOverlay leftOverlay() {
            return GeneratedDefaultBookThemeData.FRAME_LEFT_OVERLAY;
        }

        @Override
        public GuiFrameOverlay rightOverlay() {
            return GeneratedDefaultBookThemeData.FRAME_RIGHT_OVERLAY;
        }
    };

    private static final BookRecipeTheme RECIPES = new BookRecipeTheme() {
        @Override
        public GuiSprite craftingGrid() {
            return GeneratedDefaultBookThemeData.RECIPES_CRAFTING_GRID;
        }

        @Override
        public GuiSprite shapelessIcon() {
            return GeneratedDefaultBookThemeData.RECIPES_SHAPELESS_ICON;
        }

        @Override
        public GuiSprite processingRecipeBackground() {
            return GeneratedDefaultBookThemeData.RECIPES_PROCESSING_RECIPE_BACKGROUND;
        }

        @Override
        public GuiSprite smithingRecipeBackground() {
            return GeneratedDefaultBookThemeData.RECIPES_SMITHING_RECIPE_BACKGROUND;
        }

        @Override
        public GuiSprite spotlightSlot() {
            return GeneratedDefaultBookThemeData.RECIPES_SPOTLIGHT_SLOT;
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

    public static BookTheme forBook(Book book) {
        return new BookTheme() {
            @Override
            public BookContentTheme content() {
                return INSTANCE.content();
            }

            @Override
            public BookOverviewTheme overview() {
                return INSTANCE.overview();
            }

            @Override
            public BookFrameTheme frame() {
                return INSTANCE.frame();
            }

            @Override
            public BookNodeTheme node() {
                return INSTANCE.node();
            }

            @Override
            public BookRecipeTheme recipes() {
                return INSTANCE.recipes();
            }

            @Override
            public BookLayoutTheme layout() {
                return new BookLayoutTheme() {
                    @Override
                    public int bookTextOffsetX() {
                        return book.getBookTextOffsetX();
                    }

                    @Override
                    public int bookTextOffsetY() {
                        return book.getBookTextOffsetY();
                    }

                    @Override
                    public int bookTextOffsetWidth() {
                        return book.getBookTextOffsetWidth();
                    }

                    @Override
                    public int bookTextOffsetHeight() {
                        return book.getBookTextOffsetHeight();
                    }

                    @Override
                    public int categoryButtonXOffset() {
                        return book.getCategoryButtonXOffset();
                    }

                    @Override
                    public int categoryButtonYOffset() {
                        return book.getCategoryButtonYOffset();
                    }

                    @Override
                    public int searchButtonXOffset() {
                        return book.getSearchButtonXOffset();
                    }

                    @Override
                    public int searchButtonYOffset() {
                        return book.getSearchButtonYOffset();
                    }

                    @Override
                    public int readAllButtonYOffset() {
                        return book.getReadAllButtonYOffset();
                    }

                    @Override
                    public float categoryButtonIconScale() {
                        return book.getCategoryButtonIconScale();
                    }
                };
            }

            @Override
            public BookPaletteTheme palette() {
                return new BookPaletteTheme() {
                    @Override
                    public int defaultTitleColor() {
                        return book.getDefaultTitleColor();
                    }

                    @Override
                    public int defaultTextColor() {
                        return book.getDefaultTextColor();
                    }
                };
            }
        };
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
    public BookNodeTheme node() {
        return NODE;
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
