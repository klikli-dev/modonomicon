/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research;

import com.klikli_dev.modonomicon.research.hook.ResearchHookService;
import com.klikli_dev.modonomicon.research.state.ResearchStateManager;

public final class ResearchServices {

    private static final ResearchStateManager STATE = ResearchStateManager.get();
    private static final ResearchHookService HOOKS = new ResearchHookService(STATE);

    private ResearchServices() {
    }

    public static ResearchStateManager state() {
        return STATE;
    }

    public static ResearchHookService hooks() {
        return HOOKS;
    }
}
