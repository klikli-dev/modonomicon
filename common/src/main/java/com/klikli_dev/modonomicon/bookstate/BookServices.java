/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.bookstate;

import com.klikli_dev.modonomicon.bookstate.access.BookStateAccess;
import com.klikli_dev.modonomicon.bookstate.access.DefaultBookStateAccess;
import com.klikli_dev.modonomicon.bookstate.interaction.BookInteractionService;
import com.klikli_dev.modonomicon.bookstate.interaction.DefaultBookInteractionService;
import com.klikli_dev.modonomicon.bookstate.visibility.BookVisibilityService;
import com.klikli_dev.modonomicon.bookstate.visibility.DefaultBookVisibilityService;

public final class BookServices {

    private static final BookStateAccess STATE_ACCESS = new DefaultBookStateAccess();
    private static final BookVisibilityService VISIBILITY = new DefaultBookVisibilityService();
    private static final BookInteractionService INTERACTION = new DefaultBookInteractionService(STATE_ACCESS);
    private BookServices() {
    }

    public static BookStateAccess stateAccess() {
        return STATE_ACCESS;
    }

    public static BookVisibilityService visibility() {
        return VISIBILITY;
    }

    public static BookInteractionService interaction() {
        return INTERACTION;
    }
}
