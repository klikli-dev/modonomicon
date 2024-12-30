/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.data;

import java.util.Map;

public interface BookDynamicTextMacroLoader {
    Map<String, String> load();
}
