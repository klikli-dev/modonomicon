/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.action.ResearchAction;
import com.klikli_dev.modonomicon.research.data.ResearchCategoryDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A single requirement as presented in the requirements block of an entry.
 *
 * @param category the requirement category the row is grouped into
 * @param icons    icons to show, cycled every second if there is more than one
 * @param title    the title, first line of the tooltip
 * @param details  additional tooltip lines, shown below the title
 * @param met      whether the requirement is currently met
 * @param progress optional progress, shown as count on the icon and in the tooltip, e.g. 3/5
 * @param onClick  optional action run when the icon is clicked, ignored if {@code action} is set
 * @param missing  optional line stating what is still missing, defaults to a generic line
 * @param action   optional research action the player can perform by clicking the icon, set only for unmet facts of the current stage
 */
public record RequirementRow(
        ResearchCategoryDefinition category,
        List<RequirementIcon> icons,
        Component title,
        List<Component> details,
        boolean met,
        @Nullable Progress progress,
        @Nullable Runnable onClick,
        @Nullable Component missing,
        @Nullable ActionInfo action
) {

    /**
     * Progress of a requirement, the current value is already clamped to the target.
     */
    public record Progress(int current, int target) {
    }

    /**
     * The research action a click on the row performs.
     *
     * @param nodeId the node the fact is a requirement of
     * @param factId the fact the action satisfies
     * @param action the action attached to the fact
     */
    public record ActionInfo(Identifier nodeId, Identifier factId, ResearchAction action) {
    }

    public static RequirementRow of(List<RequirementIcon> icons, Component title) {
        return new RequirementRow(null, icons, title, List.of(), false, null, null, null, null);
    }

    public RequirementRow withCategory(ResearchCategoryDefinition category) {
        return new RequirementRow(category, this.icons, this.title, this.details, this.met, this.progress, this.onClick, this.missing, this.action);
    }

    public RequirementRow withIcons(List<RequirementIcon> icons) {
        return new RequirementRow(this.category, icons, this.title, this.details, this.met, this.progress, this.onClick, this.missing, this.action);
    }

    public RequirementRow withTitle(Component title) {
        return new RequirementRow(this.category, this.icons, title, this.details, this.met, this.progress, this.onClick, this.missing, this.action);
    }

    public RequirementRow withDetails(List<Component> details) {
        return new RequirementRow(this.category, this.icons, this.title, details, this.met, this.progress, this.onClick, this.missing, this.action);
    }

    public RequirementRow withMet(boolean met) {
        return new RequirementRow(this.category, this.icons, this.title, this.details, met, this.progress, this.onClick, this.missing, this.action);
    }

    public RequirementRow withProgress(@Nullable Progress progress) {
        return new RequirementRow(this.category, this.icons, this.title, this.details, this.met, progress, this.onClick, this.missing, this.action);
    }

    public RequirementRow withOnClick(@Nullable Runnable onClick) {
        return new RequirementRow(this.category, this.icons, this.title, this.details, this.met, this.progress, onClick, this.missing, this.action);
    }

    public RequirementRow withMissing(@Nullable Component missing) {
        return new RequirementRow(this.category, this.icons, this.title, this.details, this.met, this.progress, this.onClick, missing, this.action);
    }

    public RequirementRow withAction(@Nullable ActionInfo action) {
        return new RequirementRow(this.category, this.icons, this.title, this.details, this.met, this.progress, this.onClick, this.missing, action);
    }

    /**
     * The tooltip lines: title, category (with progress, if any), details, the stage the row belongs to and the status line.
     *
     * @param stage the title of the block the row belongs to, may be null or empty
     */
    public List<Component> tooltip(@Nullable Component stage) {
        var tooltip = new ArrayList<Component>();
        tooltip.add(this.title);
        tooltip.add(this.kindLine());
        tooltip.addAll(this.details);
        if (stage != null && !stage.getString().isEmpty()) {
            tooltip.add(stage.copy().withStyle(ChatFormatting.DARK_GRAY));
        }
        if (this.met) {
            tooltip.add(Component.translatable(Gui.REQUIREMENTS_MET).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(true)));
        } else {
            tooltip.add(this.notMetLine().withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(true)));
            tooltip.add(Component.translatable(Gui.REQUIREMENTS_NOT_MET_HINT).withStyle(ChatFormatting.GRAY));
        }
        if (this.onClick != null && this.action == null) {
            tooltip.add(Component.translatable(Gui.REQUIREMENTS_CLICK_TO_OPEN).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA).withItalic(true)));
        }
        return tooltip;
    }

    /**
     * Says what is still missing, e.g. "Not crafted yet" or "3 more to go".
     */
    private MutableComponent notMetLine() {
        if (this.progress != null) {
            return Component.translatable(Gui.REQUIREMENTS_NOT_MET_PROGRESS, Math.max(0, this.progress.target() - this.progress.current()));
        }
        if (this.missing != null) {
            return this.missing.copy();
        }
        return Component.translatable(Gui.REQUIREMENTS_NOT_MET);
    }

    private Component kindLine() {
        if (this.progress != null) {
            return Component.translatable(Gui.REQUIREMENTS_PROGRESS, this.progress.current(), this.progress.target()).withStyle(ChatFormatting.GRAY);
        }
        return this.category.title().copy().withStyle(ChatFormatting.GRAY);
    }
}
