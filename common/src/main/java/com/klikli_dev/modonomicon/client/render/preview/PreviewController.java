/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2026 klikli-dev
 * SPDX-FileCopyrightText: 2026 XFactHD
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.api.multiblock.MultiblockPreviewData;
import com.klikli_dev.modonomicon.multiblock.AbstractMultiblock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Owns the active multiblock preview ({@link PreviewSession}) and orchestrates both render
 * phases plus HUD, interaction and tick handling.
 * <p>
 * Lifecycle logic moved from the former {@code MultiblockPreviewRenderer} monolith. The two
 * per-frame render phases share a single {@link PreviewSnapshot} produced by
 * {@link PreviewSimulator}: the extraction phase stores it, the submit phase consumes it.
 * </p>
 */
public class PreviewController {

    private static final PreviewController INSTANCE = new PreviewController();

    private final PreviewSession session = new PreviewSession();
    private final PreviewBlockEntityRenderer blockEntityRenderer = new PreviewBlockEntityRenderer();
    @Nullable
    private PreviewSnapshot lastSnapshot;

    public static PreviewController get() {
        return INSTANCE;
    }

    public PreviewSession session() {
        return this.session;
    }

    public boolean hasMultiblock() {
        return this.session.hasMultiblock();
    }

    // -- lifecycle ----------------------------------------------------------

    public void setMultiblock(@Nullable Multiblock multiblock, @Nullable Component name, PreviewOptions options) {
        if (options.toggleIfSame() && this.session.hasMultiblock() && this.session.multiblock() == multiblock) {
            this.clear();
        } else {
            this.blockEntityRenderer.clearCache();
            this.session.setMultiblock(multiblock);
            this.session.setName(name);
            this.session.setOffsetApplier(options.offsetApplier() != null ? options.offsetApplier() : pos -> pos);
            this.session.setLayerByLayer(options.layerByLayer());
            this.session.setAnchorPos(null);
            this.session.setAnchored(false);
            this.session.setFacingRotation(Rotation.NONE);
            this.session.setTimeComplete(0);
            this.session.setLookingState(null);
            this.session.setLookingPos(null);
            this.lastSnapshot = null;
        }
    }

    public void clear() {
        this.session.clear();
        this.blockEntityRenderer.clearCache();
        this.lastSnapshot = null;
    }

    public void anchorTo(BlockPos target, Rotation rot) {
        this.session.setAnchorPos(target);
        this.session.setFacingRotation(rot);
        this.session.setAnchored(true);
    }

