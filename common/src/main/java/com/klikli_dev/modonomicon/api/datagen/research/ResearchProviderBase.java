/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.List;

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
    private ModonomiconLanguageProvider injectedLang;

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
     * Creates a namespaced identifier under the minecraft namespace.
     *
     * This is a plain id helper. It does not create research content by itself.
     */
    protected Identifier mcLoc(String path) {
        return Identifier.withDefaultNamespace(path);
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
     * Declares a research value in the active research bundle and returns a typed ref to it.
     *
     * Values are numeric counters that hooks can increment and nodes can require reaching a threshold.
     */
    protected ResearchValueRef value(String path) {
        return this.research.value(path);
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
     * Declares a research node for an already-known typed node ref and returns that same ref.
     */
    protected ResearchNodeRef node(ResearchNodeRef ref, ResearchFactRef... requiredFacts) {
        return this.research.node(ref, requiredFacts);
    }

    /**
     * Declares a research node with both fact and value requirements.
     */
    protected ResearchNodeRef node(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts, List<ResearchNodeSpec.ValueRequirement> requiredValues) {
        return this.research.node(ref, requiredFacts, requiredValues);
    }

    /**
     * Creates a typed stage ref for use in node stage declarations.
     */
    protected ResearchStageRef stageRef(String path) {
        return this.research.stageRef(path);
    }

    /**
     * Declares a research node with stages and stage dependencies.
     */
    protected ResearchNodeRef node(
            ResearchNodeRef ref,
            List<ResearchFactRef> requiredFacts,
            List<ResearchNodeSpec.ValueRequirement> requiredValues,
            List<ResearchStageSpec> stages,
            List<ResearchNodeSpec.StageDependencySpec> requiredStages
    ) {
        return this.research.node(ref, requiredFacts, requiredValues, stages, requiredStages);
    }

    /**
     * Returns a fluent helper for authoring research ingress from external events into primitive
     * research fact grants.
     */
    protected ResearchIngressHelper ingress() {
        return new ResearchIngressHelper(this.research);
    }

    /**
     * Injects a language provider. Called by {@link ResearchProvider} during generation.
     */
    public void injectLang(ModonomiconLanguageProvider lang) {
        this.injectedLang = lang;
    }

    /**
     * Returns the injected language provider for adding translations.
     */
    protected ModonomiconLanguageProvider lang() {
        return this.injectedLang;
    }

    /**
     * Add translation to the injected language provider.
     */
    protected void add(String key, String value) {
        this.injectedLang.add(key, value);
    }

    /**
     * Returns the description id for a research node, suitable for use as a translation key.
     * <p>
     * The resulting key follows the pattern {@code block.<namespace>.<path>.description}, e.g.
     * {@code block.mymod.demo/crafting_stick.description}. Use this to add a human-readable name
     * for the node that will appear in condition tooltips:
     * <pre>{@code
     * this.add(this.researchNodeDescriptionId(myNode), "Crafting Stick");
     * }</pre>
     */
    protected String researchNodeDescriptionId(Identifier nodeId) {
        return Util.makeDescriptionId("research_node", nodeId);
    }

    /**
     * Returns the description id for a research node ref.
     */
    protected String researchNodeDescriptionId(ResearchNodeRef nodeRef) {
        return this.researchNodeDescriptionId(nodeRef.id());
    }

    /**
     * Returns the description id for a research stage, suitable for use as a translation key.
     * <p>
     * The resulting key follows the pattern {@code block.<namespace>.<path>.description}. Use this
     * to add a human-readable name for the stage that will appear in condition tooltips:
     * <pre>{@code
     * this.add(this.researchStageDescriptionId(myStage), "First Planks Crafted");
     * }</pre>
     */
    protected String researchStageDescriptionId(Identifier stageId) {
        return Util.makeDescriptionId("research_stage", stageId);
    }

    /**
     * Returns the description id for a research stage ref.
     */
    protected String researchStageDescriptionId(ResearchStageRef stageRef) {
        return this.researchStageDescriptionId(stageRef.id());
    }

    /**
     * Convenience method that generates the description id and immediately registers the translation.
     * <pre>{@code
     * this.researchNodeName(myNode, "Crafting Stick");
     * }</pre>
     */
    protected void researchNodeName(ResearchNodeRef nodeRef, String name) {
        this.add(this.researchNodeDescriptionId(nodeRef), name);
    }

    /**
     * Convenience method that generates the description id and immediately registers the translation.
     */
    protected void researchStageName(ResearchStageRef stageRef, String name) {
        this.add(this.researchStageDescriptionId(stageRef), name);
    }
}
