/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import net.minecraft.resources.Identifier;

/**
 * Single owner of the generated research path scheme used for book-derived research.
 *
 * <p>Scheme, relative to the research namespace of the book:
 * <ul>
 *     <li>{@code generated/<book>/facts/<trigger>/<key>}: generated facts. Fact ids are persisted in player save data, so they must never change.</li>
 *     <li>{@code generated/<book>/nodes/entry_viewed_once/<entry>}: parent-hierarchy nodes, one per entry.</li>
 *     <li>{@code generated/<book>/nodes/entry_viewed_once/required/<entry>}: explicit entry-viewed requirement nodes.</li>
 *     <li>{@code generated/<book>/nodes/<trigger>/<key>}: item-trigger requirement nodes.</li>
 *     <li>{@code generated/<book>/facts/item_used/<key>[_consume<n>]}: item-used facts, one per item and consume count.</li>
 *     <li>{@code generated/<book>/nodes/item_used/<key>[_consume<n>]}: item-used requirement nodes.</li>
 *     <li>{@code generated/<book>/nodes/entry_progression/<entry>}: progression nodes, one per entry with stages.</li>
 *     <li>{@code generated/<book>/nodes/entry_progression/<entry>/stages/<stage>}: stages of a progression node.</li>
 * </ul>
 * Node unlock state is persisted in player save data as well, so node paths are kept stable too.
 * {@code <entry>} is the entry path with {@code /} replaced by {@code _}; {@code <key>} is the item key from
 * {@link GeneratedBookResearchStore#itemKey}.
 */
public final class GeneratedResearchPaths {
    public static final String ENTRY_VIEWED_ONCE = "entry_viewed_once";
    public static final String ITEM_CRAFTED = "item_crafted";
    public static final String ITEM_ACQUIRED = "item_acquired";
    public static final String ITEM_USED = "item_used";
    public static final String ENTRY_PROGRESSION = "entry_progression";

    private static final String ROOT = "generated/";

    private GeneratedResearchPaths() {
    }

    /**
     * Returns the bundle path of the generated research for the given book path.
     */
    public static String bundle(String bookPath) {
        return ROOT + bookPath;
    }

    /**
     * Returns the fact path for an entry-viewed-once requirement.
     */
    public static String entryViewedOnceFact(String bookPath, Identifier entryId) {
        return bundle(bookPath) + "/facts/" + ENTRY_VIEWED_ONCE + "/" + entryKey(entryId);
    }

    /**
     * Returns the node path of the parent-hierarchy node for an entry.
     */
    public static String entryHierarchyNode(String bookPath, Identifier entryId) {
        return bundle(bookPath) + "/nodes/" + ENTRY_VIEWED_ONCE + "/" + entryKey(entryId);
    }

    /**
     * Returns the node path of the explicit entry-viewed-once requirement node.
     */
    public static String entryRequiredNode(String bookPath, Identifier entryId) {
        return bundle(bookPath) + "/nodes/" + ENTRY_VIEWED_ONCE + "/required/" + entryKey(entryId);
    }

    /**
     * Returns the fact path for an item trigger ({@link #ITEM_CRAFTED} or {@link #ITEM_ACQUIRED}).
     */
    public static String itemFact(String bookPath, String trigger, String itemKey) {
        return bundle(bookPath) + "/facts/" + trigger + "/" + itemKey;
    }

    /**
     * Returns the node path for an item trigger ({@link #ITEM_CRAFTED} or {@link #ITEM_ACQUIRED}).
     */
    public static String itemNode(String bookPath, String trigger, String itemKey) {
        return bundle(bookPath) + "/nodes/" + trigger + "/" + itemKey;
    }

    /**
     * Returns the fact path for an item-used request. Requests with different consume counts get distinct facts.
     */
    public static String itemUsedFact(String bookPath, String itemKey, int consume) {
        return bundle(bookPath) + "/facts/" + ITEM_USED + "/" + itemKey + consumeSuffix(consume);
    }

    /**
     * Returns the node path for an item-used request. Requests with different consume counts get distinct nodes.
     */
    public static String itemUsedNode(String bookPath, String itemKey, int consume) {
        return bundle(bookPath) + "/nodes/" + ITEM_USED + "/" + itemKey + consumeSuffix(consume);
    }

    /**
     * Returns the node path of the progression of an entry with stages.
     */
    public static String entryProgressionNode(String bookPath, Identifier entryId) {
        return bundle(bookPath) + "/nodes/" + ENTRY_PROGRESSION + "/" + entryKey(entryId);
    }

    /**
     * Returns the path of a stage of the progression node of an entry, relative to the research namespace.
     */
    public static String entryProgressionStage(String bookPath, Identifier entryId, String stageName) {
        return entryProgressionNode(bookPath, entryId) + "/stages/" + stageName;
    }

    private static String consumeSuffix(int consume) {
        return consume > 0 ? "_consume" + consume : "";
    }

    private static String entryKey(Identifier entryId) {
        return entryId.getPath().replace('/', '_');
    }
}
