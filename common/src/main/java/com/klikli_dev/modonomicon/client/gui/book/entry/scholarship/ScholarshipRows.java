/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Research;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.research.cost.ItemCostEntry;
import com.klikli_dev.modonomicon.research.data.ResearchGroups;
import com.klikli_dev.modonomicon.research.data.ResearchGroupDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchCost;
import com.klikli_dev.modonomicon.research.data.ResearchDisplay;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchStageDefinition;
import com.klikli_dev.modonomicon.research.effect.ResearchEffect;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
public final class ScholarshipRows {

    private static final ScholarshipIcon GENERIC_RESEARCH_ICON = ScholarshipIcons.icon(ScholarshipIcons.GENERIC);
    private static final Set<Identifier> UNKNOWN_GROUPS = ConcurrentHashMap.newKeySet();

    private ScholarshipRows() {
    }

    /**
     * A row for a research fact. Met if the fact is granted, or if forceMet is set.
     */
    public static ScholarshipRow fact(Identifier factId, ScholarshipContext context, boolean forceMet) {
        return fact(factId, context, forceMet, null);
    }

    /**
     * A row for a research fact. If actionNode is set, the fact's display has an action and the row offers it when
     * the action is repeatable, or when the fact is unmet and its stage is the active one.
     * Callers pass the node only if the stage of the fact is reached, so future stages never offer actions.
     */
    public static ScholarshipRow fact(Identifier factId, ScholarshipContext context, boolean forceMet, @Nullable Identifier actionNode) {
        return fact(factId, context, forceMet, actionNode, true);
    }

    /**
     * A row for a research fact that shows the given icon if neither its display nor the hooks granting it define one.
     * Used by effect rows, so the effect type icon is shown instead of the generic research icon.
     */
    static ScholarshipRow factWithFallbackIcon(Identifier factId, ScholarshipContext context, boolean forceMet, ScholarshipIcon fallbackIcon) {
        return fact(factId, context, forceMet, null, true, fallbackIcon);
    }

    private static ScholarshipRow fact(Identifier factId, ScholarshipContext context, boolean forceMet, @Nullable Identifier actionNode, boolean active) {
        return fact(factId, context, forceMet, actionNode, active, GENERIC_RESEARCH_ICON);
    }

    private static ScholarshipRow fact(Identifier factId, ScholarshipContext context, boolean forceMet, @Nullable Identifier actionNode, boolean active,
                                       ScholarshipIcon fallbackIcon) {
        var definition = context.data().factDefinitions().get(factId);
        var display = definition != null ? definition.display().orElse(ResearchDisplay.EMPTY) : ResearchDisplay.EMPTY;
        var hookRows = hookRows(context.hooksGranting(factId), context);
        var hookRow = hookRows.isEmpty() ? null : hookRows.get(0);

        var title = display.title()
                .or(() -> translated(Research.factKey(factId)))
                .or(() -> hookRow != null ? Optional.of(hookRow.title()) : Optional.empty())
                .orElseGet(() -> Component.literal(factId.getPath()));

        var group = rowGroup(display, hookRow, ResearchGroups.RESEARCH, context);

        boolean met = forceMet || context.state().hasFact(factId);
        var row = new ScholarshipRow(
                group,
                iconsOr(display.icon(), hookRows, fallbackIcon),
                title,
                descriptionOr(display, hookRow != null ? hookRow.details() : List.of()),
                met,
                null,
                hookRow != null ? hookRow.missing() : null,
                firstAction(hookRows),
                new ScholarshipRowGrant.Fact(factId)
        );
        if (actionNode != null) {
            var action = display.action();
            if (action.isPresent() && (action.get().repeatable() || (active && !met))) {
                row = row.withAction(new ScholarshipRowAction.Perform(actionNode, factId, action.get()));
            }
        }
        return row;
    }

