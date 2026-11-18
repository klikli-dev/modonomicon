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
 * <h2>Layering</h2>
 * Research does not depend on the book package. Book-side behaviour that reacts to research changes, such as
 * visibility-driven unread state, is registered as a {@code ResearchStateChangeListener} during mod setup.
 */
package com.klikli_dev.modonomicon.research;
