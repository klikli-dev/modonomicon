/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.requirement;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Research;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.data.ResearchCategories;
import com.klikli_dev.modonomicon.research.data.ResearchCategoryDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.research.data.ResearchDisplay;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchStageDefinition;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Builds requirement rows for research facts, values, nodes and stages.
 * <p>
 * Title and icon of facts and values resolve in this order: the definition's display data, the default
 * translation key (research_fact / research_value / research_node), then the hooks that grant or increment them.
 */
public final class ResearchRequirementRows {

    private static final RequirementIcon GENERIC_RESEARCH_ICON = RequirementIcon.of(new ItemStack(Items.BOOK));
    private static final Set<Identifier> UNKNOWN_CATEGORIES = ConcurrentHashMap.newKeySet();

    private ResearchRequirementRows() {
    }

    /**
     * A row for a research fact. Met if the fact is granted, or if forceMet is set.
     */
    public static RequirementRow fact(Identifier factId, RequirementContext context, boolean forceMet) {
        return fact(factId, context, forceMet, null);
    }

    /**
     * A row for a research fact. If actionNode is set, the fact's display has an action and the row offers it when
     * the action is repeatable, or when the fact is unmet and its stage is the active one.
     * Callers pass the node only if the stage of the fact is reached, so future stages never offer actions.
     */
    public static RequirementRow fact(Identifier factId, RequirementContext context, boolean forceMet, @Nullable Identifier actionNode) {
        return fact(factId, context, forceMet, actionNode, true);
    }

    private static RequirementRow fact(Identifier factId, RequirementContext context, boolean forceMet, @Nullable Identifier actionNode, boolean active) {
        var definition = context.data().factDefinitions().get(factId);
        var display = definition != null ? definition.display().orElse(ResearchDisplay.EMPTY) : ResearchDisplay.EMPTY;
        var hookRows = hookRows(context.hooksGranting(factId), context);
        var hookRow = hookRows.isEmpty() ? null : hookRows.get(0);

        var title = display.title()
                .or(() -> translated(Research.factKey(factId)))
                .or(() -> hookRow != null ? Optional.of(hookRow.title()) : Optional.empty())
                .orElseGet(() -> Component.literal(factId.getPath()));

        var category = rowCategory(display, hookRow, ResearchCategories.RESEARCH, context);

        boolean met = forceMet || context.state().hasFact(factId);
        var row = new RequirementRow(
                category,
                iconsOr(display.icon(), hookRows),
                title,
                descriptionOr(display, hookRow != null ? hookRow.details() : List.of()),
                met,
                null,
                firstClickAction(hookRows),
                hookRow != null ? hookRow.missing() : null,
                null
        );
        if (actionNode != null) {
            var action = display.action();
            if (action.isPresent() && (action.get().repeatable() || (active && !met))) {
                row = row.withAction(new RequirementRow.ActionInfo(actionNode, factId, action.get()));
            }
        }
        return row;
    }

    /**
     * A row for a research value, showing its progress towards the threshold.
     */
    public static RequirementRow value(ResearchNodeDefinition.ValueRequirement requirement, RequirementContext context, boolean forceMet) {
        var valueId = requirement.valueId();
        var threshold = requirement.threshold();
        var definition = context.data().valueDefinitions().get(valueId);
        var display = definition != null ? definition.display().orElse(ResearchDisplay.EMPTY) : ResearchDisplay.EMPTY;
        var hookRows = hookRows(context.hooksIncrementing(valueId), context);
        var hookRow = hookRows.isEmpty() ? null : hookRows.get(0);

        var current = context.state().getValue(valueId);
        var shown = forceMet ? threshold : Math.min(current, threshold);

        var title = display.title()
                .or(() -> translated(Research.valueKey(valueId)))
                .orElseGet(() -> Component.literal(valueId.getPath()));

        return new RequirementRow(
                rowCategory(display, hookRow, ResearchCategories.PROGRESS, context),
                iconsOr(display.icon(), hookRows),
                title,
                descriptionOr(display, List.of()),
                forceMet || current >= threshold,
                new RequirementRow.Progress(shown, threshold),
                null,
                null,
                null
        );
    }

