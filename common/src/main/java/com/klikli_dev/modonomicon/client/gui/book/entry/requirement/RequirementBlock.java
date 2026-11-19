/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.research.data.ResearchCost;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A block of requirement rows, e.g. the requirements of the next stage of an entry progression.
 *
 * @param title       the title of the block, e.g. the next stage, shown in the tooltips and as page title
 * @param rows        the requirement rows, empty if the block only states a condition
 * @param complete    true if everything the block describes is done, shown as a check mark
 * @param description optional description of the block, shown in the tooltip of the separator
 * @param stage       optional stage line below the rows, null if the block is not part of a staged progression
 */
public record RequirementBlock(Component title, List<RequirementRow> rows, boolean complete, @Nullable Component description, @Nullable StageLine stage) {

    public RequirementBlock(Component title, List<RequirementRow> rows, boolean complete) {
        this(title, rows, complete, null, null);
    }

    public RequirementBlock(Component title, List<RequirementRow> rows, boolean complete, @Nullable Component description) {
        this(title, rows, complete, description, null);
    }

    /**
     * An empty block, nothing to show.
     */
    public static RequirementBlock empty() {
        return new RequirementBlock(Component.empty(), List.of(), false);
    }

    /**
     * True if the block has nothing to show. A block with a stage line is shown even without rows, e.g. a manual stage
     * without requirements that is completed by its button.
     */
    public boolean isEmpty() {
        return this.rows.isEmpty() && !this.complete && this.stage == null;
    }

    /**
     * The stage line of a staged block.
     *
     * @param index         0-based index of the stage the block shows
     * @param total         number of stages of the progression
     * @param lastBrowsable the index shown by default, browsing is limited to the stages up to it
     * @param navigable     whether the stage line offers browsing, false for pages that show one fixed stage
     * @param title         title of the shown stage, shown in the tooltip of the stage line
     * @param description   optional description of the shown stage, shown in the tooltip of the stage line
     * @param manual        set if the shown stage is completed by hand, null for automatic stages and browsed stages
     */
    public record StageLine(int index, int total, int lastBrowsable, boolean navigable, Component title, @Nullable Component description, @Nullable ManualStage manual) {
    }

    /**
     * The manual completion of the shown stage, offered as a button once its requirements are met.
     *
     * @param nodeId     the node the stage belongs to
     * @param stageIndex 0-based index of the stage, used to detect when a completion has been synced
     * @param cost       the cost consumed on completion
     * @param ready      whether the requirements of the stage are met, the button is only shown then
     */
    public record ManualStage(Identifier nodeId, int stageIndex, ResearchCost cost, boolean ready) {
    }
}
