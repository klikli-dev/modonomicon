/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.book.entry.scholarship;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.BookIcon;
import net.minecraft.resources.Identifier;

/**
 * The default textures of research type icons.
 * <p>
 * Icons are drawn at 16x16 GUI pixels, independent of the resolution of the PNG files: the whole texture is scaled to
 * the drawn size, so higher resolution artwork works as long as it is square. All of them live in
 * {@code assets/modonomicon/textures/gui/sprites/modonomicon/scholarship/icons/} and are referenced by file name, so
 * artwork can be replaced by dropping in a file with the same name.
 * <p>
 * The built-in presenters do not use overlay badges, as they cover the item and its count. Addons can still attach a
 * badge (drawn at {@link #BADGE_SIZE} over the bottom right of the icon) via {@link ScholarshipIcon#withBadge}.
 * <p>
 * The icons are fallbacks: where a research requirement shows dynamic data (e.g. the item of a cost or the icon of an
 * entry), that data is shown instead and the texture is not used.
 */
public final class ScholarshipIcons {

    /**
     * The directory of the research icon textures, relative to the mod's namespace.
     */
    public static final String DIRECTORY = "textures/gui/sprites/modonomicon/scholarship/icons/";

    /**
     * The size of a badge in pixels, badges are drawn at the bottom right corner of a 16x16 icon.
     */
    public static final int BADGE_SIZE = 8;

    /**
     * The offset of a badge from the top left corner of the icon it decorates.
     */
    public static final int BADGE_OFFSET = ModonomiconConstants.Data.Icon.DEFAULT_WIDTH - BADGE_SIZE;

    // ---- icons of research cost, effect and trigger types, and of the generic fallback ----

    public static final Identifier COST_LEVELS = texture("cost_levels");
    public static final Identifier EFFECT_GRANT_FACT = texture("effect_grant_fact");
    public static final Identifier EFFECT_ADD_VALUE = texture("effect_add_value");
    public static final Identifier EFFECT_GIVE_ITEM = texture("effect_give_item");
    public static final Identifier EFFECT_EXPERIENCE = texture("effect_experience");
    public static final Identifier EFFECT_FUNCTION = texture("effect_function");
    public static final Identifier TRIGGER_ENTRY_VIEWED = texture("trigger_entry_viewed");
    public static final Identifier TRIGGER_ADVANCEMENT = texture("trigger_advancement");
    /**
     * The generic fallback, shares the artwork of the default research requirement category.
     */
    public static final Identifier GENERIC = Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, "textures/gui/sprites/modonomicon/scholarship/groups/research.png");

    // ---- icons of stage completion modes and book conditions, no default UI location yet ----

    /**
     * Stage completes automatically once its requirements are met.
     */
    public static final Identifier STAGE_AUTO = texture("stage_auto");
    /**
     * Stage needs to be completed by hand.
     */
    public static final Identifier STAGE_MANUAL = texture("stage_manual");
    /**
     * Book condition: a research node is unlocked.
     */
    public static final Identifier CONDITION_NODE_UNLOCKED = texture("condition_node_unlocked");
    /**
     * Book condition: a research stage is completed.
     */
    public static final Identifier CONDITION_STAGE_COMPLETED = texture("condition_stage_completed");

    private ScholarshipIcons() {
    }

    /**
     * The texture of a research icon by its file name without extension, e.g. {@code cost_levels}.
     */
    public static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(Modonomicon.MOD_ID, DIRECTORY + name + ".png");
    }

    /**
     * A requirement icon showing a texture at 16x16.
     * <p>
     * The width and height passed to {@link BookIcon} are logical sizes: the blit covers the full texture (region size
     * equals texture size), so the actual resolution of the PNG does not matter.
     */
    public static ScholarshipIcon icon(Identifier texture) {
        return ScholarshipIcon.of(new BookIcon(texture, ModonomiconConstants.Data.Icon.DEFAULT_WIDTH, ModonomiconConstants.Data.Icon.DEFAULT_HEIGHT));
    }
}
