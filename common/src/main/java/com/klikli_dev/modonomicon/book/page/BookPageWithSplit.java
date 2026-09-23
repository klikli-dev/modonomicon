/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.page;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookTextHolder;

/**
 * Implemented by pages that render a {@link BookTextHolder} body and support
 * opt-in client-side page splitting as an alternative to scale-to-fit.
 */
public interface BookPageWithSplit {
    /**
     * @return nullable per-page override for auto-scaling, null means inherit the book default.
     */
    Boolean getAutoScaleOverride();

    /**
     * @return nullable per-page override for page splitting, null means inherit the book default.
     */
    Boolean getAllowPageSplitOverride();

    /**
     * @return the body text holder that is measured and split.
     */
    BookTextHolder getText();

    /**
     * Splitting takes precedence over auto-scaling.
     */
    default boolean isSplitEnabled(Book book) {
        var override = this.getAllowPageSplitOverride();
        if (override != null) {
            return override;
        }
        return book != null && book.defaultAllowPageSplit();
    }

    default boolean isAutoScaleEnabled(Book book) {
        if (this.isSplitEnabled(book)) {
            return false;
        }
        var override = this.getAutoScaleOverride();
        if (override != null) {
            return override;
        }
        return book == null || book.defaultAutoScale();
    }
}
