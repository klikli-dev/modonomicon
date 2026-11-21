/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;

/**
 * Trigger-bound research hook step under its old name.
 *
 * @deprecated Use {@link ResearchHookBuilder} instead. Will be removed in Minecraft 26.3.
 */
@Deprecated(forRemoval = true)
public class ResearchIngress<TTarget> extends ResearchHookBuilder<TTarget> {
    /**
     * @deprecated Use {@link ResearchHookBuilder} instead. Will be removed in Minecraft 26.3.
     */
    @Deprecated(forRemoval = true)
    public ResearchIngress(ResearchDataBuilder research, TriggerType<TTarget, ?> triggerType, TTarget target) {
        super(research, triggerType, target);
    }
}