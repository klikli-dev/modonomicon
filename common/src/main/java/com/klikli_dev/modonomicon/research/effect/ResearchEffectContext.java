/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.research.effect;

import org.jetbrains.annotations.Nullable;
import net.minecraft.resources.Identifier;

/**
 * Where an effect is applied from.
 *
 * @param factId the fact an action is performed for, null for stage rewards
 */
public record ResearchEffectContext(@Nullable Identifier factId) {

    public static final ResearchEffectContext NONE = new ResearchEffectContext(null);
}
