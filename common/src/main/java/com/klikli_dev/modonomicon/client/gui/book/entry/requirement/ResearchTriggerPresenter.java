/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;

/**
 * Describes a research hook that grants a fact or increments a value as a requirement row. Registered per trigger type id,
 * see {@link ResearchTriggerPresenterRegistry}. Only icon, title, details and click action matter, the met state is
 * determined by the requirement itself.
 */
@FunctionalInterface
public interface ResearchTriggerPresenter {
    /**
     * @return the row describing the hook, never null; unknown trigger types get a generic row
     */
    RequirementRow present(ResearchHookDefinition<?> hook, RequirementContext context);
}
