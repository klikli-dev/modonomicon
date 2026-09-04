/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

/**
 * Shared tuning constants for the in-world multiblock preview.
 * <p>
 * Values are moved verbatim from the former {@code MultiblockPreviewRenderer} monolith so that
 * the refactor does not change on-screen behavior. Each constant documents where it is used.
 * </p>
 */
public final class PreviewConstants {

    private PreviewConstants() {
    }

    /** Maximum anchor distance (in blocks) before the preview stops rendering. */
    public static final int MAX_ANCHOR_DISTANCE = 64;

    /** Ghost block alpha for normal blocks (0-1 range, multiplied to 0-255 when meshing). */
    public static final float BLOCK_ALPHA = 0.3F;

    /** Base ghost block alpha for the block the player is looking at. Pulsates via {@link #LOOKING_PULSE_SPEED}. */
    public static final float LOOKING_BASE_ALPHA = 0.6F;

    /** Amplitude of the looking-highlight alpha pulsation. */
    public static final float LOOKING_PULSE_AMPLITUDE = 0.1F;

    /** Speed of the looking-highlight alpha pulsation. */
    public static final float LOOKING_PULSE_SPEED = 0.3F;

    /** Ghost alpha applied to block entity renders (0-1 range). */
    public static final float BLOCK_ENTITY_ALPHA = 0.6F;

    /**
     * Anti-z-fighting scale applied to ghost blocks.
     * HACK / WHY: without the slight upscaling the translucent preview flickers against real
     * blocks occupying the same space.
     */
    public static final float GHOST_SCALE = 1.0001F;

    /**
     * Scale of the placeholder cube rendered for empty (air) positions.
     * HACK / WHY: air has no model, so a small red concrete cube marks spots that must stay empty.
     */
    public static final float AIR_PLACEHOLDER_SCALE = 0.3F;

    /** Ticks the "complete" banner waits before fading out. */
    public static final int COMPLETE_WAIT_TICKS = 40;

    /** Pixels per tick the HUD shifts up while fading out after completion. */
    public static final int COMPLETE_FADE_PX_PER_TICK = 4;

    /** Extra ticks after {@link #COMPLETE_WAIT_TICKS} before the preview auto-clears. */
    public static final int COMPLETE_ANIM_TAIL_TICKS = 10;

    /** {@code timeComplete} value at which the completion chime plays. */
    public static final int COMPLETE_SOUND_TICK = 14;

    /** HUD progress bar width in pixels. */
    public static final int HUD_BAR_WIDTH = 180;

    /** HUD progress bar height in pixels. */
    public static final int HUD_BAR_HEIGHT = 9;

    /** HUD title Y position. */
    public static final int HUD_TITLE_Y = 12;
}
