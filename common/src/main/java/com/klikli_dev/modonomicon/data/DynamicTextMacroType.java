/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import net.minecraft.resources.Identifier;

public record DynamicTextMacroType(
        Identifier id,
        BookDynamicTextMacroLoader loader
) {
}
