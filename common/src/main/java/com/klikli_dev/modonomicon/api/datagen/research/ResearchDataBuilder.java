/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import com.klikli_dev.modonomicon.data.TriggerType;
import com.klikli_dev.modonomicon.registry.TriggerTypeRegistry;
import com.klikli_dev.modonomicon.research.data.ResearchCategories;
import com.klikli_dev.modonomicon.research.data.ResearchCategoryDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchFactDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchHookDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchNodeDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchToastDefinition;
import com.klikli_dev.modonomicon.research.data.ResearchValueDefinition;
import com.klikli_dev.modonomicon.research.hook.NoTriggerTarget;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Low-level research authoring collector that compiles typed authoring input into canonical research
 * definition records.
 *
 * This class is intentionally close to the output model. Higher-level fluent syntax such as ingress
 * helpers should delegate into this builder rather than bypassing it.
 */
public final class ResearchDataBuilder {
    private final String namespace;
    private final List<ResearchFactSpec> facts = new ArrayList<>();
    private final List<ResearchValueSpec> values = new ArrayList<>();
    private final List<ResearchNodeSpec> nodes = new ArrayList<>();
    private final List<ResearchHookSpec<?>> hooks = new ArrayList<>();
    private final List<ResearchCategoryModel> categories = new ArrayList<>();