    /**
     * The requirement block of an entry progression: the stage dependencies of the node, then the requirements of its
     * next stage, titled by that stage. A completed node is marked complete and shows the requirements of its last stage, all met.
     * <p>
     * The state stage index is 1-based while a stage is active (index N = list index N-1) and exceeds the stage count once the node is complete.
     * Before the node is unlocked the first stage is the next stage.
     *
     * @param browsed the 0-based stage index to show instead of the next stage, or null to show the next stage
     */
    public static RequirementBlock progression(Identifier nodeId, @Nullable Integer browsed, RequirementContext context) {
        var node = context.data().nodeDefinitions().get(nodeId);
        if (node == null) {
            return new RequirementBlock(Component.translatable(Gui.REQUIREMENTS_NEXT_STEPS), List.of(unknown(nodeId, context)), false);
        }

        int total = node.stages().size();
        int stageIndex = context.state().getNodeStageIndex(nodeId);
        boolean complete = stageIndex > total;
        int current = total == 0 ? 0 : Math.min(Math.max(stageIndex - 1, 0), total - 1);
        int shown = browsed != null && total > 0 ? Math.min(browsed, total - 1) : (total > 0 ? current : 0);
        boolean browsing = browsed != null && total > 0;

        var rows = new ArrayList<RequirementRow>();
        for (var dependency : node.requiredStages()) {
            stageDependency(dependency, context, rows::add);
        }

        if (total == 0) {
            for (var factId : node.requiredFacts()) {
                rows.add(fact(factId, context, false));
            }
            for (var requirement : node.requiredValues()) {
                rows.add(value(requirement, context, false));
            }
            return new RequirementBlock(Component.translatable(Gui.REQUIREMENTS_TITLE), rows, false);
        }

        var stage = node.stages().get(shown);
        //stages before the current one are done, the current one is only done once the node is complete
        boolean done = complete || shown < current;
        //only the current stage offers non-repeatable actions, a browsed or completed stage is read-only apart from repeatable actions
        boolean active = !done && !browsing;
        var manual = active && stage.completion().isManual()
                ? new RequirementBlock.ManualStage(nodeId, shown, stage.completion().cost(), requirementsMet(stage, context))
                : null;
        stageRequirements(stage, context, done, nodeId, active, stage.completion().isManual() ? stage.completion().cost() : null, rows::add);
        rewardRows(stage.rewards(), done, context, rows::add);
        var description = stageDescription(stage);
        if (complete && !browsing) {
            //a completed progression keeps showing the steps of its last stage, all met, so the player sees what was done
            return new RequirementBlock(Component.translatable(Gui.REQUIREMENTS_COMPLETE), rows, true, description, stageLine(node, shown, current, null));
        }
        var title = browsing || complete ? stageTitle(stage, false) : stageTitle(stage, true);
        return new RequirementBlock(title, rows, false, description, stageLine(node, shown, current, manual));
    }

    /**
     * The requirement block of one stage, e.g. for a stage requirement page. Completed stages show all rows met.
     */
    public static RequirementBlock stageBlock(Identifier stageId, RequirementContext context) {
        var data = context.data();
        var location = data.stageLocations().get(stageId);
        var node = location != null ? data.nodeDefinitions().get(location.nodeId()) : null;
        if (location == null || node == null) {
            return new RequirementBlock(Component.translatable(Gui.REQUIREMENTS_NEXT_STEPS), List.of(unknown(stageId, context)), false);
        }

        var stage = node.stages().get(location.stageIndex());
        boolean completed = context.state().isStageCompleted(location.nodeId(), location.stageIndex());
        boolean current = !completed && context.state().isNodeUnlocked(location.nodeId())
                && context.state().getNodeStageIndex(location.nodeId()) - 1 == location.stageIndex();
        var manual = current && stage.completion().isManual()
                ? new RequirementBlock.ManualStage(location.nodeId(), location.stageIndex(), stage.completion().cost(), requirementsMet(stage, context))
                : null;
        var rows = new ArrayList<RequirementRow>();
        //a stage is reached once it is completed or active, future stages offer no actions
        stageRequirements(stage, context, completed, completed || current ? location.nodeId() : null, current,
                stage.completion().isManual() ? stage.completion().cost() : null, rows::add);
        rewardRows(stage.rewards(), completed, context, rows::add);
        int total = node.stages().size();
        return new RequirementBlock(stageTitle(stage, false), rows, false, stageDescription(stage),
                new RequirementBlock.StageLine(location.stageIndex(), total, location.stageIndex(), false,
                        stageTitle(stage, false), stageDescription(stage), manual));
    }

