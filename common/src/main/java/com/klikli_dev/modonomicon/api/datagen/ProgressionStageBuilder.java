/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchActionModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCostModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchFactRef;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchNodeSpec;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchEffectModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchValueRef;
import com.klikli_dev.modonomicon.research.data.ResearchStageCompletion;
import com.klikli_dev.modonomicon.research.item.ResearchItemMatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Collects the requirements, completion, rewards and display of one stage of an entry progression.
 * Use it in {@link EntryProvider#stage(String, Consumer)}.
 * <p>
 * Requirements are generated research facts: {@link #craft}, {@link #obtain} and {@link #use} take an item, an item stack
 * (matching its components), an item tag or a {@link ResearchItemMatcher}. {@link #read} takes an entry, and {@link #fact}
 * and {@link #value} take hand-authored research. {@link #action} creates a hand-authored fact that the player satisfies
 * with an action, e.g. paying to learn it.
 * <p>
 * Costs accumulate: {@link #consume}, {@link #manual(ResearchCostModel)} and {@link #cost} add to the cost the stage
 * consumes on completion. Calling any of them makes the stage manual.
 * <p>
 * Texts are literal English: {@link #title}, {@link #description} and the title of {@link #action} are written to the
 * language file under generated keys, e.g. {@code <entry>.stage.<stage>.title}.
 */
public final class ProgressionStageBuilder {
    private final List<ResearchItemMatcher> crafted = new ArrayList<>();
    private final List<ResearchItemMatcher> acquired = new ArrayList<>();
    private final List<UsedItem> used = new ArrayList<>();
    private final List<Identifier> viewed = new ArrayList<>();
    private final List<ResearchFactRef> facts = new ArrayList<>();
    private final List<ActionFact> actionFacts = new ArrayList<>();
    private final List<ResearchNodeSpec.ValueRequirement> values = new ArrayList<>();
    private final List<ResearchEffectModel> rewards = new ArrayList<>();
    private final ResearchCostModel cost = ResearchCostModel.create();
    private boolean manual;
    @Nullable
    private String title;
    @Nullable
    private String description;
    @Nullable
    private ItemStackTemplate icon;

    ProgressionStageBuilder() {
    }

    /**
     * Requires the item to be crafted. Shown as a craft row.
     */
    public ProgressionStageBuilder craft(ItemLike item) {
        return this.craft(new ItemStackTemplate(item.asItem()));
    }

    /**
     * Requires the item to be crafted. Shown as a craft row. Components of the stack must match.
     */
    public ProgressionStageBuilder craft(ItemStackTemplate item) {
        return this.craft(ResearchItemMatcher.of(item));
    }

    /**
     * Requires any item of the tag to be crafted. Shown as a craft row with the first item of the tag as icon.
     */
    public ProgressionStageBuilder craft(TagKey<Item> tag) {
        return this.craft(ResearchItemMatcher.tag(tag));
    }

    /**
     * Requires an item matching the matcher to be crafted. Shown as a craft row.
     */
    public ProgressionStageBuilder craft(ResearchItemMatcher matcher) {
        this.crafted.add(matcher);
        return this;
    }

    /**
     * Requires the item to be obtained. Shown as an obtain row.
     */
    public ProgressionStageBuilder obtain(ItemLike item) {
        return this.obtain(new ItemStackTemplate(item.asItem()));
    }

    /**
     * Requires the item to be obtained. Shown as an obtain row. Components of the stack must match, e.g. a marked research note.
     */
    public ProgressionStageBuilder obtain(ItemStackTemplate item) {
        return this.obtain(ResearchItemMatcher.of(item));
    }

    /**
     * Requires any item of the tag to be obtained. Shown as an obtain row with the first item of the tag as icon.
     */
    public ProgressionStageBuilder obtain(TagKey<Item> tag) {
        return this.obtain(ResearchItemMatcher.tag(tag));
    }

    /**
     * Requires an item matching the matcher to be obtained. Shown as an obtain row.
     */
    public ProgressionStageBuilder obtain(ResearchItemMatcher matcher) {
        this.acquired.add(matcher);
        return this;
    }

    /**
     * Requires the item to be used in the air. Shown as a use row. Consumes nothing.
     */
    public ProgressionStageBuilder use(ItemLike item) {
        return this.use(new ItemStackTemplate(item.asItem()));
    }

    /**
     * Requires the item to be used in the air. Shown as a use row. Components of the stack must match. Consumes nothing.
     */
    public ProgressionStageBuilder use(ItemStackTemplate item) {
        return this.use(item, 0);
    }

    /**
     * Requires any item of the tag to be used in the air. Shown as a use row. Consumes nothing.
     */
    public ProgressionStageBuilder use(TagKey<Item> tag) {
        return this.use(ResearchItemMatcher.tag(tag), 0);
    }

    /**
     * Requires an item matching the matcher to be used in the air. Shown as a use row. Consumes nothing.
     */
    public ProgressionStageBuilder use(ResearchItemMatcher matcher) {
        return this.use(matcher, 0);
    }

    /**
     * Requires the item to be used in the air. The used stack loses {@code consume} items when the use changes research state.
     */
    public ProgressionStageBuilder use(ItemStackTemplate item, int consume) {
        return this.use(ResearchItemMatcher.of(item), consume);
    }

    /**
     * Requires an item matching the matcher to be used in the air. The used stack loses {@code consume} items when the use
     * changes research state.
     */
    public ProgressionStageBuilder use(ResearchItemMatcher matcher, int consume) {
        this.used.add(new UsedItem(matcher, consume));
        return this;
    }

    /**
     * Requires the entry with the given id to be viewed. Shown as a read row.
     */
    public ProgressionStageBuilder read(Identifier entryId) {
        this.viewed.add(entryId);
        return this;
    }

    /**
     * Requires the given entry to be viewed. Shown as a read row.
     */
    public ProgressionStageBuilder read(BookEntryModel entry) {
        return this.read(entry.getId());
    }

    /**
     * Requires a hand-authored research fact to be granted.
     */
    public ProgressionStageBuilder fact(ResearchFactRef fact) {
        this.facts.add(fact);
        return this;
    }

    /**
     * Requires a fact that the player satisfies with an action, e.g. paying to learn it. The fact is created for this stage
     * and its id is {@code id} within the stage. The title is literal English and is written to the language file under
     * {@code <entry>.stage.<stage>.action.<id>.title}.
     * <pre>{@code
     * s.action("observation", "Observe the sky", ResearchActionModel.learn(ResearchCostModel.create().item(Items.PAPER)))
     * }</pre>
     */
    public ProgressionStageBuilder action(String id, String title, ResearchActionModel action) {
        this.actionFacts.add(new ActionFact(id, title, action));
        return this;
    }

    /**
     * Requires a hand-authored research value to reach the given threshold.
     */
    public ProgressionStageBuilder value(ResearchValueRef value, int threshold) {
        this.values.add(new ResearchNodeSpec.ValueRequirement(value, threshold));
        return this;
    }

    /**
     * The stage is completed by hand once its requirements are met, see {@code ResearchStages.completeManual}.
     */
    public ProgressionStageBuilder manual() {
        this.manual = true;
        return this;
    }

    /**
     * The stage is completed by hand and consumes the cost on completion. The cost is merged into the cost collected so far.
     */
    public ProgressionStageBuilder manual(ResearchCostModel cost) {
        this.manual = true;
        this.cost.merge(cost);
        return this;
    }

    /**
     * Edits the cost the stage consumes on completion fluently, e.g. {@code cost(c -> c.item(Items.PAPER).levels(2))}.
     * The operator edits the collected cost in place. Implies {@link #manual()}.
     */
    public ProgressionStageBuilder cost(UnaryOperator<ResearchCostModel> update) {
        this.manual = true;
        update.apply(this.cost);
        return this;
    }

    /**
     * Consumes {@code count} of the item on completion. Implies {@link #manual()}.
     */
    public ProgressionStageBuilder consume(ItemLike item, int count) {
        return this.consume(new ItemStackTemplate(item.asItem(), count));
    }

    /**
     * Consumes the stack on completion, in its count, matching its components. Implies {@link #manual()}.
     */
    public ProgressionStageBuilder consume(ItemStackTemplate stack) {
        this.manual = true;
        this.cost.item(stack);
        return this;
    }

    /**
     * Grants a reward once when the stage completes.
     */
    public ProgressionStageBuilder reward(ResearchEffectModel reward) {
        this.rewards.add(reward);
        return this;
    }

    /**
     * The title of the stage, literal English, shown as "Next: title" in requirement blocks. Defaults to the stage id, capitalized.
     */
    public ProgressionStageBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * The description of the stage, literal English, shown in requirement blocks. Optional.
     */
    public ProgressionStageBuilder description(String description) {
        this.description = description;
        return this;
    }

    /**
     * The icon of the stage. Defaults to the first crafted, obtained or used item of the stage.
     */
    public ProgressionStageBuilder icon(ItemLike icon) {
        return this.icon(new ItemStackTemplate(icon.asItem()));
    }

    /**
     * The icon of the stage. Defaults to the first crafted, obtained or used item of the stage.
     */
    public ProgressionStageBuilder icon(ItemStackTemplate icon) {
        this.icon = icon;
        return this;
    }

    boolean hasRequirements() {
        return !this.crafted.isEmpty() || !this.acquired.isEmpty() || !this.used.isEmpty() || !this.viewed.isEmpty()
                || !this.facts.isEmpty() || !this.actionFacts.isEmpty() || !this.values.isEmpty();
    }

    @Nullable
    String title() {
        return this.title;
    }

    @Nullable
    String description() {
        return this.description;
    }

    /**
     * The icon of the stage: the explicit icon, else the first crafted, obtained or used item. For tags the first item
     * of the tag. Null if none of these is available.
     */
    @Nullable
    ItemStackTemplate icon() {
        if (this.icon != null) {
            return this.icon;
        }
        if (!this.crafted.isEmpty()) {
            return iconOf(this.crafted.get(0));
        }
        if (!this.acquired.isEmpty()) {
            return iconOf(this.acquired.get(0));
        }
        if (!this.used.isEmpty()) {
            return iconOf(this.used.get(0).target());
        }
        return null;
    }

    List<ResearchItemMatcher> crafted() {
        return List.copyOf(this.crafted);
    }

    List<ResearchItemMatcher> acquired() {
        return List.copyOf(this.acquired);
    }

    List<UsedItem> used() {
        return List.copyOf(this.used);
    }

    List<Identifier> viewed() {
        return List.copyOf(this.viewed);
    }

    List<ResearchFactRef> facts() {
        return List.copyOf(this.facts);
    }

    List<ActionFact> actionFacts() {
        return List.copyOf(this.actionFacts);
    }

    List<ResearchNodeSpec.ValueRequirement> values() {
        return List.copyOf(this.values);
    }

    /**
     * The completion of the stage: manual with the collected cost, or automatic.
     */
    ResearchStageCompletion completion() {
        return this.manual ? ResearchStageCompletion.manual(this.cost.toResearchCost()) : ResearchStageCompletion.AUTO;
    }

    List<ResearchEffectModel> rewards() {
        return List.copyOf(this.rewards);
    }

    @Nullable
    private static ItemStackTemplate iconOf(ResearchItemMatcher matcher) {
        return matcher.value().left().orElseGet(() -> {
            ItemStack stack = matcher.displayStack();
            return stack.isEmpty() ? null : new ItemStackTemplate(stack.getItem());
        });
    }

    /**
     * An item that must be used in the air, and how many of it the use consumes.
     *
     * @param target  the item matcher the use accepts
     * @param consume the number of items the use consumes
     */
    public record UsedItem(ResearchItemMatcher target, int consume) {
    }

    /**
     * A hand-authored fact created by the stage, see {@link ProgressionStageBuilder#action(String, String, ResearchActionModel)}.
     */
    record ActionFact(String id, String title, ResearchActionModel action) {
    }
}
