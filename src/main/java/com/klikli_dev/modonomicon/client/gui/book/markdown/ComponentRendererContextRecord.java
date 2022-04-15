/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.client.gui.book.markdown;

import net.minecraft.network.chat.TextColor;

/**
 * @param renderSoftLineBreaks           True to render soft line breaks (deviating from MD spec). Should usually be
 *                                       false.
 * @param replaceSoftLineBreaksWithSpace True to replace soft line breaks with spaces. Should usually be true, prevents
 *                                       IDE line breaks from causing words to be rendered without spaces inbetween.
 * @param linkColor                      The color to use for http and book page links. Suggested: Blue: 0x5555FF
 */
public record ComponentRendererContextRecord(boolean renderSoftLineBreaks, boolean replaceSoftLineBreaksWithSpace,
                                             TextColor linkColor) {
    //TODO: make renderSoftLineBreaks a book level option
}