    /**
     * True if all facts of the stage are granted and all its values reached their thresholds.
     */
    private static boolean requirementsMet(ResearchStageDefinition stage, RequirementContext context) {
        for (var factId : stage.requiredFacts()) {
            if (!context.state().hasFact(factId)) {
                return false;
            }
        }
        for (var requirement : stage.requiredValues()) {
            if (context.state().getValue(requirement.valueId()) < requirement.threshold()) {
                return false;
            }
        }
        return true;
    }

    /**
     * The stage line of a progression, browsing is limited to the stages up to the current one.
     */
    private static RequirementBlock.StageLine stageLine(ResearchNodeDefinition node, int shown, int lastBrowsable, @Nullable RequirementBlock.ManualStage manual) {
        var stage = node.stages().get(shown);
        return new RequirementBlock.StageLine(shown, node.stages().size(), lastBrowsable, true, stageTitle(stage, false), stageDescription(stage), manual);
    }

    private static Component stageTitle(ResearchStageDefinition stage, boolean next) {
        var title = stage.display().flatMap(ResearchDisplay::title).or(() -> translated(Research.stageKey(stage.id())));
        if (title.isEmpty()) {
            return Component.translatable(Gui.REQUIREMENTS_NEXT_STEPS);
        }
        return next ? Component.translatable(Gui.REQUIREMENTS_NEXT, title.get()) : title.get();
    }

    private static @Nullable Component stageDescription(ResearchStageDefinition stage) {
        return stage.display().flatMap(ResearchDisplay::description).map(description -> description.copy().withStyle(ChatFormatting.GRAY)).orElse(null);
    }

    /**
     * A research row for another node reaching a stage, titled by the node name and the stage name.
     */
    public static void stageDependency(ResearchNodeDefinition.StageDependency dependency, RequirementContext context, Consumer<RequirementRow> out) {
        var data = context.data();
        var location = data.stageLocations().get(dependency.stageId());
        int stageIndex = location != null ? location.stageIndex() : 0;
        boolean met = context.state().isStageCompleted(dependency.nodeId(), stageIndex);

        var node = data.nodeDefinitions().get(dependency.nodeId());
        var display = node != null ? node.display().orElse(ResearchDisplay.EMPTY) : ResearchDisplay.EMPTY;
        var nodeName = display.title()
                .or(() -> translated(Research.nodeKey(dependency.nodeId())))
                .orElseGet(() -> Component.literal(dependency.nodeId().getPath()));
        var stageName = translated(Research.stageKey(dependency.stageId()))
                .orElseGet(() -> Component.literal(dependency.stageId().getPath()));

        out.accept(new RequirementRow(
                category(ResearchCategories.RESEARCH, context),
                iconsOr(display.icon(), List.of()),
                Component.translatable(Gui.REQUIREMENTS_STAGE_TITLE, nodeName, stageName),
                descriptionOr(display, List.of()),
                met,
                null,
                null,
                null,
                null
        ));
    }

    /**
     * The rewards of a stage as rows of the rewards category, met once the stage is completed.
     */
    private static void rewardRows(List<ResearchEffect> rewards, boolean met, RequirementContext context, Consumer<RequirementRow> out) {
        var category = category(ResearchCategories.REWARDS, context);
        for (var reward : rewards) {
            out.accept(ResearchEffectPresenterRegistry.get(reward.type().id()).present(reward, context)
                    .withCategory(category)
                    .withMet(met)
                    .withMissing(Component.translatable(Gui.REQUIREMENTS_REWARD_PENDING)));
        }
    }

    private static void stageRequirements(ResearchStageDefinition stage, RequirementContext context, boolean forceMet, @Nullable Identifier actionNode,
                                          boolean active, @Nullable ResearchCost consumed, Consumer<RequirementRow> out) {
        for (var factId : stage.requiredFacts()) {
            var row = fact(factId, context, forceMet, actionNode, active);
            if (active && consumed != null && consumedOnCompletion(factId, consumed, context)) {
                var details = new ArrayList<>(row.details());
                details.add(Component.translatable(Gui.REQUIREMENTS_CONSUMED_ON_COMPLETION).withStyle(ChatFormatting.GOLD));
                row = row.withDetails(details);
            }
            out.accept(row);
        }
        for (var requirement : stage.requiredValues()) {
            out.accept(value(requirement, context, forceMet));
        }
        if (consumed != null) {
            costRows(consumed, forceMet, context, out);
        }
    }

