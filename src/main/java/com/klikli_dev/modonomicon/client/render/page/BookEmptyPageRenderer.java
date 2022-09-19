/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.book.page.BookEmptyPage;
import com.mojang.blaze3d.vertex.PoseStack;

public class BookEmptyPageRenderer extends BookPageRenderer<BookEmptyPage>  {
    public BookEmptyPageRenderer(BookEmptyPage page) {
        super(page);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {

    }
}
