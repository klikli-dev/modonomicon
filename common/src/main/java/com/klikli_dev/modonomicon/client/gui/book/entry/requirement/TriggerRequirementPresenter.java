/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import org.jetbrains.annotations.Nullable;

/**
 * Describes a research hook that grants a fact as a requirement row. Registered per trigger type id.
 * Only icon, title, details and click action matter, the met state is determined by the requirement itself.
 */
@FunctionalInterface
public interface TriggerRequirementPresenter {
    /**
     * @return the row describing the hook, or null if the hook cannot be described
     */
    @Nullable
    RequirementRow present(ResearchHookDefinition<?> hook, RequirementContext context);
}
