/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.page;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock.SimulateResult;
import com.klikli_dev.modonomicon.book.page.BookMultiblockPage;
import com.klikli_dev.modonomicon.client.ClientTicks;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.button.VisualizeButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.MultiblockPreviewRenderer;
import com.klikli_dev.modonomicon.client.render.state.pip.GuiMultiblockRenderState;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BookMultiblockPageRenderer extends BookPageRenderer<BookMultiblockPage> implements PageWithTextRenderer {

    private static final RandomSource randomSource = RandomSource.createThreadLocalInstance();
    private final Map<BlockPos, BlockEntity> blockEntityCache = new Object2ObjectOpenHashMap<>();
    private final Set<BlockEntity> erroredBlockEntities = Collections.newSetFromMap(new WeakHashMap<>());

    protected Pair<BlockPos, Collection<SimulateResult>> multiblockSimulation;
    protected Button visualizeButton;

    public BookMultiblockPageRenderer(BookMultiblockPage page) {
        super(page);
    }

    public void handleButtonVisualize(Button button) {
        MultiblockPreviewRenderer.setMultiblock(this.page.getMultiblock(), this.page.getMultiblockName().getComponent(), true);
        BookGuiManager.get().closeScreenStack(this.parentScreen); //will cause the book to close entirely, and save the open page

        //TODO: visualizer bookmark to go back to this page quickly?
        //String entryKey =  this.parentEntry.getId().toString(); will be used for bookmark for multiblock
//        Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
//        parent.addBookmarkButtons();
    }

    private void renderMultiblock(GuiGraphics guiGraphics) {
        var multiblock = this.page.getMultiblock();

        var facingRotation = Rotation.NONE;

        var size = multiblock.getSize();
        int sizeX = size.getX();
        int sizeY = size.getY();
        int sizeZ = size.getZ();

        // Compute scale to fit into 106x106 frame (match frame used in render())
        float maxX = 90;
        float maxY = 90;
        float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
        float scaleX = maxX / Math.max(1.0F, diag);
        float scaleY = maxY / Math.max(1.0F, sizeY);
        float scale = Math.min(scaleX, scaleY);

        // Compute animation time
        float time = this.parentScreen.getTicksInBook() * 0.5F;
        if (!Minecraft.getInstance().hasShiftDown()) {
            time += ClientTicks.partialTicks;
        }

        // Define target rectangle in screen space
        int frameX = BookEntryScreen.PAGE_WIDTH / 2 - 53;
        int frameY = 7;

        int x0 = this.parentScreen.getBookLeft() + this.left + frameX;
        int y0 = this.parentScreen.getBookTop() + this.top + frameY;
        int x1 = x0 + 106;
        int y1 = y0 + 106;

        // Build and submit PIP render state
        var state = new GuiMultiblockRenderState(
                multiblock,
                this.multiblockSimulation.getSecond(),
                size,
                time,
                facingRotation,
                null,
                this.blockEntityCache,
                this.erroredBlockEntities,
                randomSource,
                x0, y0, x1, y1,
                scale,
                guiGraphics.scissorStack.peek()
        );

        guiGraphics.guiRenderState.submitPicturesInPictureState(state);
    }

    @Override
    public int getTextY() {
        //text is always below multiblock, and we don't shift based on multiblock name (unlike title for text pages)
        return 115;
    }

    @Override
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);

        this.multiblockSimulation = this.page.getMultiblock().simulate(null, BlockPos.ZERO, Rotation.NONE, true, true);

        if (this.page.showVisualizeButton()) {
            this.addButton(this.visualizeButton = new VisualizeButton(this.parentScreen, 13, 102, this::handleButtonVisualize));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {

        //render a frame for the multiblock render area
        int x = BookEntryScreen.PAGE_WIDTH / 2 - 53;
        int y = 7;
        BookContentRenderer.drawFromContentTexture(RenderPipelines.GUI_TEXTURED, guiGraphics, this.page.getBook(), x, y, 405, 149, 106, 106);

        //render multiblock name in place of title
        if (!this.page.getMultiblockName().isEmpty()) {
            this.renderTitle(guiGraphics, this.page.getMultiblockName(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
        }

        this.renderMultiblock(guiGraphics);

        var textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.page.getText(), 0, textY, BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - textY);

        var style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
    }

    @Nullable
    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        if (pMouseX > 0 && pMouseY > 0) {
            var multiblockNameStyle = this.getClickedComponentStyleAtForTitle(this.page.getMultiblockName(), BookEntryScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY);
            if (multiblockNameStyle != null) {
                return multiblockNameStyle;
            }

            var bounds = this.getBookTextHolderBounds(0, this.getTextY(), BookEntryScreen.PAGE_WIDTH, BookEntryScreen.PAGE_HEIGHT - this.getTextY());
            var textStyle = this.getClickedComponentStyleAtForTextHolder(this.page.getText(), bounds.x, bounds.y, bounds.width, bounds.height, pMouseX, pMouseY);
            if (textStyle != null) {
                return textStyle;
            }
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY);
    }


}
