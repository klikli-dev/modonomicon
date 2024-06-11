// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.book.BookProvider;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface BookScreenWithButtons extends BookProvider {
    void setTooltip(List<Component> tooltip);
}
