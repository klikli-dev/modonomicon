---
sidebar_position: 12
---

# Entry Progression

An entry progression is a staged research progression that belongs to one entry. Each stage lists the requirements the player must meet, and the content after a stage stays hidden until that stage is complete. The requirements are shown in a requirements block, on the first page, on a trailing page, or on pages you place yourself.

Entry progressions are built on the [research system](./research.md): every stage is a research node stage, and every requirement is a generated fact or value.

## Stages

A progression has one or more stages, completed in order. Each stage has:

- **requirements**: what must be met, e.g. crafting an item, obtaining an item, reading an entry or completing an action.
- **completion**: automatic (the default, the stage completes as soon as its requirements are met) or manual (the player clicks the Complete button once they hold what the stage consumes, see [Actions, Costs and Effects](./actions.md)).
- **rewards**: effects granted once when the stage completes, see [Rewards](./rewards.md).
- **display**: a title, an optional description and an optional icon, shown in the requirements block.

## Gating

Content is gated by the stages of the progression:

- **Pages** declared after a stage are only visible once that stage is complete. Content before the first stage is never gated.
- **Tabs** anchored to a gated page follow the page, so they are hidden with it.
- **Requirements pages** are never gated. They keep showing the requirements with check marks after a stage is complete.

Entries without any stage are not affected.

## Display modes

Where the requirements are shown is set by the requirements display of the entry:

| Display | Where the requirements block appears |
|---------|--------------------------------------|
| `first_page` | At the bottom of the first page. The default. |
| `trailing_page` | On a page after the last page of the entry. |
| `pages` | On the requirements pages you place between the content. |
| `none` | Nowhere. The stages still gate the content. |

Declaring a requirements page in datagen switches the entry to `pages`, unless you set the display explicitly.

## Stage browsing

Below the requirement rows, the stage line shows the stage that is required. Its arrows let the player look at the stages they have already reached, so a completed stage stays readable.

## Completion of the progression

Once the last stage is complete, the progression is complete. The entry then shows its requirements as done. Child entries that depend on the progression unlock at this point: the generated child condition requires the completion stage of the parent, which is the last stage unless you choose another one (see [Datagen](#datagen)).

## JSON

The entry references its progression node. The node and its stages are generated into the research bundle, so you do not write them by hand:

```json
{
  "id": "modonomicon:progression/scholarship",
  "progression": {
    "node": "modonomicon:generated/demo/nodes/entry_progression/progression_scholarship"
  }
}
```

The node defines the stages with their requirements, completion and rewards:

```json
{
  "id": "modonomicon:generated/demo/nodes/entry_progression/progression_scholarship",
  "required_facts": [
    "modonomicon:generated/demo/facts/entry_viewed_once/progression_scholarship"
  ],
  "stages": [
    {
      "id": "modonomicon:generated/demo/nodes/entry_progression/progression_scholarship/stages/observation",
      "display": {
        "title": { "translate": "book.modonomicon.demo.progression.scholarship.stage.observation.title" }
      },
      "required_facts": [
        "modonomicon:generated/demo/nodes/entry_progression/progression_scholarship/stages/observation/observation"
      ],
      "rewards": [
        { "type": "modonomicon:experience", "amount": 10 }
      ]
    }
  ]
}
```

## Datagen

Declare a progression with `EntryProvider.stage(id, s -> ...)`. Each call creates one stage, in the order it is declared. The builder collects the requirements, completion, rewards and display:

```java
this.stage("observation", s -> s
        .action("observation", "Record your observations",
                ResearchActionModel.learn(ResearchCostModel.create().item(Items.PAPER)))
        .title("Observation")
        .description("Pay one paper to record your observations.")
        .reward(ResearchEffectModel.experience(10)));

this.stage("transcription", s -> s
        .fact(DemoResearch.SCHOLARSHIP_NOTE_STUDIED)
        .title("Transcription")
        .reward(ResearchEffectModel.addValue(DemoResearch.SCHOLARSHIP_INSIGHT, 1))
        .reward(ResearchEffectModel.grantFact(DemoResearch.SCHOLARSHIP_NOTES_READ)));
```

A stage needs at least one requirement and a unique id within the entry. Requirements are:

| Method | Requirement |
|--------|-------------|
| `craft(item \| stack \| tag \| matcher)` | Item crafted |
| `obtain(item \| stack \| tag \| matcher)` | Item in inventory |
| `use(item \| stack \| tag \| matcher, consume)` | Item used in the air, see [item_used](./item-used.md) |
| `read(entry)` | Entry viewed |
| `fact(ref)` | A hand-authored fact |
| `action(id, title, action)` | A fact the player satisfies with an action |
| `value(ref, threshold)` | A value reaching a threshold |

Texts are literal English. The title defaults to the stage id, capitalized. Calling `manual()`, `consume(...)`, `cost(...)` or `manual(cost)` makes the stage manual, see [Costs](./actions.md#costs).

### Requirements pages

`requirementsPage(id)` places a page that shows the requirements of the next stage. It is the stage declared after the page, or the last stage if the page comes after all stages. Use the built-in texts, or give your own:

```java
this.page("intro", () -> BookTextPageModel.create()
        .withTitle(this.context().pageTitle())
        .withText(this.context().pageText()));
this.requirementsPage("next");
this.stage("quickening", s -> s.craft(Items.CLAY).title("Breathing life into clay"));
this.requirementsPage("after", "Stage requirements", "The requirements of the next stage.");
```

`requirementsDisplay(RequirementsDisplay.TRAILING_PAGE)` overrides the display of the entry.

### Low-level alternative

`BookEntryModel.withProgression(node)` attaches a progression node you generated yourself. Use it only if `stage(...)` does not fit, for example for a progression shared by several entries. `withProgression(node, stage)` sets the completion stage that gates the children.

## Examples

The demo entries show each display:

- `progression/first_page_progression`: the default display, requirements on the first page.
- `progression/trailing_page_progression`: requirements on a trailing page, with a value bar.
- `progression/placed_page_progression`: requirements pages placed between the content.
- `progression/none_progression`: no requirements display, gating only.
- `progression/scholarship`: actions, manual completion and rewards, see [Actions, Costs and Effects](./actions.md).

See also [Research Datagen](./datagen.md), [Requirement Categories](./categories.md) and the [Glossary](./glossary.md).
