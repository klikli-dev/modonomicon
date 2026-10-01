/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.associated;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.associated.AssociatedItemLookup;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookEntityPage;
import com.klikli_dev.modonomicon.book.page.BookImagePage;
import com.klikli_dev.modonomicon.book.page.BookMultiblockPage;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.client.ModonomiconKeys;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.entry.EntryDisplayState;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Optional;

/**
 * Shared logic for associated-item tooltips and hold-to-open.
 * <p>
 * Used by the loader tooltip hooks (Neo/Forge {@code RenderTooltipEvent.GatherComponents},
 * Fabric item tooltip callback + item stack mixin) and the client tick hold tracker.
 */
public class AssociatedTooltipHelper {

    /**
     * Builds the complete tooltip component for the stack: title plus hold hint
     * (with live countdown) or locked hint. Empty if there is no association.
     */
    public static Optional<TooltipComponent> gather(ItemStack stack) {
        var association = AssociatedItemLookup.get().find(stack);
        if (association == null) {
            return Optional.empty();
        }

        var entry = AssociatedItemLookup.get().resolveEntry(association);
        if (entry == null) {
            return Optional.empty();
        }

        var page = AssociatedItemLookup.get().resolvePage(association);
        Component title = resolveTitle(entry, page);
        Component hint = isLocked(entry)
                ? Component.translatable(ModonomiconConstants.I18n.Tooltips.ASSOCIATED_LOCKED)
                        .withStyle(ChatFormatting.GRAY)
                : holdHint(stack);
        return Optional.of(new AssociatedEntryTooltip(association, title, hint));
    }

    private static Component holdHint(ItemStack stack) {
        var keyName = ModonomiconKeys.OPEN_ASSOCIATED_ENTRY.getTranslatedKeyMessage();
        //before holding show the full duration with one decimal ("2.0s"), while holding
        //count down with millisecond precision ("0.678s") so players see the hold register
        boolean holding = AssociatedHoverTracker.isHoldingFor(stack);
        double seconds = holding
                ? AssociatedHoverTracker.holdRemainingSeconds(stack)
                : AssociatedHoverTracker.holdTotalSeconds();
        String formatted = String.format(java.util.Locale.ROOT, holding ? "%.3f" : "%.1f", seconds);
        return Component.translatable(ModonomiconConstants.I18n.Tooltips.ASSOCIATED_HOLD_TO_OPEN,
                        keyName, formatted)
                .withStyle(ChatFormatting.GRAY);
    }

    /**
     * Opens the book at the association for the given stack. Returns false if there is
     * nothing to open (no match, unknown entry, or locked).
     */
    public static boolean openFromHover(ItemStack stack) {
        var association = AssociatedItemLookup.get().find(stack);
        if (association == null) {
            return false;
        }

        var entry = AssociatedItemLookup.get().resolveEntry(association);
        if (entry == null || isLocked(entry)) {
            return false;
        }

        BookGuiManager.get().openEntry(association.bookId(), entry.getCategoryId(), association.entryId(),
                association.pageNumber() >= 0 ? association.pageNumber() : 0);
        return true;
    }

    /**
     * True if the entry is not yet unlocked for the client player.
     * Fails open (returns false) when the player is unavailable.
     */
    public static boolean isLocked(BookEntry entry) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        return entry.getEntryDisplayState(player) != EntryDisplayState.UNLOCKED;
    }

    /**
     * Resolves the icon to render: the page item for spotlight pages, otherwise the entry icon.
     */
    public static BookIcon resolveIcon(BookEntry entry, BookPage page) {
        if (page instanceof BookSpotlightPage spotlight) {
            ItemStack stack = spotlight.getCachedItemStack();
            if (stack == null || stack.isEmpty()) {
                var level = Minecraft.getInstance().level;
                if (level != null) {
                    stack = spotlight.getItem().map(
                            template -> template.create(),
                            ingredient -> {
                                var display = ingredient.display()
                                        .resolveForFirstStack(
                                                net.minecraft.world.item.crafting.display.SlotDisplayContext.fromLevel(level));
                                return display;
                            });
                }
            }
            if (stack != null && !stack.isEmpty()) {
                return new BookIcon(ItemStackTemplate.fromNonEmptyStack(stack));
            }
        }
        return entry.getIcon();
    }

    /**
     * Resolves the title to show: the page title when it has one, otherwise the entry name.
     */
    public static Component resolveTitle(BookEntry entry, BookPage page) {
        Component fallback = Component.translatable(entry.getName());
        if (page == null) {
            return fallback;
        }

        if (page instanceof BookTextPage textPage) {
            return holderComponent(textPage.getTitle(), fallback);
        }
        if (page instanceof BookSpotlightPage spotlightPage) {
            return holderComponent(spotlightPage.getTitle(), fallback);
        }
        if (page instanceof BookImagePage imagePage) {
            return holderComponent(imagePage.getTitle(), fallback);
        }
        if (page instanceof BookEntityPage entityPage) {
            return holderComponent(entityPage.getEntityName(), fallback);
        }
        if (page instanceof BookMultiblockPage multiblockPage) {
            return holderComponent(multiblockPage.getMultiblockName(), fallback);
        }
        if (page instanceof BookRecipePage<?> recipePage) {
            Component title = holderComponent(recipePage.getTitle1(), null);
            if (title == null) {
                title = holderComponent(recipePage.getTitle2(), null);
            }
            return title == null ? fallback : title;
        }
        return fallback;
    }

    private static Component holderComponent(BookTextHolder holder, Component fallback) {
        if (holder == null || holder.isEmpty()) {
            return fallback;
        }
        if (holder.hasComponent() && holder.getComponent() != null) {
            return holder.getComponent();
        }
        String key = holder.getKey();
        if (key != null && !key.isEmpty()) {
            return Component.translatable(key);
        }
        String text = holder.getString();
        if (text == null || text.isEmpty()) {
            return fallback;
        }
        return Component.literal(text);
    }
}
