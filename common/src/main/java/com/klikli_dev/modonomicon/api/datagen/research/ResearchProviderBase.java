/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

/**
 * Shared base for research-side datagen authoring helpers.
 *
 * This base intentionally stays narrower than the book datagen base. It provides only registry
 * access, namespace/id helpers, primitive research declarations, and research-ingress helpers.
 */
public abstract class ResearchProviderBase {
    protected final String modId;
    private HolderLookup.Provider registries;
    private ResearchDataBuilder research;

    /**
     * Creates a research provider base for content authored under the given mod id.
     */
    protected ResearchProviderBase(String modId) {
        this.modId = modId;
    }

    /**
     * Sets the registry lookup provider for the current generate pass.
     */
    protected void registries(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    /**
     * Returns the registry lookup provider for the current generate pass.
     */
    protected HolderLookup.Provider registries() {
        return this.registries;
    }

    /**
     * Sets the active research data builder for the current generate pass.
     */
    protected void research(ResearchDataBuilder research) {
        this.research = research;
    }

    /**
     * Returns the active research data builder for the current generate pass.
     */
    protected ResearchDataBuilder research() {
        return this.research;
    }

    /**
     * Creates a namespaced identifier under the current provider's mod id.
     *
     * This is a plain id helper. It does not create research content by itself.
     */
    protected Identifier modLoc(String path) {
        return Identifier.fromNamespaceAndPath(this.modId, path);
    }

    /**
     * Declares a research fact in the active research bundle and returns a typed ref to it.
     *
     * Facts are primitive durable research inputs. They are granted by explicit research ingress
     * such as hooks, then referenced by research nodes as unlock requirements.
     */
    protected ResearchFactRef fact(String path) {
        return this.research.fact(path);
    }

    /**
     * Declares a research node in the active research bundle and returns a typed ref to it.
     *
     * In the current runtime model, a node is an authored durable milestone that unlocks when all
     * of the referenced fact requirements are present in the player's research state.
     */
    protected ResearchNodeRef node(String path, ResearchFactRef... requiredFacts) {
        return this.research.node(path, requiredFacts);
    }

    /**
     * Returns a fluent helper for authoring research ingress from external events into primitive
     * research fact grants.
     */
    protected ResearchIngressHelper ingress() {
        return new ResearchIngressHelper(this.research);
    }
}