    /**
     * The cost of a manual stage as rows, one per entry, each described by its cost presenter. Completed stages show the entries met,
     * where the cost was paid.
     */
    private static void costRows(ResearchCost cost, boolean forceMet, RequirementContext context, Consumer<RequirementRow> out) {
        var consumed = List.<Component>of(Component.translatable(Gui.REQUIREMENTS_CONSUMED_ON_COMPLETION).withStyle(ChatFormatting.GOLD));
        for (var entry : cost.entries()) {
            out.accept(ResearchCostPresenterRegistry.get(entry.type().id()).row(entry, context, forceMet).withDetails(consumed));
        }
    }

    /**
     * True if the fact is granted by an item acquisition whose item is part of the cost, i.e. the item is consumed on completion.
     */
    private static boolean consumedOnCompletion(Identifier factId, ResearchCost cost, RequirementContext context) {
        return context.hooksGranting(factId).stream().anyMatch(hook -> hook.triggerTarget() instanceof ResearchItemMatcher matcher
                && cost.entries().stream().anyMatch(entry -> entry instanceof ItemCostEntry item && matcher.matches(item.item().displayStack())));
    }
    /**
     * Placeholder for references to definitions that are not known to the client.
     */
    public static RequirementRow unknown(Identifier id, RequirementContext context) {
        return RequirementRow.of(List.of(GENERIC_RESEARCH_ICON), Component.literal(id.toString()).withStyle(ChatFormatting.RED))
                .withCategory(category(ResearchCategories.RESEARCH, context));
    }

    /**
     * The rows presented by the trigger presenters of the hooks, grouped by the category claiming their trigger type.
     */
    private static List<RequirementRow> hookRows(List<ResearchHookDefinition<?>> hooks, RequirementContext context) {
        return hooks.stream()
                .map(hook -> {
                    var triggerCategory = context.data().categoryForTriggerType(hook.triggerType().id())
                            .orElseGet(() -> category(ResearchCategories.RESEARCH, context));
                    return TriggerRequirementPresenterRegistry.present(hook, context).withCategory(triggerCategory);
                })
                .toList();
    }

    /**
     * The category of a requirement row with a definition display and optionally a hook row. Precedence, first match wins:
     * <ol>
     *     <li>the category set in the display of the definition (display override),</li>
     *     <li>the category of the hook row, which is the category claiming the trigger type of its hook (trigger-type default),</li>
     *     <li>the fallback category of the row kind.</li>
     * </ol>
     * This is the only place that resolves row categories. Presenters that build rows directly use the same precedence.
     */
    private static ResearchCategoryDefinition rowCategory(ResearchDisplay display, @Nullable RequirementRow hookRow, Identifier fallback, RequirementContext context) {
        if (display.category().isPresent()) {
            return category(display.category().get(), context);
        }
        if (hookRow != null && hookRow.category() != null) {
            return hookRow.category();
        }
        return category(fallback, context);
    }

    /**
     * Resolves a category id to its definition. Unknown ids fall back to the research category and are logged once.
     */
    public static ResearchCategoryDefinition category(Identifier categoryId, RequirementContext context) {
        var category = context.data().categoryDefinitions().get(categoryId);
        if (category != null) {
            return category;
        }
        if (UNKNOWN_CATEGORIES.add(categoryId)) {
            Modonomicon.LOG.warn("Unknown research category '{}', using '{}' instead", categoryId, ResearchCategories.RESEARCH);
        }
        return context.data().categoryDefinitions().get(ResearchCategories.RESEARCH);
    }

    private static List<RequirementIcon> iconsOr(Optional<BookIcon> displayIcon, List<RequirementRow> hookRows) {
        if (displayIcon.isPresent()) {
            return List.of(RequirementIcon.of(displayIcon.get()));
        }
        var icons = hookRows.stream().flatMap(row -> row.icons().stream()).toList();
        return icons.isEmpty() ? List.of(GENERIC_RESEARCH_ICON) : icons;
    }

    private static List<Component> descriptionOr(ResearchDisplay display, List<Component> fallback) {
        return display.description()
                .map(description -> List.<Component>of(description.copy().withStyle(ChatFormatting.GRAY)))
                .orElse(fallback);
    }

    private static Runnable firstClickAction(List<RequirementRow> rows) {
        return rows.stream().map(RequirementRow::onClick).filter(onClick -> onClick != null).findFirst().orElse(null);
    }

    private static Optional<Component> translated(String key) {
        return Language.getInstance().has(key) ? Optional.of(Component.translatable(key)) : Optional.empty();
    }
}
