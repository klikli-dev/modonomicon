/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.research.data.ResearchGroupDefinition;
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
 * @param group the requirement group the row is grouped into
 * @param icons    icons to show, cycled every second if there is more than one
 * @param title    the title, first line of the tooltip
 * @param details  additional tooltip lines, shown below the title
 * @param met      whether the requirement is currently met
 * @param progress optional progress, shown as count on the icon and in the tooltip, e.g. 3/5
 * @param missing  optional line stating what is still missing, defaults to a generic line
 * @param action   optional action the click on the row asks for, see {@link ScholarshipRowAction}
 * @param factId   the research fact the row is a requirement for, or null if the row is not a fact. Used to grant the fact to players with game master permission
 */
public record ScholarshipRow(
        ResearchGroupDefinition group,
        List<ScholarshipIcon> icons,
        Component title,
        List<Component> details,
        boolean met,
        @Nullable Progress progress,
        @Nullable Component missing,
        @Nullable ScholarshipRowAction action,
        @Nullable Identifier factId
) {

    /**
     * Progress of a requirement, the current value is already clamped to the target.
     */
    public record Progress(int current, int target) {
    }

    public static ScholarshipRow of(List<ScholarshipIcon> icons, Component title) {
        return new ScholarshipRow(null, icons, title, List.of(), false, null, null, null, null);
    }

    public ScholarshipRow withGroup(ResearchGroupDefinition group) {
        return new ScholarshipRow(group, this.icons, this.title, this.details, this.met, this.progress, this.missing, this.action, this.factId);
    }

    public ScholarshipRow withIcons(List<ScholarshipIcon> icons) {
        return new ScholarshipRow(this.group, icons, this.title, this.details, this.met, this.progress, this.missing, this.action, this.factId);
    }

    public ScholarshipRow withTitle(Component title) {
        return new ScholarshipRow(this.group, this.icons, title, this.details, this.met, this.progress, this.missing, this.action, this.factId);
    }

    public ScholarshipRow withDetails(List<Component> details) {
        return new ScholarshipRow(this.group, this.icons, this.title, details, this.met, this.progress, this.missing, this.action, this.factId);
    }

    public ScholarshipRow withMet(boolean met) {
        return new ScholarshipRow(this.group, this.icons, this.title, this.details, met, this.progress, this.missing, this.action, this.factId);
    }

    public ScholarshipRow withProgress(@Nullable Progress progress) {
        return new ScholarshipRow(this.group, this.icons, this.title, this.details, this.met, progress, this.missing, this.action, this.factId);
    }

    public ScholarshipRow withMissing(@Nullable Component missing) {
        return new ScholarshipRow(this.group, this.icons, this.title, this.details, this.met, this.progress, missing, this.action, this.factId);
    }

    public ScholarshipRow withAction(@Nullable ScholarshipRowAction action) {
        return new ScholarshipRow(this.group, this.icons, this.title, this.details, this.met, this.progress, this.missing, action, this.factId);
    }

    /**
     * The tooltip lines: title, group (with progress, if any), details, the stage the row belongs to and the status line.
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
            tooltip.add(Component.translatable(Gui.SCHOLARSHIP_MET).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(true)));
        } else {
            tooltip.add(this.notMetLine().withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(true)));
            tooltip.add(Component.translatable(Gui.SCHOLARSHIP_NOT_MET_HINT).withStyle(ChatFormatting.GRAY));
        }
        if (this.action instanceof ScholarshipRowAction.OpenEntry) {
            tooltip.add(Component.translatable(Gui.SCHOLARSHIP_CLICK_TO_OPEN).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA).withItalic(true)));
        }
        return tooltip;
    }

    /**
     * Says what is still missing, e.g. "Not crafted yet" or "3 more to go".
     */
    private MutableComponent notMetLine() {
        if (this.progress != null) {
            return Component.translatable(Gui.SCHOLARSHIP_NOT_MET_PROGRESS, Math.max(0, this.progress.target() - this.progress.current()));
        }
        if (this.missing != null) {
            return this.missing.copy();
        }
        return Component.translatable(Gui.SCHOLARSHIP_NOT_MET);
    }

    private Component kindLine() {
        if (this.progress != null) {
            return Component.translatable(Gui.SCHOLARSHIP_PROGRESS, this.progress.current(), this.progress.target()).withStyle(ChatFormatting.GRAY);
        }
        return this.group.title().copy().withStyle(ChatFormatting.GRAY);
    }
}