    /**
     * A row for a research value, showing its progress towards the threshold.
     */
    public static ScholarshipRow value(ResearchNodeDefinition.ValueRequirement requirement, ScholarshipContext context, boolean forceMet) {
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

        boolean met = forceMet || current >= threshold;

        //tell the player what fulfils the requirement, as the value itself does not say it
        var details = new ArrayList<Component>(descriptionOr(display, List.of()));
        var viewedEntry = viewedEntry(context.hooksIncrementing(valueId));
        if (viewedEntry != null && !met) {
            details.add(Component.translatable(Gui.SCHOLARSHIP_VIEW_ENTRY_HINT, entryTitle(viewedEntry)).withStyle(ChatFormatting.GRAY));
        }

        return new ScholarshipRow(
                rowGroup(display, hookRow, ResearchGroups.PROGRESS, context),
                iconsOr(display.icon(), hookRows),
                title,
                details,
                met,
                new ScholarshipRow.Progress(shown, threshold),
                null,
                null,
                new ScholarshipRowGrant.Value(valueId, threshold)
        );
    }

    /**
     * The name of an entry of any book, or its id if no book has it.
     */
    private static Component entryTitle(Identifier entryId) {
        for (var book : BookDataManager.get().getBooks().values()) {
            var entry = book.getEntry(entryId);
            if (entry != null) {
                return Component.translatable(entry.getName());
            }
        }
        return Component.literal(entryId.getPath());
    }

    /**
     * The entry of the first entry viewed hook that raises a value, e.g. viewing the sources shelf counts one source consulted.
     */
    @Nullable
    private static Identifier viewedEntry(List<ResearchHookDefinition<?>> hooks) {
        for (var hook : hooks) {
            if (hook.triggerType().id().equals(TriggerTypeRegistry.ENTRY_VIEWED_ONCE.id()) && hook.triggerTarget() instanceof Identifier entryId) {
                return entryId;
            }
        }
        return null;
    }

    /**
     * The requirement block of an entry research: the stage dependencies of the node, then the requirements of its
     * next stage, titled by that stage. A completed node is marked complete and shows the requirements of its last stage, all met.
     * <p>
     * The state stage index is 1-based while a stage is active (index N = list index N-1) and exceeds the stage count once the node is complete.
     * Before the node is unlocked the first stage is the next stage.
     *
     * @param browsed the 0-based stage index to show instead of the next stage, or null to show the next stage
     */
    public static ScholarshipBlock research(Identifier nodeId, @Nullable Integer browsed, ScholarshipContext context) {
        var node = context.data().nodeDefinitions().get(nodeId);
        if (node == null) {
            return new ScholarshipBlock(Component.translatable(Gui.SCHOLARSHIP_NEXT_STEPS), List.of(unknown(nodeId, context)), false);
        }

        int total = node.stages().size();
        var state = context.state();
        int current = total == 0 ? 0 : Math.min(state.currentStageIndex(nodeId), total - 1);
        boolean complete = state.isNodeStarted(nodeId) && state.completedStageCount(nodeId) >= total;
        int shown = browsed != null && total > 0 ? Math.min(browsed, total - 1) : (total > 0 ? current : 0);
        boolean browsing = browsed != null && total > 0;

        var leading = new ArrayList<ScholarshipRow>();
        for (var dependency : node.requiredStages()) {
            stageDependency(dependency, context, leading::add);
        }
        //a node with stages only lists its unmet requirements, so the player sees why the node is locked
        for (var factId : node.requiredFacts()) {
            if (total == 0 || !state.hasFact(factId)) {
                leading.add(fact(factId, context, false));
            }
        }
        for (var requirement : node.requiredValues()) {
            if (total == 0 || state.getValue(requirement.valueId()) < requirement.threshold()) {
                leading.add(value(requirement, context, false));
            }
        }

        if (total == 0) {
            return new ScholarshipBlock(Component.translatable(Gui.SCHOLARSHIP_TITLE), leading, false);
        }

        //stages before the current one are done, the current one is only done once the node is complete
        boolean done = complete || shown < current;
        //only the current stage offers non-repeatable actions, a browsed or completed stage is read-only apart from repeatable actions
        boolean active = !done && !browsing;
        //a completed research keeps showing the steps of its last stage, all met, so the player sees what was done
        boolean finished = complete && !browsing;
        var slot = new StageSlot(nodeId, node, shown, current, done, active, nodeId, true, finished);
        var title = finished ? Component.translatable(Gui.SCHOLARSHIP_COMPLETE)
                : stageTitle(node.stages().get(shown), !(browsing || complete));
        return stageBlock(slot, title, leading, context);
    }

