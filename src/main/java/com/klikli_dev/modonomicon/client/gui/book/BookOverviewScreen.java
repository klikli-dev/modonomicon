/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */
package com.klikli_dev.modonomicon.client.gui.book;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.capability.BookStateCapability;
import com.klikli_dev.modonomicon.capability.BookUnlockCapability;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.button.CategoryButton;
import com.klikli_dev.modonomicon.client.gui.book.button.ReadAllButton;
import com.klikli_dev.modonomicon.client.gui.book.button.SearchButton;
import com.klikli_dev.modonomicon.network.Networking;
import com.klikli_dev.modonomicon.network.messages.ClickReadAllButtonMessage;
import com.klikli_dev.modonomicon.network.messages.SaveBookStateMessage;
import com.klikli_dev.modonomicon.network.messages.SyncBookUnlockCapabilityMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BookOverviewScreen extends Screen {

    private final Book book;
    private final ResourceLocation bookOverviewTexture;
    private final EntryConnectionRenderer connectionRenderer = new EntryConnectionRenderer();
    private final List<BookCategory> categories;
    private final List<BookCategoryScreen> categoryScreens;

    //TODO: make the frame thickness configurable in the book?
    private final int frameThicknessW = 14;
    private final int frameThicknessH = 14;
    private int currentCategory = 0;

    private boolean hasUnreadEntries;
    private boolean hasUnreadUnlockedEntries;

    public BookOverviewScreen(Book book) {
        super(Component.literal(""));

        //somehow there are render calls before init(), leaving minecraft null
        this.minecraft = Minecraft.getInstance();

        this.book = book;

        this.bookOverviewTexture = book.getBookOverviewTexture();

        //we only show categories that are unlocked. Unlike entries there is no "greying out"
        this.categories = book.getCategoriesSorted().stream().filter(cat -> BookUnlockCapability.isUnlockedFor(this.minecraft.player, cat)).toList();
        this.categoryScreens = this.categories.stream().map(c -> new BookCategoryScreen(this, c)).toList();
    }

    public void onDisplay() {
        this.loadBookState();

        this.updateUnreadEntriesState();

        var currentScreen = this.categoryScreens.get(this.currentCategory);
        currentScreen.onDisplay();
    }

    protected void updateUnreadEntriesState(){
        //check if ANY entry is unread
        this.hasUnreadEntries = this.book.getEntries().values().stream().anyMatch(e -> !BookUnlockCapability.isReadFor(this.minecraft.player, e));

        //check if any currently unlocked entry is unread
        this.hasUnreadUnlockedEntries = this.book.getEntries().values().stream().anyMatch(e ->
                BookUnlockCapability.isUnlockedFor(this.minecraft.player, e) &&
                        !BookUnlockCapability.isReadFor(this.minecraft.player, e));
    }
    public EntryConnectionRenderer getConnectionRenderer() {
        return this.connectionRenderer;
    }

    public BookCategoryScreen getCurrentCategoryScreen() {
        return this.categoryScreens.get(this.currentCategory);
    }

    public int getCurrentCategory() {
        return this.currentCategory;
    }

    public Book getBook() {
        return this.book;
    }

    public ResourceLocation getBookOverviewTexture() {
        return this.bookOverviewTexture;
    }

    /**
     * gets the x coordinate of the inner area of the book frame
     */
    public int getInnerX() {
        return (this.width - this.getFrameWidth()) / 2 + this.frameThicknessW / 2;
    }

    /**
     * gets the y coordinate of the inner area of the book frame
     */
    public int getInnerY() {
        return (this.height - this.getFrameHeight()) / 2 + this.frameThicknessH / 2;
    }

    /**
     * gets the width of the inner area of the book frame
     */
    public int getInnerWidth() {
        return this.getFrameWidth() - this.frameThicknessW;
    }

    /**
     * gets the height of the inner area of the book frame
     */
    public int getInnerHeight() {
        return this.getFrameHeight() - this.frameThicknessH;
    }

    public int getFrameThicknessW() {
        return this.frameThicknessW;
    }

    public int getFrameThicknessH() {
        return this.frameThicknessH;
    }

    public void changeCategory(BookCategory category) {
        int index = this.categories.indexOf(category);
        if (index != -1) {
            this.changeCategory(index);
        } else {
            Modonomicon.LOGGER.warn("Tried to change to a category ({}) that does not exist in this book ({}).", this.book.getId(), category.getId());
        }
    }

    public void changeCategory(int categoryIndex) {
        var oldIndex = this.currentCategory;
        this.currentCategory = categoryIndex;
        this.onCategoryChanged(oldIndex, this.currentCategory);
    }

    public void onCategoryChanged(int oldIndex, int newIndex) {
        var oldScreen = this.categoryScreens.get(oldIndex);
        oldScreen.onClose();

        var newScreen = this.categoryScreens.get(newIndex);
        newScreen.onDisplay();

        //TODO: SFX for category change?
    }

    /**
     * Gets the outer width of the book frame
     */
    protected int getFrameWidth() {
        //TODO: enable config frame width
        return this.width - 60;
    }

    /**
     * Gets the outer height of the book frame
     */
    protected int getFrameHeight() {
        //TODO: enable config frame height
        return this.height - 20;
    }

    protected void renderFrame(PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.bookOverviewTexture);

        int width = this.getFrameWidth();
        int height = this.getFrameHeight();
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        int blitOffset = this.getBlitOffset();

        //draw a resizeable border. Center parts of each side will be stretched
        //the exact border size mostly does not matter because the center is empty anyway, but 50 gives a lot of flexiblity
        ScreenUtils.blitWithBorder(poseStack, x, y, 0, 0, width, height,
                140, 140, 50, 50, 50, 50, blitOffset);

        //now draw center parts of each side. This allows to add non-repeat features here or just generally bridge the repeated pixesl
        //top, bottom, left, right
        //these parts are to the left of the repeatable frame in the texture.
        ScreenUtils.drawTexturedModalRect(poseStack, (x + (width / 2)) - 36, y, 140, 0, 72, 17, blitOffset);
        ScreenUtils.drawTexturedModalRect(poseStack, (x + (width / 2)) - 36, (y + height) - 18, 140, 17, 72, 18, blitOffset);
        ScreenUtils.drawTexturedModalRect(poseStack, x, (y + (height / 2)) - 35, 140, 35, 17, 70, blitOffset);
        ScreenUtils.drawTexturedModalRect(poseStack, x + width - 17, (y + (height / 2)) - 35, 157, 35, 17, 70, blitOffset);
    }

    protected void onBookCategoryButtonClick(CategoryButton button) {
        this.changeCategory(button.getCategoryIndex());
    }

    protected void onBookCategoryButtonTooltip(CategoryButton button, PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.renderTooltip(pPoseStack, Component.translatable(button.getCategory().getName()), pMouseX, pMouseY);
    }

    protected void onReadAllButtonClick(ReadAllButton button) {
        if (this.hasUnreadUnlockedEntries) {
            Networking.sendToServer(new ClickReadAllButtonMessage(this.book.getId(), false));
            this.hasUnreadUnlockedEntries = false;
        } else if (this.hasUnreadEntries && Screen.hasShiftDown()) {
            Networking.sendToServer(new ClickReadAllButtonMessage(this.book.getId(), true));
            this.hasUnreadEntries = false;
        }
    }

    protected void onReadAllButtonTooltip(ReadAllButton button, PoseStack poseStack, int mouseX, int mouseY) {
        this.renderTooltip(poseStack, button.getTooltip(), mouseX, mouseY);
    }

    protected boolean canSeeReadAllButton() {
        return this.hasUnreadEntries || this.hasUnreadUnlockedEntries;
    }

    private void loadBookState() {
        var state = BookStateCapability.getBookStateFor(this.minecraft.player, this.book);
        if (state != null) {
            if (state.openCategory != null) {
                var openCategory = this.book.getCategory(state.openCategory);
                if (openCategory != null) {
                    this.currentCategory = this.categories.indexOf(openCategory);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        //ignore return value, because we need our base class to handle dragging and such
        this.getCurrentCategoryScreen().mouseClicked(pMouseX, pMouseY, pButton);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return this.getCurrentCategoryScreen().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.getCurrentCategoryScreen().zoom(pDelta);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);

        this.getCurrentCategoryScreen().renderBackground(pPoseStack);

        this.renderFrame(pPoseStack);

        this.getCurrentCategoryScreen().render(pPoseStack, pMouseX, pMouseY, pPartialTick);


        //do super render last -> it does the widgets including especially the tooltips and we want those to go over the frame
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        this.getCurrentCategoryScreen().onClose();
        Networking.sendToServer(new SaveBookStateMessage(this.book, this.getCurrentCategoryScreen().getCategory().getId()));

        BookGuiManager.get().resetHistory();

        BookGuiManager.get().openOverviewScreen = null;

        super.onClose();
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        return super.handleComponentClicked(pStyle);
    }

    public void onSyncBookUnlockCapabilityMessage(SyncBookUnlockCapabilityMessage message) {
        //this leads to re-init of the category buttons after a potential unlock
        this.rebuildWidgets();
        this.updateUnreadEntriesState();
    }

    @Override
    protected void init() {
        super.init();

        BookGuiManager.get().openOverviewScreen = this;

        int buttonXOffset = -11;

        int buttonX = (this.width - this.getFrameWidth()) / 2 - this.getFrameThicknessW() + buttonXOffset;
        int buttonY = (this.height - this.getFrameHeight()) / 2 - this.getFrameThicknessH() + 30;
        //calculate button width so it aligns with the outer edge of the frame
        int buttonWidth = (this.width - this.getFrameWidth()) / 2 + buttonXOffset + 6;
        int buttonHeight = 20;
        int buttonSpacing = 2;

        int buttonCount = 0;
        for (int i = 0, size = this.categories.size(); i < size; i++) {
            if (this.categories.get(i).showCategoryButton() && BookUnlockCapability.isUnlockedFor(this.minecraft.player, this.categories.get(i))) {
                var button = new CategoryButton(this, this.categories.get(i), i,
                        buttonX, buttonY + (buttonHeight + buttonSpacing) * buttonCount, buttonWidth, buttonHeight,
                        (b) -> this.onBookCategoryButtonClick((CategoryButton) b),
                        (b, stack, x, y) -> this.onBookCategoryButtonTooltip((CategoryButton) b, stack, x, y));

                this.addRenderableWidget(button);
                buttonCount++;
            }
        }

        int readAllButtonX = this.getFrameWidth() + this.getFrameThicknessW() + ReadAllButton.WIDTH / 2 - 3; //(this.width - this.getFrameWidth()); // / 2 - this.getFrameThicknessW() + buttonXOffset;
        int readAllButtonY = (this.height - this.getFrameHeight()) / 2 + ReadAllButton.HEIGHT / 2;

        var readAllButton = new ReadAllButton(this, readAllButtonX, readAllButtonY,
                () -> this.hasUnreadUnlockedEntries, //if we have unlocked entries that are not read -> blue
                this::canSeeReadAllButton, //display condition -> if we have any unlocked entries -> grey
                (b) -> this.onReadAllButtonClick((ReadAllButton) b),
                (b, stack, x, y) -> this.onReadAllButtonTooltip((ReadAllButton) b, stack, x, y));

        this.addRenderableWidget(readAllButton);


        int searchButtonXOffset = 7;
        int searchButtonYOffset = -30;
        int searchButtonX = this.getFrameWidth() + this.getFrameThicknessW() + ReadAllButton.WIDTH / 2 + searchButtonXOffset;
        int searchButtonY = this.getFrameHeight() + this.getFrameThicknessH() - ReadAllButton.HEIGHT / 2 + searchButtonYOffset;
        int searchButtonWidth = 44; //width in png
        int scissorX = this.getFrameWidth() + this.getFrameThicknessW() * 2 + 2; //this is the render location of our frame so our search button never overlaps

        var searchButton = new SearchButton(this, searchButtonX, searchButtonY,
                scissorX,
                searchButtonWidth, buttonHeight,
                (b) -> this.onSearchButtonClick((SearchButton) b),
                (b, stack, x, y) -> this.onSearchButtonTooltip((SearchButton) b, stack, x, y));

        this.addRenderableWidget(searchButton);
    }

    protected void onSearchButtonClick(SearchButton button) {
        ForgeHooksClient.pushGuiLayer(this.getMinecraft(), new BookSearchScreen(this));
    }

    protected void onSearchButtonTooltip(SearchButton button, PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.renderTooltip(pPoseStack, Component.translatable(ModonomiconConstants.I18n.Gui.OPEN_SEARCH), pMouseX, pMouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
