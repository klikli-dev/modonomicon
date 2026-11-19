/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

/**
 * Research runtime: facts, values, nodes and stages that gate book content.
 *
 * <h2>Pipeline</h2>
 * <ol>
 *     <li><b>Event.</b> A loader hook, mixin or command reports something that happened, for example an item was crafted,
 *     an advancement was completed or an entry was viewed. Gameplay code calls the convenience methods on
 *     {@code ResearchServices.hooks()} such as {@code onItemCrafted} or {@code onEntryViewedOnce}.</li>
 *     <li><b>Hook resolution.</b> The event becomes a {@code TriggerContext}. The {@code TriggerHandler} of the trigger type
 *     resolves the hooks that target it through {@code ResearchData.hooksFor}, which uses the pre-built per-type
 *     {@code ResearchHookIndex} instead of scanning every hook.</li>
 *     <li><b>Hook batch.</b> {@code ResearchHookService} applies the hooks of one event as one batch, in this order:
 *     <ol>
 *         <li>registered {@code ResearchStateChangeListener}s capture their "before" state,</li>
 *         <li>toast collection begins,</li>
 *         <li>each hook grants its fact or increments its value on {@code ResearchStateManager},</li>
 *         <li>{@code ResearchStateManager.reevaluate} unlocks nodes and advances stages,</li>
 *         <li>toast collection ends and the collected triggers are sent to the player as one {@code ResearchToastMessage},</li>
 *         <li>listeners receive whether anything changed, so they can react (for example by updating book unread state).</li>
 *     </ol></li>
 *     <li><b>Reevaluate.</b> For every node rule whose fact, value and stage dependencies are met, the node is unlocked
 *     and started, and its stages advance while their requirements are met. Stage rules and stage ids are looked up by
 *     {@code StageLocation}. Progress is stored 1-based in {@code PlayerResearchState}, use its stage helpers
 *     ({@code completedStageCount}, {@code currentStageIndex}, {@code isStageCompleted}) instead of the raw value.</li>
 *     <li><b>Persistence and sync.</b> Mutations mark {@code ResearchStatesSaveData} dirty. Loaders send
 *     {@code SyncResearchStateMessage} to the player via {@code ResearchStateManager.syncFor} when the state
 *     changes outside a hook batch. Admin commands mutate the state directly.</li>
 * </ol>
 *
 * <h2>Data</h2>
 * Authored JSON is parsed by {@code ResearchDataManager}. {@code ResearchData.validate} checks references and builds all
 * derived indices (hook indices, stage rules and locations, stage counts). The server sends the authored definitions to
 * clients with {@code SyncResearchDataMessage}, and clients rebuild the same indices with the same validation.
 *
 * <h2>Effects</h2>
 * A {@code ResearchEffect} changes the player: it grants a fact, increments a value, gives an item, grants experience or runs a
 * function. Effects are the building block of stage rewards and of action effects. {@code apply} returns true if the effect changed
 * something (research state, inventory or experience), so a caller can tell a real change from a no-op. Effects run on the server,
 * inside a state change, see {@code ResearchHookService#applyStateChange}.
 *
 * <h2>Actions</h2>
 * A {@code ResearchAction} is a cost, the effects it applies and two flags: {@code grantsFact} (pay to learn the fact it is
 * attached to) and {@code repeatable} (offered again after the fact is granted or the stage is completed). An action must grant
 * its fact or be repeatable, otherwise it is never recorded and is rejected. Players perform actions only through
 * {@code ResearchActions.perform}, which validates the node, stage and fact, checks the action's availability and plans its cost.
 *
 * <h2>Manual stage completion</h2>
 * A manual stage is completed by the player through {@code ResearchStages.completeManual}, not by a hook. It requires the
 * stage's requirements to be met and its cost to be plannable. Automatic stages that follow cascade through the same
 * reevaluation as hooks.
 *
 * <h2>Costs</h2>
 * A {@code ResearchCost} is a list of typed entries, e.g. items, durability or levels. Every paid action and every manual stage
 * follows the same order: <b>plan, then mutate, then pay</b>. {@code ResearchCost#plan} reserves what the cost needs against the
 * current inventory and levels, without changing anything, and returns empty if it is not affordable. The state change runs
 * only after a plan exists. {@code ResearchCost#pay} then applies the plan. If the reserved stacks changed during the change, the
 * cost is planned again, and if that fails a warning is logged and the change is kept.
 *
 * <h2>State changes</h2>
 * Every state change that is not a plain hook goes through {@code ResearchHookService#applyStateChange}: listeners before,
 * the mutation, reevaluation, toast sync, then listeners after. Its boolean result is true if the mutation or the reevaluation reported a change.
 * Hooks, actions and manual stage completion all use this entry point, so they share one pipeline.
 *
 * <h2>Default icons</h2>
 * The client presenters of cost, effect and trigger types show a texture icon where the icon is a type default, e.g. the
 * level cost or the reward of a function. Dynamic data keeps its own icon, such as the item of a cost or the icon of an entry.
 * The textures and their file names are listed in {@code ResearchIcons}. Badges are 8x8 textures drawn over the bottom right
 * of an icon, e.g. the consumed badge of an item cost, and are attached with {@code RequirementIcon#withBadge}.
 *
 * <h2>Layering</h2>
 * Research does not depend on the book package, with two exceptions that only carry presentation data:
 * {@code BookIcon} for the icons of displays and toasts ({@code ResearchDisplay}, {@code ResearchToastDefinition}), and
 * {@code AssociatedItemMatcher}, the item matcher shared with the book ({@code ResearchItemMatcher}). Book-side behaviour that
 * reacts to research changes, such as visibility-driven unread state, is registered as a {@code ResearchStateChangeListener}
 * during mod setup. Nothing else in research may import the book package.
 */
package com.klikli_dev.modonomicon.research;