    /**
     * The requirement block of one stage, e.g. for a stage scholarship page. Completed stages show all rows met.
     */
    public static ScholarshipBlock stageBlock(Identifier stageId, ScholarshipContext context) {
        var data = context.data();
        var location = data.stageLocations().get(stageId);
        var node = location != null ? data.nodeDefinitions().get(location.nodeId()) : null;
        if (location == null || node == null) {
            return new ScholarshipBlock(Component.translatable(Gui.SCHOLARSHIP_NEXT_STEPS), List.of(unknown(stageId, context)), false);
        }

        int index = location.stageIndex();
        boolean completed = context.state().isStageCompleted(location.nodeId(), index);
        boolean current = !completed && context.state().isNodeUnlocked(location.nodeId())
                && context.state().currentStageIndex(location.nodeId()) == index;
        //a stage is reached once it is completed or active, future stages offer no actions
        var actionNode = completed || current ? location.nodeId() : null;
        var slot = new StageSlot(location.nodeId(), node, index, index, completed, current, actionNode, false, false);
        var stage = node.stages().get(index);
        return stageBlock(slot, stageTitle(stage, false), List.of(), context);
    }

    /**
     * The state of one stage shown by a requirement block.
     *
     * @param lastBrowsable the last stage index the stage line lets the player browse to
     * @param done the stage counts as done, its rows show met
     * @param active the stage is the one being worked on, it offers its manual completion and non-repeatable actions
     * @param actionNode the node that actions of the stage act on, null if the stage offers no actions
     * @param navigable true if the stage line offers browsing between stages
     * @param complete the whole research is complete
     */
    private record StageSlot(Identifier nodeId, ResearchNodeDefinition node, int index, int lastBrowsable, boolean done, boolean active,
                             @Nullable Identifier actionNode, boolean navigable, boolean complete) {
    }

    /**
     * The one builder of stage blocks: the leading rows, then the stage requirements and effects, with the manual completion
     * and the stage line of the stage. Callers derive the slot state and the title.
     */
    private static ScholarshipBlock stageBlock(StageSlot slot, Component title, List<ScholarshipRow> leading, ScholarshipContext context) {
        var stage = slot.node().stages().get(slot.index());
        var rows = new ArrayList<>(leading);
        var consumed = stage.completion().isManual() ? stage.completion().cost() : null;
        stageRequirements(stage, context, slot.done(), slot.actionNode(), slot.active(), consumed, rows::add);
        effectRows(stage.effects(), slot.done(), context, rows::add);

        var manual = slot.active() && consumed != null
                ? new ScholarshipBlock.ManualStage(slot.nodeId(), slot.index(), consumed, requirementsMet(stage, context))
                : null;
        var description = stageDescription(stage);
        var line = new ScholarshipBlock.StageLine(slot.index(), slot.node().stages().size(), slot.lastBrowsable(), slot.navigable(),
                stageTitle(stage, false), description, manual);
        return new ScholarshipBlock(title, rows, slot.complete(), description, line);
    }

