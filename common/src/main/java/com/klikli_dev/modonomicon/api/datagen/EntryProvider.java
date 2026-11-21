/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.api.datagen.GeneratedBookResearchStore.ActionFact;
import com.klikli_dev.modonomicon.api.datagen.GeneratedBookResearchStore.StageRequest;
import com.klikli_dev.modonomicon.api.datagen.GeneratedBookResearchStore.EntryResearchRequest;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryTabModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookNoneConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookScholarshipPageModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchDisplayModel;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchStageRef;
import com.klikli_dev.modonomicon.book.ScholarshipDisplay;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EntryProvider extends ModonomiconProviderBase {

    protected final CategoryProviderBase parent;

    protected BookEntryModel entry;
    private final List<StageRequest> researchStages = new ArrayList<>();
    private final List<PendingScholarshipPage> scholarshipPages = new ArrayList<>();
    private ResearchStageRef currentStage;
    private boolean scholarshipPageUsed;

    public EntryProvider(CategoryProviderBase parent) {
        super(parent.modId(), parent.lang(), parent.langsAsMapOfBiConsumers(), parent.context(), parent.condition());
        this.parent = parent;
        this.entry = null;
    }

    @Override
    protected HolderLookup.Provider registries() {
        return this.parent.registries();
    }

    @Override
    protected Map<String, String> macros() {
        return Stream.concat(super.macros().entrySet().stream(), this.parent.macros().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

    /**
     * Adds a page title for the current context to the underlying language provider.
     */
    protected void pageTitle(String title) {
        this.add(this.context().pageTitle(), title);
    }

    /**
     * Adds a page text for the current context to the underlying language provider.
     */
    protected void pageText(String text) {
        this.add(this.context().pageText(), text);
    }

    /**
     * Adds a page text for the current context to the underlying language provider.
     */
    protected void pageText(String text, Object... args) {
        this.add(this.context().pageText(), text, args);
    }

    /**
     * Adds a page to the cached pages of this category provider.
     * Make sure to call this.context().page(<pageId>) before calling this method!
     * The page will be added to the next entry created with this.entry(...)
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param model the page model
     */
    protected <T extends BookPageModel<?>> T page(T model) {
        return this.add(model);
    }

    /**
     * Registers the page with the current context and adds it to the cached pages of this category provider.
     * No need to call this.context().page(<pageId>). This method will do that for you.
     * The page will be added to the next entry created with this.entry(...)
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param modelSupplier A supplier that provides a page model. It is a supplier, because that way you can use this.context() within the supplier and it will correctly use the given page as part of the context.
     */
    protected <T extends BookPageModel<?>> T page(String page, Supplier<T> modelSupplier) {
        this.context().page(page);
        var model = modelSupplier.get();
        model.withId(page);
        return this.add(model);
    }

    /**
     * Controls whether pages for this entry are generated as separate JSON files.
     * Default is {@code true} (separate files).
     * Set to {@code false} to include pages inline in the entry's JSON (legacy behavior).
     */
    protected void generatePagesAsFiles(boolean generatePagesAsFiles) {
        this.entry.withGeneratePagesAsFiles(generatePagesAsFiles);
    }

    /**
     * Controls whether tabs for this entry are generated as separate JSON files.
     * Default is {@code true} (separate files).
     * Set to {@code false} to include tabs inline in the entry's JSON.
     */
    protected void generateTabsAsFiles(boolean generateTabsAsFiles) {
        this.entry.withGenerateTabsAsFiles(generateTabsAsFiles);
    }

    /**
     * Adds a tab title for the current context to the underlying language provider.
     * Like {@link #pageTitle(String)} it can be called right after {@link #tab(String, Supplier)}, as the tab stays the current context
     * until the next tab is declared. Use the translation key {@link #tabTitleKey()} in the tab model, e.g. {@code .withTitle(this.tabTitleKey())}.
     */
    protected void tabTitle(String title) {
        this.add(this.context().tabTitle(), title);
    }

    /**
     * The translation key of the title of the tab in the current context.
     */
    protected String tabTitleKey() {
        return this.context().tabTitle();
    }

    /**
     * Adds a tab text for the current context to the underlying language provider, e.g. for the text of the tab page.
     * Like {@link #pageText(String)} it can be called right after {@link #tab(String, Supplier)}.
     * Use the translation key {@link #tabTextKey()} in the tab model.
     */
    protected void tabText(String text) {
        this.add(this.context().tabText(), text);
    }

    /**
     * The translation key of the text of the tab in the current context.
     */
    protected String tabTextKey() {
        return this.context().tabText();
    }

    /**
     * Registers a tab with the current context and adds it to the tabs of the entry created with this.entry(...).
     * The tab is pinned to the page most recently declared with {@link #page(String, Supplier)} in this entry,
     * i.e. it is only visible while that page is on the open spread. Several tabs declared after the same page all pin to it.
     * An anchor set explicitly on the returned model is kept.
     * The tab is written as a file named after the id, see {@link #generateTabsAsFiles(boolean)}.
     * Needs to be called after this.context().entry(<entryId>) and after the page it should pin to has been declared.
     *
     * @param id            the tab id, used as file name and as id of the tab page if it has none
     * @param modelSupplier A supplier that provides a tab model. It is a supplier, because that way you can use this.context() within the supplier, e.g. for tabTitleKey().
     * @throws IllegalStateException if no page has been declared yet in this entry, use {@link #entryTab(String, Supplier)} for entry-wide tabs
     */
    protected BookEntryTabModel tab(String id, Supplier<BookEntryTabModel> modelSupplier) {
        var pageId = this.context().pageId();
        if (pageId == null) {
            throw new IllegalStateException("Tab '" + id + "' in entry '" + this.entryId() + "' has no page to pin to. "
                    + "Declare a page with this.page(...) before this.tab(...), or use this.entryTab(...) for a tab that is visible for the whole entry.");
        }

        var model = this.addTab(id, modelSupplier);
        if (model.getAnchor() == null) {
            model.withAnchor(pageId);
        }
        return model;
    }

    /**
     * Registers an entry-wide tab with the current context and adds it to the tabs of the entry created with this.entry(...).
     * Unlike {@link #tab(String, Supplier)} the tab is not pinned to a page, so it is visible for the whole entry.
     * An anchor set explicitly on the returned model is kept.
     * The tab is written as a file named after the id, see {@link #generateTabsAsFiles(boolean)}.
     * Needs to be called after this.context().entry(<entryId>)
     *
     * @param id            the tab id, used as file name and as id of the tab page if it has none
     * @param modelSupplier A supplier that provides a tab model. It is a supplier, because that way you can use this.context() within the supplier, e.g. for tabTitleKey().
     */
    protected BookEntryTabModel entryTab(String id, Supplier<BookEntryTabModel> modelSupplier) {
        return this.addTab(id, modelSupplier);
    }

    private BookEntryTabModel addTab(String id, Supplier<BookEntryTabModel> modelSupplier) {
        this.context().tab(id);
        var model = modelSupplier.get();
        model.withId(id);
        if (this.currentStage != null) {
            model.withCondition(this.stageGate(model.getCondition()));
        }
        this.entry.withTab(model);
        return model;
    }

    /**
     * Declares a stage of the research of this entry. Pages and tabs declared after a stage are only
     * visible once the stage is completed. Requirements are declared with the builder, see {@link ResearchStageBuilder}.
     * This is the way to declare a research: the research node, its stages and the facts of the requirements are generated.
     * <p>
     * The first stage's requirements unlock the content after it; content declared before the first stage is not gated.
     * A stage needs at least one requirement (or a cost to consume) and a unique id within the entry. Texts given to the builder are literal English;
     * they are written to the language file under keys generated from the entry and stage id.
     * <p>
     * Example:
     * <pre>{@code
     * this.page("intro", ...);                  // visible from the start
     * this.stage("quickening", s -> s
     *         .craft(Items.CLAY)                // generated item_crafted fact
     *         .obtain(ItemTags.SAND)            // generated item_acquired fact for any item of the tag
     *         .read(OtherEntry.ID)              // generated entry_viewed_once fact
     *         .value(SOME_VALUE, 3)             // value threshold
     *         .title("Breathing life into clay")
     *         .icon(Items.CLAY_BALL));
     * this.page("notebook2", ...);              // only visible after "quickening" is completed
     * this.scholarshipPage("next");            // shows the requirements of the next stage, see scholarshipPage
     * this.stage("mastery", s -> s.craft(Items.GOLD_INGOT).consume(Items.GOLD_INGOT, 1));
     * }</pre>
     *
     * @param id            the stage id, unique within the entry. Used in the generated research and lang keys.
     * @param configuration configures the requirements, completion, rewards and display of the stage
     * @throws IllegalStateException if the id is already used or the stage has no requirements
     */
    protected void stage(String id, Consumer<ResearchStageBuilder> configuration) {
        var stageRef = this.condition().entryResearchStage(this.entry, id);
        if (this.researchStages.stream().anyMatch(stage -> stage.ref().equals(stageRef))) {
            throw new IllegalStateException("Duplicate stage id '" + id + "' in entry '" + this.entryId() + "'.");
        }

        var builder = new ResearchStageBuilder();
        configuration.accept(builder);
        if (!builder.hasRequirements()) {
            throw new IllegalStateException("Stage '" + id + "' in entry '" + this.entryId() + "' has no requirements. "
                    + "Add at least one craft, obtain, use, read, fact, action, value or consume requirement.");
        }

        var stagePrefix = this.context().entry() + ".stage." + id;
        var titleKey = stagePrefix + ".title";
        this.add(titleKey, builder.title() != null ? builder.title() : humanize(id));
        var display = ResearchDisplayModel.create().withTitle(titleKey);
        if (builder.description() != null) {
            var descriptionKey = stagePrefix + ".description";
            this.add(descriptionKey, builder.description());
            display.withDescription(descriptionKey);
        }
        if (builder.icon() != null) {
            display.withIcon(builder.icon());
        }

        var actionFacts = new ArrayList<ActionFact>();
        for (var action : builder.actionFacts()) {
            var actionTitleKey = stagePrefix + ".action." + action.id() + ".title";
            this.add(actionTitleKey, action.title());
            actionFacts.add(new ActionFact(action.id(),
                    ResearchDisplayModel.create().withTitle(actionTitleKey).withAction(action.action())));
        }

        this.researchStages.add(new StageRequest(stageRef,
                builder.crafted(), builder.acquired(), builder.used(), builder.viewed(), builder.facts(), builder.values(),
                actionFacts, display, builder.completion(), builder.rewards()));
        this.currentStage = stageRef;
    }

    /**
     * Places a page that shows the requirements of the stage that comes next at this point of the entry.
     * The stage is the first stage declared after this page, or the last stage if this page comes after all stages.
     * The page is never gated by stages, so it stays visible and shows the completed requirements with check marks.
     * Its title and text are the built-in translations, see {@link #scholarshipPage(String, String, String)} for custom ones.
     * <p>
     * Declaring this page switches the requirements display of the entry to {@link ScholarshipDisplay#PAGES}, unless it was set explicitly.
     * Requires a stage to be declared in this entry.
     *
     * @param id the page id, e.g. {@code next}
     */
    protected BookScholarshipPageModel scholarshipPage(String id) {
        return this.addScholarshipPage(this.page(id, () -> BookScholarshipPageModel.create()
                .withTitle(Gui.SCHOLARSHIP_TITLE)
                .withText(Gui.SCHOLARSHIP_PAGE_TEXT)));
    }

    /**
     * Like {@link #scholarshipPage(String)}, but with a custom literal English title and text for this page.
     * The texts are written to the language file under the page's keys.
     *
     * @param id    the page id, e.g. {@code next}
     * @param title the literal English title of the page
     * @param text  the literal English text of the page
     */
    protected BookScholarshipPageModel scholarshipPage(String id, String title, String text) {
        var page = this.page(id, () -> BookScholarshipPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle(title);
        this.pageText(text);
        return this.addScholarshipPage(page);
    }

    private BookScholarshipPageModel addScholarshipPage(BookScholarshipPageModel page) {
        this.scholarshipPages.add(new PendingScholarshipPage(page, this.researchStages.size()));
        this.scholarshipPageUsed = true;
        return page;
    }

    /**
     * Sets where the requirements of this entry are shown, overriding the book default.
     * Only has an effect for entries with stages.
     */
    protected void scholarshipDisplay(ScholarshipDisplay scholarshipDisplay) {
        this.entry.withScholarshipDisplay(scholarshipDisplay);
    }

    private BookConditionModel<?> stageGate(BookConditionModel<?> condition) {
        var gate = this.condition().researchStageCompleted(
                this.condition().entryResearchNode(this.entry), this.currentStage);
        if (condition == null || condition instanceof BookNoneConditionModel) {
            return gate;
        }
        return this.and(condition, gate);
    }

    private void gatePage(BookPageModel<?> page) {
        if (this.currentStage == null || page instanceof BookScholarshipPageModel) {
            return;
        }
        page.withCondition(this.stageGate(page.getCondition()));
    }

    private void resetResearch() {
        this.researchStages.clear();
        this.scholarshipPages.clear();
        this.currentStage = null;
        this.scholarshipPageUsed = false;
    }

    private void finishResearch() {
        if (this.researchStages.isEmpty()) {
            if (!this.scholarshipPages.isEmpty()) {
                throw new IllegalStateException("Entry '" + this.entryId() + "' has a requirements page, but no stage. "
                        + "Declare a stage with this.stage(...).");
            }
            return;
        }

        if (this.entry.hasResearch()) {
            throw new IllegalStateException("Entry '" + this.entryId() + "' already has a research, set with BookEntryModel.withResearch. "
                    + "Declare researches with this.stage(...) in EntryProvider, or use BookEntryModel.withResearch for a hand-authored node, not both.");
        }

        var lastIndex = this.researchStages.size() - 1;
        this.condition().addEntryResearch(new EntryResearchRequest(this.entry.getId(), List.copyOf(this.researchStages)));
        this.entry.withResearch(this.condition().entryResearchNode(this.entry), this.researchStages.get(lastIndex).ref());

        for (var pending : this.scholarshipPages) {
            var next = this.researchStages.get(Math.min(pending.nextStageIndex(), lastIndex));
            pending.page().withStage(next.ref());
        }

        if (this.scholarshipPageUsed && this.entry.getScholarshipDisplay() == null) {
            this.entry.withScholarshipDisplay(ScholarshipDisplay.PAGES);
        }
    }

    private static String humanize(String id) {
        var text = id.replace('_', ' ');
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private record PendingScholarshipPage(BookScholarshipPageModel page, int nextStageIndex) {}

    /**
     * Ensures present tab ids are unique within the entry and that every tab anchor refers to a page of the entry.
     */
    private void validateTabs() {
        var pageIds = this.entry.getPages().stream()
                .map(BookPageModel::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        var tabIds = new HashSet<String>();
        for (var tab : this.entry.getTabs()) {
            //inline tabs may omit the id, tabs written as files are checked for an id in BookProvider
            var tabId = tab.getId();
            if (tabId != null && !tabId.isEmpty() && !tabIds.add(tabId)) {
                throw new IllegalStateException("Duplicate tab id '" + tab.getId() + "' in entry '" + this.entryId() + "'.");
            }

            if (tab.getAnchor() != null && !pageIds.contains(tab.getAnchor())) {
                throw new IllegalStateException("Tab '" + tab.getId() + "' in entry '" + this.entryId() + "' is anchored to page '"
                        + tab.getAnchor() + "', but the entry has no page with that id.");
            }
        }
    }

    protected <T extends BookPageModel<?>> T add(T page) {
        this.gatePage(page);
        this.entry.withPage(page);
        return page;
    }

    protected List<BookPageModel<?>> add(List<BookPageModel<?>> pages) {
        pages.forEach(this::gatePage);
        this.entry.withPages(pages);
        return pages;
    }

    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     * <p>
     * This overload resolves a location from {@link #entryId()} via the parent category's
     * {@code layout()} helper.
     * <p>
     * If no explicit layout position exists yet, the entry starts at {@link Vec2#ZERO}, which allows later
     * post-generation layout mutation via {@code this.layout().entry(model)...}.
     */
    public BookEntryModel generate() {
        return this.generate(this.parent.layout().getOrDefault(this.entryId(), Vec2.ZERO));
    }

    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     */
    public BookEntryModel generate(String location) {
        if (location.length() == 1)
            return this.generate(location.charAt(0));
        return this.generate(this.parent.entryMap().get(location));
    }


    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     */
    public BookEntryModel generate(char location) {
        return this.generate(this.parent.entryMap().get(location));
    }

    /**
     * Call this in your CategoryProvider to get the entry.
     * Will automatically add the entry to the parent category.
     */
    public BookEntryModel generate(Vec2 location) {
        this.context().entry(this.entryId());

        var entry = BookEntryModel.create(
                        this.modLoc(this.context().categoryId() + "/" + this.context().entryId()),
                        this.context().entryName()
                )
                .withDescription(this.context().entryDescription());

        this.add(this.context().entryName(), this.entryName());
        this.add(this.context().entryDescription(), this.entryDescription());
        entry.withIcon(this.entryIcon());
        entry.withLocation(location);
        entry.withEntryBackground(this.entryBackground());

        this.entry = this.additionalSetup(entry);

        this.resetResearch();
        this.generatePages();
        this.finishResearch();
        this.validateTabs();

        this.parent.add(this.entry);
        return this.entry;
    }

    /**
     * Implement this and in it generate, and .add() (or .page()) your pages.
     * Context already is set to this entry.
     */
    protected abstract void generatePages();

    /**
     * Implement this and modify the entry as needed for additional config.
     * Pages should not be added here, instead call .add() in generatePages().
     * Context already is set to this entry.
     */
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        return entry;
    }

    /**
     * Implement this and return the entry name in the main language.
     */
    protected abstract String entryName();

    /**
     * Implement this and return the entry description in the main language.
     */
    protected String entryDescription() {
        return "";
    }

    /**
     * Implement this and return the GUI texture used for the entry background.
     */
    protected abstract GuiSprite entryBackground();

    /**
     * Implement this and return the desired icon for the entry.
     */
    protected abstract BookIconModel entryIcon();

    /**
     * Implement this and return the desired id for the entry.
     */

    protected abstract String entryId();

}