    public InteractionResult onPlayerInteract(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
        if (this.session.hasMultiblock() && !this.session.isAnchored() && player == Minecraft.getInstance().player) {
            this.anchorTo(hit.getBlockPos(), getRotation(player));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void onClientTick(Minecraft mc) {
        if (Minecraft.getInstance().level == null) {
            this.clear();
            return;
        }
        PreviewProgress progress = this.lastSnapshot != null ? this.lastSnapshot.progress() : PreviewProgress.EMPTY;
        if (this.session.isAnchored() && progress.isComplete(this.session.isLayerByLayer())) {
            this.session.setTimeComplete(this.session.timeComplete() + 1);
            if (this.session.timeComplete() == PreviewConstants.COMPLETE_SOUND_TICK) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
            }
        } else {
            this.session.setTimeComplete(0);
        }
    }

    // -- phase 1: extract ---------------------------------------------------

    /**
     * Phase 1: resolve the anchor, simulate once and extract ghost block entity render states.
     * Called during the render state extraction phase.
     */
    public void extractRenderState() {
        //Like the original code, an extraction pass with no active preview (or a culled one)
        //drops the previous snapshot so the submit phase renders nothing stale.
        this.lastSnapshot = null;
        if (!this.session.hasMultiblock()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) {
            return;
        }
        if (!this.resolveForFrame(mc)) {
            return;
        }

        PreviewSnapshot snapshot = PreviewSimulator.simulate(this.session, level);
        if (snapshot == null) {
            return;
        }
        snapshot = snapshot.withBlockEntityStates(this.blockEntityRenderer.extract(snapshot));
        this.storeSnapshot(snapshot);
    }

    // -- phase 2: submit / draw ---------------------------------------------

    /**
     * Phase 2: draw ghost blocks and submit ghost block entities from the extraction snapshot.
     * Falls back to a block-only simulation when no extraction snapshot exists (e.g. the
     * extraction event was skipped), mirroring the former empty block entity list in that case.
     */
    public void renderLevel(PoseStack poseStack) {
        if (!this.session.hasMultiblock()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) {
            return;
        }

        PreviewSnapshot snapshot = this.lastSnapshot;
        if (snapshot == null) {
            if (!this.resolveForFrame(mc)) {
                return;
            }
            snapshot = PreviewSimulator.simulate(this.session, level);
            if (snapshot == null) {
                return;
            }
            this.storeSnapshot(snapshot);
        }

        PreviewBlockRenderer.render(snapshot, level, poseStack);
        this.blockEntityRenderer.submit(snapshot.blockEntityStates(), poseStack);
    }

    // -- HUD ----------------------------------------------------------------

    /**
     * Renders the preview HUD overlay. Clears the preview once the completion animation ends.
     */
    public void renderHud(GuiGraphicsExtractor guiGraphics, float partialTicks) {
        if (!this.session.hasMultiblock()) {
            return;
        }
        PreviewProgress progress = this.lastSnapshot != null ? this.lastSnapshot.progress() : PreviewProgress.EMPTY;
        if (PreviewHudRenderer.render(this.session, progress, guiGraphics, partialTicks)) {
            this.clear();
        }
    }

    // -- API views ----------------------------------------------------------

    @Nullable
    public MultiblockPreviewData getMultiblockPreviewData() {
        if (!this.session.hasMultiblock()) {
            return null;
        }
        return new MultiblockPreviewData(this.session.multiblock(), this.session.anchorPos(),
                this.session.facingRotation(), this.session.isAnchored(), this.session.isLayerByLayer());
    }

    // -- internals ----------------------------------------------------------

    /**
     * Stores the frame snapshot and mirrors looking + floating-counter semantics of the original
     * render loop: the HUD reads the session's looking state, and block counters reset while
     * the preview floats unanchored.
     */
    private void storeSnapshot(PreviewSnapshot snapshot) {
        if (!this.session.isAnchored()) {
            snapshot = snapshot.withZeroedBlockCounters();
        }
        this.session.setLookingState(snapshot.lookingState());
        this.session.setLookingPos(snapshot.checkPos());
        this.lastSnapshot = snapshot;
    }

    /**
     * Shared anchor + rotation resolution for both render phases (previously duplicated).
     *
     * @return {@code false} when there is no anchor yet or the anchored preview is out of range
     * and must be skipped this frame.
     */
    private boolean resolveForFrame(Minecraft mc) {
        if (mc.player == null) {
            return false;
        }
        if (!this.session.isAnchored()) {
            this.session.setFacingRotation(getRotation(mc.player));
            if (mc.hitResult instanceof BlockHitResult) {
                this.session.setAnchorPos(((BlockHitResult) mc.hitResult).getBlockPos());
            }
        } else if (this.session.anchorPos() != null
                && this.session.anchorPos().distToCenterSqr(mc.player.position()) > PreviewConstants.MAX_ANCHOR_DISTANCE * PreviewConstants.MAX_ANCHOR_DISTANCE) {
            return false;
        }

        if (this.session.anchorPos() == null) {
            return false;
        }
        if (this.session.multiblock().isSymmetrical()) {
            this.session.setFacingRotation(Rotation.NONE);
        }
        return true;
    }

    /**
     * Returns the Rotation of a multiblock structure based on the given entity's facing direction.
     */
    private static Rotation getRotation(Entity entity) {
        return AbstractMultiblock.rotationFromFacing(entity.getDirection());
    }
}