    /**
     * True if all facts of the stage are granted and all its values reached their thresholds.
     */
    private static boolean requirementsMet(ResearchStageDefinition stage, ScholarshipContext context) {
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
     * The title of a stage, prefixed by "next" if asked for. Falls back to the next steps title if the stage has no name.
     */
    private static Component stageTitle(ResearchStageDefinition stage, boolean next) {
        var title = stage.display().flatMap(ResearchDisplay::title).or(() -> translated(Research.stageKey(stage.id())));
        if (title.isEmpty()) {
            return Component.translatable(Gui.SCHOLARSHIP_NEXT_STEPS);
        }
        return next ? Component.translatable(Gui.SCHOLARSHIP_NEXT, title.get()) : title.get();
    }

    private static @Nullable Component stageDescription(ResearchStageDefinition stage) {
        return stage.display().flatMap(ResearchDisplay::description).map(description -> description.copy().withStyle(ChatFormatting.GRAY)).orElse(null);
    }

    /**
     * A research row for another node reaching a stage, titled by the node name and the stage name.
     */
    public static void stageDependency(ResearchNodeDefinition.StageDependency dependency, ScholarshipContext context, Consumer<ScholarshipRow> out) {
        var data = context.data();
        var location = data.stageLocations().get(dependency.stageId());
        //an unknown stage is not met, it must not show as done
        boolean met = location != null && location.nodeId().equals(dependency.nodeId())
                && context.state().isStageCompleted(location.nodeId(), location.stageIndex());

        var node = data.nodeDefinitions().get(dependency.nodeId());
        var display = node != null ? node.display().orElse(ResearchDisplay.EMPTY) : ResearchDisplay.EMPTY;
        var nodeName = display.title()
                .or(() -> translated(Research.nodeKey(dependency.nodeId())))
                .orElseGet(() -> Component.literal(dependency.nodeId().getPath()));
        var stageName = translated(Research.stageKey(dependency.stageId()))
                .orElseGet(() -> Component.literal(dependency.stageId().getPath()));

        out.accept(new ScholarshipRow(
                group(ResearchGroups.RESEARCH, context),
                iconsOr(display.icon(), List.of()),
                Component.translatable(Gui.SCHOLARSHIP_STAGE_TITLE, nodeName, stageName),
                descriptionOr(display, List.of()),
                met,
                null,
                null,
                null,
                null
        ));
    }

    /**
     * The effects of a stage as rows of the effects group, met once the stage is completed.
     */
    private static void effectRows(List<ResearchEffect> effects, boolean met, ScholarshipContext context, Consumer<ScholarshipRow> out) {
        var group = group(ResearchGroups.EFFECTS, context);
        for (var effect : effects) {
            out.accept(ScholarshipEffectPresenterRegistry.get(effect.type().id()).present(effect, context)
                    .withGroup(group)
                    .withMet(met)
                    .withMissing(Component.translatable(Gui.SCHOLARSHIP_EFFECT_PENDING)));
        }
    }

    private static void stageRequirements(ResearchStageDefinition stage, ScholarshipContext context, boolean forceMet, @Nullable Identifier actionNode,
                                          boolean active, @Nullable ResearchCost consumed, Consumer<ScholarshipRow> out) {
        for (var factId : stage.requiredFacts()) {
            var row = fact(factId, context, forceMet, actionNode, active);
            if (active && consumed != null && consumedOnCompletion(factId, consumed, context)) {
                var details = new ArrayList<>(row.details());
                details.add(Component.translatable(Gui.SCHOLARSHIP_CONSUMED_ON_COMPLETION).withStyle(ChatFormatting.GOLD));
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
    private static void costRows(ResearchCost cost, boolean forceMet, ScholarshipContext context, Consumer<ScholarshipRow> out) {
        var consumed = List.<Component>of(Component.translatable(Gui.SCHOLARSHIP_CONSUMED_ON_COMPLETION).withStyle(ChatFormatting.GOLD));
        for (var entry : cost.entries()) {
            out.accept(ScholarshipCostPresenterRegistry.get(entry.type().id()).row(entry, context, forceMet).withDetails(consumed));
        }
    }

    /**
     * True if the fact is granted by an item acquisition whose item is part of the cost, i.e. the item is consumed on completion.
     */
    private static boolean consumedOnCompletion(Identifier factId, ResearchCost cost, ScholarshipContext context) {
        return context.hooksGranting(factId).stream().anyMatch(hook -> hook.triggerTarget() instanceof ResearchItemMatcher matcher
                && cost.entries().stream().anyMatch(entry -> entry instanceof ItemCostEntry item && matcher.matches(item.item().displayStack())));
    }
    /**
     * Placeholder for references to definitions that are not known to the client.
     */
    public static ScholarshipRow unknown(Identifier id, ScholarshipContext context) {
        return ScholarshipRow.of(List.of(GENERIC_RESEARCH_ICON), Component.literal(id.toString()).withStyle(ChatFormatting.RED))
                .withGroup(group(ResearchGroups.RESEARCH, context));
    }

    /**
     * The rows presented by the trigger presenters of the hooks, grouped by the group claiming their trigger type.
     */
    private static List<ScholarshipRow> hookRows(List<ResearchHookDefinition<?>> hooks, ScholarshipContext context) {
        return hooks.stream()
                .map(hook -> {
                    var triggerGroup = context.data().groupForTriggerType(hook.triggerType().id())
                            .orElseGet(() -> group(ResearchGroups.RESEARCH, context));
                    return ScholarshipTriggerPresenterRegistry.present(hook, context).withGroup(triggerGroup);
                })
                .toList();
    }

    /**
     * The group of a requirement row with a definition display and optionally a hook row. Precedence, first match wins:
     * <ol>
     *     <li>the group set in the display of the definition (display override),</li>
     *     <li>the group of the hook row, which is the group claiming the trigger type of its hook (trigger-type default),</li>
     *     <li>the fallback group of the row kind.</li>
     * </ol>
     * This is the only place that resolves row groups. Presenters that build rows directly use the same precedence.
     */
    private static ResearchGroupDefinition rowGroup(ResearchDisplay display, @Nullable ScholarshipRow hookRow, Identifier fallback, ScholarshipContext context) {
        if (display.group().isPresent()) {
            return group(display.group().get(), context);
        }
        if (hookRow != null && hookRow.group() != null) {
            return hookRow.group();
        }
        return group(fallback, context);
    }

    /**
     * Resolves a group id to its definition. Unknown ids fall back to the research group and are logged once.
     */
    public static ResearchGroupDefinition group(Identifier groupId, ScholarshipContext context) {
        var group = context.data().groupDefinitions().get(groupId);
        if (group != null) {
            return group;
        }
        if (UNKNOWN_GROUPS.add(groupId)) {
            Modonomicon.LOG.warn("Unknown research group '{}', using '{}' instead", groupId, ResearchGroups.RESEARCH);
        }
        return context.data().groupDefinitions().get(ResearchGroups.RESEARCH);
    }

    private static List<ScholarshipIcon> iconsOr(Optional<BookIcon> displayIcon, List<ScholarshipRow> hookRows) {
        return iconsOr(displayIcon, hookRows, GENERIC_RESEARCH_ICON);
    }

    private static List<ScholarshipIcon> iconsOr(Optional<BookIcon> displayIcon, List<ScholarshipRow> hookRows, ScholarshipIcon fallbackIcon) {
        if (displayIcon.isPresent()) {
            return List.of(ScholarshipIcon.of(displayIcon.get()));
        }
        var icons = hookRows.stream().flatMap(row -> row.icons().stream()).toList();
        return icons.isEmpty() ? List.of(fallbackIcon) : icons;
    }

    private static List<Component> descriptionOr(ResearchDisplay display, List<Component> fallback) {
        return display.description()
                .map(description -> List.<Component>of(description.copy().withStyle(ChatFormatting.GRAY)))
                .orElse(fallback);
    }

    private static ScholarshipRowAction firstAction(List<ScholarshipRow> rows) {
        return rows.stream().map(ScholarshipRow::action).filter(action -> action != null).findFirst().orElse(null);
    }

    private static Optional<Component> translated(String key) {
        return Language.getInstance().has(key) ? Optional.of(Component.translatable(key)) : Optional.empty();
    }
}
