/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.render.preview;

import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;

import java.util.List;

/**
 * A {@link SubmitNodeCollector} that renders everything submitted through it as a translucent
 * ghost. See {@link PreviewGhostOrderedSubmitNodeCollector} for the ghosting details.
 * <p>
 * Instances are created per frame in {@link PreviewBlockEntityRenderer#submit(List, SubmitNodeCollector)}
 * and only wrap the block entity submissions, so the shared level submission pipeline is never
 * affected.
 * </p>
 */
final class PreviewGhostSubmitNodeCollector extends PreviewGhostOrderedSubmitNodeCollector implements SubmitNodeCollector {

    private final SubmitNodeCollector delegate;

    PreviewGhostSubmitNodeCollector(SubmitNodeCollector delegate, int alpha) {
        super(delegate, alpha);
        this.delegate = delegate;
    }

    @Override
    public OrderedSubmitNodeCollector order(int order) {
        return new PreviewGhostOrderedSubmitNodeCollector(this.delegate.order(order), this.alpha);
    }
}
