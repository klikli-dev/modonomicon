/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

/**
 * Opinionated base class for authoring one research bundle more easily.
 *
 * This mirrors the role of {@code SingleBookSubProvider} on the book side: it owns one bundle id,
 * sets up the active {@link ResearchDataBuilder}, and lets subclasses focus on the actual authored
 * research content in {@link #generateResearch()}.
 */
public abstract class SingleResearchSubProvider extends ResearchProviderBase implements ResearchSubProvider {
    private final String researchId;

    /**
     * Creates a subprovider that will author one research bundle under the given relative id.
     */
    protected SingleResearchSubProvider(String researchId, String modId) {
        super(modId);
        this.researchId = researchId;
    }

    /**
     * Returns the bundle-relative id authored by this subprovider.
     */
    protected String researchId() {
        return this.researchId;
    }

    /**
     * Generates this subprovider's bundle and hands it to the top-level research provider.
     */
    @Override
    public void generate(BiConsumer<Identifier, ResearchBundle> consumer, HolderLookup.Provider registries) {
        this.registries(registries);

        var research = new ResearchDataBuilder(this.modId);
        this.research(research);
        this.generateResearch();

        consumer.accept(this.modLoc(this.researchId), new ResearchBundle(research));
    }

    /**
     * Implement this to author the research facts, nodes, and ingress mappings for this bundle.
     */
    protected abstract void generateResearch();
}
