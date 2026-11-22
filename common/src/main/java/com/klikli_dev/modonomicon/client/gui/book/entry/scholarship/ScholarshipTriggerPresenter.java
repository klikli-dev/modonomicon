/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;

/**
 * Describes a research hook that grants a fact or increments a value as a requirement row. Registered per trigger type id,
 * see {@link ScholarshipTriggerPresenterRegistry}. Only icon, title, details and click action matter, the met state is
 * determined by the requirement itself.
 */
@FunctionalInterface
public interface ScholarshipTriggerPresenter {
    /**
     * @return the row describing the hook, never null; unknown trigger types get a generic row
     */
    ScholarshipRow present(ResearchHookDefinition<?> hook, ScholarshipContext context);
}