    /**
     * Creates a builder that will namespace authored relative paths under the given namespace.
     */
    public ResearchDataBuilder(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Declares one explicit research fact definition and returns a typed ref to it.
     *
     * Facts are primitive durable inputs in the research model. Hooks grant facts, and nodes query
     * those granted facts as part of their unlock requirements.
     *
     * @throws IllegalArgumentException if a fact with the same id was already declared
     */
    public ResearchFactRef fact(String path) {
        return this.fact(ResearchFactRef.of(Identifier.fromNamespaceAndPath(this.namespace, path)));
    }

    /**
     * Declares one explicit research fact definition for an already-known typed ref and returns that same ref.
     * The ref's id is used as-is, so it may live in any namespace.
     *
     * @throws IllegalArgumentException if a fact with the same id was already declared
     */
    public ResearchFactRef fact(ResearchFactRef ref) {
        if (this.facts.stream().anyMatch(spec -> spec.ref().id().equals(ref.id()))) {
            throw new IllegalArgumentException("Research fact '" + ref.id() + "' is already declared");
        }
        this.facts.add(ResearchFactSpec.of(ref));
        return ref;
    }

    /**
     * Declares one explicit research value definition and returns a typed ref to it.
     *
     * Values are numeric counters that hooks can increment and nodes can require reaching a threshold.
     *
     * @throws IllegalArgumentException if a value with the same id was already declared
     */
    public ResearchValueRef value(String path) {
        return this.value(ResearchValueRef.of(Identifier.fromNamespaceAndPath(this.namespace, path)));
    }

    /**
     * Declares one explicit research value definition for an already-known typed ref and returns that same ref.
     * The ref's id is used as-is, so it may live in any namespace.
     *
     * @throws IllegalArgumentException if a value with the same id was already declared
     */
    public ResearchValueRef value(ResearchValueRef ref) {
        if (this.values.stream().anyMatch(spec -> spec.ref().id().equals(ref.id()))) {
            throw new IllegalArgumentException("Research value '" + ref.id() + "' is already declared");
        }
        this.values.add(ResearchValueSpec.of(ref));
        return ref;
    }

    /**
     * Returns the given fact ref after checking that it was declared in this builder.
     *
     * @throws IllegalArgumentException if the fact was never declared
     */
    public ResearchFactRef requireFact(ResearchFactRef ref) {
        if (this.facts.stream().noneMatch(spec -> spec.ref().id().equals(ref.id()))) {
            throw new IllegalArgumentException("Research fact '" + ref.id() + "' was never declared in this builder");
        }
        return ref;
    }

    /**
     * Returns the given value ref after checking that it was declared in this builder.
     *
     * @throws IllegalArgumentException if the value was never declared
     */
    public ResearchValueRef requireValue(ResearchValueRef ref) {
        if (this.values.stream().noneMatch(spec -> spec.ref().id().equals(ref.id()))) {
            throw new IllegalArgumentException("Research value '" + ref.id() + "' was never declared in this builder");
        }
        return ref;
    }

    /**
     * Declares one explicit research node definition and returns a typed ref to it.
     *
     * In the current implementation, the given fact refs become the node's required facts in the
     * generated canonical research resource.
     */
    public ResearchNodeRef node(String path, ResearchFactRef... requiredFacts) {
        var ref = ResearchNodeRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
        this.addNode(ResearchNodeSpec.factsOnly(ref, List.of(requiredFacts)));
        return ref;
    }

    /**
     * Declares one explicit research node definition for an already-known typed ref and returns that
     * same ref.
     *
     * This overload lets higher-level authoring code centralize node ids in a typed ref catalog
     * while still compiling to the same canonical node definition output.
     */
    public ResearchNodeRef node(ResearchNodeRef ref, ResearchFactRef... requiredFacts) {
        this.addNode(ResearchNodeSpec.factsOnly(ref, List.of(requiredFacts)));
        return ref;
    }

    /**
     * Declares a research node with both fact and value requirements.
     */
    public ResearchNodeRef node(ResearchNodeRef ref, List<ResearchFactRef> requiredFacts, List<ResearchNodeSpec.ValueRequirement> requiredValues) {
        this.addNode(ResearchNodeSpec.of(ref, requiredFacts, requiredValues, List.of(), List.of(), Optional.empty()));
        return ref;
    }

    /**
     * Creates a typed stage ref for use in node stage declarations.
     * The stage itself is defined inline in the node spec.
     */
    public ResearchStageRef stageRef(String path) {
        return ResearchStageRef.of(Identifier.fromNamespaceAndPath(this.namespace, path));
    }

    /**
     * Declares a research node with stages and stage dependencies.
     *
     * @deprecated use {@link #nodeBuilder(ResearchNodeRef)} which covers stages, stage dependencies, values and toast.
     */
    @Deprecated(forRemoval = true)
    public ResearchNodeRef node(
            ResearchNodeRef ref,
            List<ResearchFactRef> requiredFacts,
            List<ResearchNodeSpec.ValueRequirement> requiredValues,
            List<ResearchStageSpec> stages,
            List<ResearchNodeSpec.StageDependencySpec> requiredStages
    ) {
        this.addNode(ResearchNodeSpec.of(ref, requiredFacts, requiredValues, stages, requiredStages, Optional.empty()));
        return ref;
    }

    /**
     * Declares a research node with stages, stage dependencies, and optional toast.
     *
     * @deprecated use {@link #nodeBuilder(ResearchNodeRef)} which covers stages, stage dependencies, values and toast.
     */
    @Deprecated(forRemoval = true)
    public ResearchNodeRef node(
            ResearchNodeRef ref,
            List<ResearchFactRef> requiredFacts,
            List<ResearchNodeSpec.ValueRequirement> requiredValues,
            List<ResearchStageSpec> stages,
            List<ResearchNodeSpec.StageDependencySpec> requiredStages,
            ResearchToastDefinition toast
    ) {
        this.addNode(ResearchNodeSpec.of(ref, requiredFacts, requiredValues, stages, requiredStages, Optional.ofNullable(toast)));
        return ref;
    }

    /**
     * Creates a fluent builder for authoring a research node with facts, values, stages, stage
     * dependencies and toast. Call {@link ResearchNodeBuilder#build()} to register the node.
     */
    public ResearchNodeBuilder nodeBuilder(ResearchNodeRef ref) {
        return new ResearchNodeBuilder(this, ref);
    }

    /**
     * Registers a compiled node spec. Used by {@link ResearchNodeBuilder}.
     *
     * @throws IllegalArgumentException if a node with the same id was already declared
     */
    void addNode(ResearchNodeSpec spec) {
        if (this.nodes.stream().anyMatch(existing -> existing.ref().id().equals(spec.ref().id()))) {
            throw new IllegalArgumentException("Research node '" + spec.ref().id() + "' is already declared");
        }
        this.nodes.add(spec);
    }

    /**
     * Adds toast display data to an existing fact.
     *
     * @throws IllegalArgumentException if the fact was never declared
     */
    public void toast(ResearchFactRef factRef, ResearchToastDefinition toast) {
        var index = this.indexOfFact(factRef);
        this.facts.set(index, this.facts.get(index).withToast(toast));
    }

    /**
     * Adds toast display data to an existing value.
     *
     * @throws IllegalArgumentException if the value was never declared
     */
    public void toast(ResearchValueRef valueRef, ResearchToastDefinition toast) {
        var index = this.indexOfValue(valueRef);
        this.values.set(index, this.values.get(index).withToast(toast));
    }

    /**
     * Adds toast display data to an existing node.
     *
     * @throws IllegalArgumentException if the node was never declared
     */
    public void toast(ResearchNodeRef nodeRef, ResearchToastDefinition toast) {
        var index = this.indexOfNode(nodeRef);
        this.nodes.set(index, this.nodes.get(index).withToast(toast));
    }

    /**
     * Merges display data into an existing fact. Parts unset in the display keep their previous value, so calling this
     * twice does not drop the icon or title. Use {@link #display(ResearchFactRef, UnaryOperator)} to edit the display in place.
     *
     * @throws IllegalArgumentException if the fact was never declared
     */
    public void display(ResearchFactRef factRef, ResearchDisplayModel display) {
        this.display(factRef, current -> current.merge(display));
    }

    /**
     * Updates the display of an existing fact. The operator receives a copy of the existing display or an empty one.
     *
     * @throws IllegalArgumentException if the fact was never declared
     */
    public void display(ResearchFactRef factRef, UnaryOperator<ResearchDisplayModel> update) {
        var index = this.indexOfFact(factRef);
        this.facts.set(index, this.facts.get(index).withDisplay(update));
    }

    /**
     * Merges display data into an existing value, see {@link #display(ResearchFactRef, ResearchDisplayModel)}.
     *
     * @throws IllegalArgumentException if the value was never declared
     */
    public void display(ResearchValueRef valueRef, ResearchDisplayModel display) {
        this.display(valueRef, current -> current.merge(display));
    }

    /**
     * Updates the display of an existing value, see {@link #display(ResearchFactRef, UnaryOperator)}.
     *
     * @throws IllegalArgumentException if the value was never declared
     */
    public void display(ResearchValueRef valueRef, UnaryOperator<ResearchDisplayModel> update) {
        var index = this.indexOfValue(valueRef);
        this.values.set(index, this.values.get(index).withDisplay(update));
    }

    /**
     * Merges display data into an existing node, see {@link #display(ResearchFactRef, ResearchDisplayModel)}.
     *
     * @throws IllegalArgumentException if the node was never declared
     */
    public void display(ResearchNodeRef nodeRef, ResearchDisplayModel display) {
        this.display(nodeRef, current -> current.merge(display));
    }

    /**
     * Updates the display of an existing node, see {@link #display(ResearchFactRef, UnaryOperator)}.
     *
     * @throws IllegalArgumentException if the node was never declared
     */
    public void display(ResearchNodeRef nodeRef, UnaryOperator<ResearchDisplayModel> update) {
        var index = this.indexOfNode(nodeRef);
        this.nodes.set(index, this.nodes.get(index).withDisplay(update));
    }

    /**
     * Declares a requirement category of this bundle and returns its id. Assign it via {@link ResearchDisplayModel#withCategory(Identifier)}.
     * Declaring a category with the id of a built-in category (see {@link ResearchCategories}) overrides the built-in one.
     *
     * @throws IllegalArgumentException if a category with the same id was already declared
     */
    public Identifier category(ResearchCategoryModel category) {
        if (this.categories.stream().anyMatch(existing -> existing.id().equals(category.id()))) {
            throw new IllegalArgumentException("Research category '" + category.id() + "' is already declared");
        }
        this.categories.add(category);
        return category.id();
    }

    private int indexOfFact(ResearchFactRef ref) {
        for (int i = 0; i < this.facts.size(); i++) {
            if (this.facts.get(i).ref().id().equals(ref.id())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Research fact '" + ref.id() + "' was never declared in this builder");
    }

    private int indexOfValue(ResearchValueRef ref) {
        for (int i = 0; i < this.values.size(); i++) {
            if (this.values.get(i).ref().id().equals(ref.id())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Research value '" + ref.id() + "' was never declared in this builder");
    }

    private int indexOfNode(ResearchNodeRef ref) {
        for (int i = 0; i < this.nodes.size(); i++) {
            if (this.nodes.get(i).ref().id().equals(ref.id())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Research node '" + ref.id() + "' was never declared in this builder");
    }

    // --- Generic hook ingress ---

    /**
     * Returns the namespace that relative paths authored through this builder are placed in.
     */
    public String namespace() {
        return this.namespace;
    }

    /**
     * Returns a hook path derived from the given ref id that is not yet used by any hook in this builder.
     *
     * The base path is the ref path with {@code _hook} appended. Refs outside this builder's namespace are
     * additionally prefixed with their namespace ({@code <namespace>/<path>_hook}) so that refs with equal
     * paths in different namespaces do not collide. If the base path is already taken (e.g. because the same
     * ref is granted from multiple triggers), a numeric suffix ({@code _2}, {@code _3}, ...) is appended.
     */
    public String derivedHookPath(Identifier refId) {
        var basePath = (refId.getNamespace().equals(this.namespace) ? "" : refId.getNamespace() + "/") + refId.getPath() + "_hook";
        var path = basePath;
        for (int i = 2; this.hasHook(path); i++) {
            path = basePath + "_" + i;
        }
        return path;
    }

    private boolean hasHook(String hookPath) {
        var id = Identifier.fromNamespaceAndPath(this.namespace, hookPath);
        return this.hooks.stream().anyMatch(spec -> spec.id().equals(id));
    }

    private void addHook(ResearchHookSpec<?> spec) {
        if (this.hooks.stream().anyMatch(existing -> existing.id().equals(spec.id()))) {
            throw new IllegalArgumentException("Research hook '" + spec.id() + "' is already declared");
        }
        this.hooks.add(spec);
    }

    /**
     * Grants a fact when the specified trigger type fires for the given target.
     * This is the generic entry point for third-party trigger types.
     *
     * @throws IllegalArgumentException if a hook with the same id was already declared
     */
    public <TTarget> void grantFact(String hookPath, TriggerType<TTarget, ?> triggerType, TTarget target, ResearchFactRef factRef) {
        this.grantFact(hookPath, triggerType, target, factRef, 0);
    }

    /**
     * Grants a fact when the specified trigger type fires for the given target, consuming the given number of items
     * of the used stack when the hook changes research state. Only item_used hooks consume items.
     */
    public <TTarget> void grantFact(String hookPath, TriggerType<TTarget, ?> triggerType, TTarget target, ResearchFactRef factRef, int consume) {
        this.addHook(ResearchHookSpec.of(
                Identifier.fromNamespaceAndPath(this.namespace, hookPath),
                triggerType, target, factRef, null, 1, consume
        ));
    }

    /**
     * Increments a value when the specified trigger type fires for the given target.
     * This is the generic entry point for third-party trigger types.
     *
     * @throws IllegalArgumentException if a hook with the same id was already declared
     */
    public <TTarget> void incrementValue(String hookPath, TriggerType<TTarget, ?> triggerType, TTarget target, ResearchValueRef valueRef, int increment) {
        this.incrementValue(hookPath, triggerType, target, valueRef, increment, 0);
    }

    /**
     * Increments a value when the specified trigger type fires for the given target, consuming the given number of items
     * of the used stack when the hook changes research state. Only item_used hooks consume items.
     */
    public <TTarget> void incrementValue(String hookPath, TriggerType<TTarget, ?> triggerType, TTarget target, ResearchValueRef valueRef, int increment, int consume) {
        this.addHook(ResearchHookSpec.of(
                Identifier.fromNamespaceAndPath(this.namespace, hookPath),
                triggerType, target, null, valueRef, increment, consume
        ));
    }

    /**
     * Grants a fact when the specified targetless trigger type fires.
     */
    public void grantFact(String hookPath, TriggerType<NoTriggerTarget, ?> triggerType, ResearchFactRef factRef) {
        this.grantFact(hookPath, triggerType, NoTriggerTarget.INSTANCE, factRef);
    }

    /**
     * Increments a value when the specified targetless trigger type fires.
     */
    public void incrementValue(String hookPath, TriggerType<NoTriggerTarget, ?> triggerType, ResearchValueRef valueRef, int increment) {
        this.incrementValue(hookPath, triggerType, NoTriggerTarget.INSTANCE, valueRef, increment);
    }

    // --- Convenience sugar (deprecated, delegates to generic) ---

    /**
     * Declares an explicit {@code entry_viewed_once} research ingress hook that grants a fact.
     *
     * @deprecated use {@link ResearchIngressHelper#onEntryViewedOnce(Identifier)} or {@link #grantFact(String, TriggerType, Object, ResearchFactRef)}.
     */
    @Deprecated(forRemoval = true)
    public void grantFactOnEntryViewedOnce(String path, Identifier entryId, ResearchFactRef factRef) {
        this.grantFact(path, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId, factRef);
    }

    /**
     * Declares an explicit {@code entry_viewed_once} research ingress hook that increments a value.
     *
     * @deprecated use {@link ResearchIngressHelper#onEntryViewedOnce(Identifier)} or {@link #incrementValue(String, TriggerType, Object, ResearchValueRef, int)}.
     */
    @Deprecated(forRemoval = true)
    public void incrementValueOnEntryViewedOnce(String path, Identifier entryId, ResearchValueRef valueRef, int increment) {
        this.incrementValue(path, TriggerTypeRegistry.ENTRY_VIEWED_ONCE, entryId, valueRef, increment);
    }

    /**
     * Declares an explicit advancement-earned research ingress hook that grants a fact.
     *
     * @deprecated use {@link ResearchIngressHelper#onAdvancementEarned(Identifier)} or {@link #grantFact(String, TriggerType, Object, ResearchFactRef)}.
     */
    @Deprecated(forRemoval = true)
    public void grantFactOnAdvancementEarned(String path, Identifier advancementId, ResearchFactRef factRef) {
        this.grantFact(path, TriggerTypeRegistry.ADVANCEMENT, advancementId, factRef);
    }

    /**
     * Declares an explicit advancement-earned research ingress hook that increments a value.
     *
     * @deprecated use {@link ResearchIngressHelper#onAdvancementEarned(Identifier)} or {@link #incrementValue(String, TriggerType, Object, ResearchValueRef, int)}.
     */
    @Deprecated(forRemoval = true)
    public void incrementValueOnAdvancementEarned(String path, Identifier advancementId, ResearchValueRef valueRef, int increment) {
        this.incrementValue(path, TriggerTypeRegistry.ADVANCEMENT, advancementId, valueRef, increment);
    }

    /**
     * Declares an explicit {@code item_crafted} research ingress hook that grants a fact.
     *
     * @deprecated use {@link ResearchIngressHelper#onItemCrafted(ItemStackTemplate)} or {@link #grantFact(String, TriggerType, Object, ResearchFactRef)}.
     */
    @Deprecated(forRemoval = true)
    public void grantFactOnItemCrafted(String path, ItemStackTemplate targetItem, ResearchFactRef factRef) {
        this.grantFact(path, TriggerTypeRegistry.ITEM_CRAFTED, ResearchItemMatcher.of(targetItem), factRef);
    }

    /**
     * Declares an explicit {@code item_crafted} research ingress hook that increments a value.
     *
     * @deprecated use {@link ResearchIngressHelper#onItemCrafted(ItemStackTemplate)} or {@link #incrementValue(String, TriggerType, Object, ResearchValueRef, int)}.
     */
    @Deprecated(forRemoval = true)
    public void incrementValueOnItemCrafted(String path, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment) {
        this.incrementValue(path, TriggerTypeRegistry.ITEM_CRAFTED, ResearchItemMatcher.of(targetItem), valueRef, increment);
    }

    /**
     * Declares an explicit {@code item_acquired} research ingress hook that grants a fact.
     *
     * @deprecated use {@link ResearchIngressHelper#onItemAcquired(ItemStackTemplate)} or {@link #grantFact(String, TriggerType, Object, ResearchFactRef)}.
     */
    @Deprecated(forRemoval = true)
    public void grantFactOnItemAcquired(String path, ItemStackTemplate targetItem, ResearchFactRef factRef) {
        this.grantFact(path, TriggerTypeRegistry.ITEM_ACQUIRED, ResearchItemMatcher.of(targetItem), factRef);
    }

    /**
     * Declares an explicit {@code item_acquired} research ingress hook that increments a value.
     *
     * @deprecated use {@link ResearchIngressHelper#onItemAcquired(ItemStackTemplate)} or {@link #incrementValue(String, TriggerType, Object, ResearchValueRef, int)}.
     */
    @Deprecated(forRemoval = true)
    public void incrementValueOnItemAcquired(String path, ItemStackTemplate targetItem, ResearchValueRef valueRef, int increment) {
        this.incrementValue(path, TriggerTypeRegistry.ITEM_ACQUIRED, ResearchItemMatcher.of(targetItem), valueRef, increment);
    }

    // --- Definition compilation ---

    /**
     * Returns the compiled canonical fact definitions collected so far.
     */
    public List<ResearchFactDefinition> factDefinitions() {
        return this.facts.stream().map(ResearchFactSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical value definitions collected so far.
     */
    public List<ResearchValueDefinition> valueDefinitions() {
        return this.values.stream().map(ResearchValueSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical node definitions collected so far.
     */
    public List<ResearchNodeDefinition> nodeDefinitions() {
        return this.nodes.stream().map(ResearchNodeSpec::toDefinition).toList();
    }

    /**
     * Returns the compiled canonical category definitions declared in this bundle.
     */
    public List<ResearchCategoryDefinition> categoryDefinitions() {
        return this.categories.stream().map(ResearchCategoryModel::toDefinition).toList();
    }

    /**
     * Returns ALL compiled hook definitions (all trigger types in one list).
     */
    @SuppressWarnings("unchecked")
    public List<ResearchHookDefinition<?>> hookDefinitions() {
        return (List<ResearchHookDefinition<?>>) (List<?>) this.hooks.stream().map(ResearchHookSpec::toDefinition).toList();
    }
}
