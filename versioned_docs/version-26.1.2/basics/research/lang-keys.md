---
sidebar_position: 18
---

# Lang Key Reference

Modonomicon generates the translation keys of entry research, actions and research elements from the ids you author. You only write the text. This page lists the keys of the book and the research data, so you can find or override them in a language file. The texts of the scholarship block are listed in [Scholarship Lang Keys](../scholarship/lang-keys.md).

Texts you give in datagen as literal English are written to the language file of the book provider under the generated key. Keys you give yourself, such as `withTitle("my.key")`, are used as they are.

## Book keys

Book keys start with `book.<book namespace>.` followed by the entry path, with `/` replaced by `.`.

| Key | Source |
|-----|--------|
| `book.<ns>.<entry>.name` | Entry name |
| `book.<ns>.<entry>.description` | Entry description |
| `book.<ns>.<entry>.<page>.title` and `.text` | Page title and text, where `<page>` is the page id |
| `book.<ns>.<entry>.stage.<stage>.title` | Stage title, `Next: title` in the scholarship block |
| `book.<ns>.<entry>.stage.<stage>.description` | Stage description, if set |
| `book.<ns>.<entry>.stage.<stage>.action.<action>.title` | Title of an action of a stage |

Example, from the notes entry of the demo book, where `<ns>` is `modonomicon` and the entry is `scholarship/notes`:

```
book.modonomicon.demo.scholarship.notes.stage.observation.title
book.modonomicon.demo.scholarship.notes.stage.observation.action.observation.title
book.modonomicon.demo.scholarship.notes.transcription_take.title
```

The scholarship page uses the built-in texts `modonomicon.gui.scholarship.page.text` unless you give your own title and text, which then use the page keys above.

Tabs use the keys returned by the entry's `tabTitleKey()` and `tabTextKey()`, see [Entry Tabs](../structure/entry-tabs.md).

## Research keys

Research keys follow the pattern `<prefix>.<namespace>.<path>`, where `/` in the path becomes `.`. The prefix names the kind of element:

| Prefix | Used for | Example |
|--------|----------|---------|
| `research_fact.` | Title of a fact row, when the display has no title | `research_fact.modonomicon.demo.research.note_studied` |
| `research_value.` | Title of a value row | `research_value.modonomicon.demo.insight` |
| `research_node.` | Name of a node in condition tooltips | `research_node.modonomicon.demo.crafting_stick` |
| `research_stage.` | Name of a stage in condition tooltips | `research_stage.modonomicon.demo.stages_demo_stage_1` |
| `research_group.` | Title of a research group, with `.description` for its description | `research_group.modonomicon.items` |
| `research_toast.` | Toast titles and descriptions | `research_toast.modonomicon.stick_crafted.title` |
| `tag.item.` | Name of an item tag in scholarship rows | `tag.item.minecraft.logs` |

The code that creates these keys is shared between datagen and runtime. In datagen, `researchNodeName(ref, "Name")` and `researchStageName(ref, "Name")` write the `research_node.` and `research_stage.` keys. `ResearchProviderBase` offers `researchNodeDescriptionId(ref)` and `researchStageDescriptionId(ref)` to get the keys without writing them.

A row title is looked up in this order: the display title, then the `research_fact.` or `research_value.` key, then the title of the hook, then the path of the id.

### Item tags

An item tag row is named by `tag.item.<namespace>.<path>`, the translation key used by NeoForge and Fabric for item tags. If the language file has no such key, the row shows `#<namespace>:<path>`. Add the key to show a friendlier name:

```
"tag.item.minecraft.logs": "Logs"
```

## Key authoring summary

| You write | Key written |
|-----------|-------------|
| `stage("id", s -> s.title("T"))` | `book.<ns>.<entry>.stage.id.title` |
| `s.action("id", "T", ...)` | `book.<ns>.<entry>.stage.<stage>.action.id.title` |
| `researchNodeName(node, "N")` | `research_node.<ns>.<path>` |
| `researchStageName(stage, "N")` | `research_stage.<ns>.<path>` |
| `group.withTitle("key")` | Your key, used as given |
| `display.withTitle("key")` | Your key, used as given |

See [Display Data](./display.md) for the display parts and [Research Groups](./groups.md) for group keys.
