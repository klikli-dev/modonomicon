---
sidebar_position: 1
---

# Scholarship

Scholarship is the part of a book page that the player sees and interacts with when research is involved. It draws the research of an entry on the page, and handles the clicks on it: a scholarship block shows the stages and rows of the entry research, and the player clicks a row to perform an action or completes a stage.

Scholarship is the UI and interaction layer. The research data it shows is described in [Research](../research/research.md), and the conditions that unlock content are described in [Research Conditions](../unlock-conditions/research-conditions.md). The [Glossary](../research/glossary.md#condition-research-and-scholarship) explains how the three fit together.

## What scholarship covers

- **Scholarship block**: the box on a page that lists the rows of the current stage. It is drawn at the bottom of a page, and it is also the content of a scholarship page.
- **Rows**: one line per research element of the stage, with an icon, a title, details and a met or not met state. Rows are grouped into [scholarship groups](./groups.md).
- **Click interactions**: a row with an action is clickable, and so is the Complete button of a manual stage. Hovering a row shows its costs.
- **Icons**: the icons and badges of rows and groups, and the textures used when a row has no icon of its own, see [Icons and Textures](./icons-and-textures.md).
- **Presenters**: the client-side code that turns a hook, an effect or a cost into a row. The built-in presenters cover the common cases, and [Custom Presenters](./custom-presenters.md) describes how to add your own.
- **Feedback messages**: short messages that tell the player why an action did not happen, e.g. that it is not available right now.
- **Display modes**: where the block appears on the entry, see [Scholarship Display](./display.md).
- **Scholarship page type**: a page that shows the block, see [Scholarship Page](../page-types/scholarship-page.md).
- **Lang keys**: the built-in texts of the block and its actions, see [Scholarship Lang Keys](./lang-keys.md).

## Action verbs

An action row shows a verb that depends on the kind of action:

| Key | Text | When it is shown |
|-----|------|------------------|
| `modonomicon.gui.scholarship.action.take` | Click to take %s | An action that gives the player something, e.g. a blank research note |
| `modonomicon.gui.scholarship.action.take_again` | Click to take another %s | The same action again, for a repeatable take |
| `modonomicon.gui.scholarship.action.perform` | Click to perform this action | An action without a title |
| `modonomicon.gui.scholarship.action.perform_titled` | Click to perform: %s | An action with a title |
| `modonomicon.gui.scholarship.action.already_held` | You already have this. | A take action when the player already holds what it gives |

Actions are described in [Actions, Costs and Effects](../research/actions.md).

## Terms in the datagen API

- `EntryProvider.scholarshipPage(...)` declares a scholarship page, see [Entry Research](../research/entry-research.md#scholarship-pages).
- `EntryProvider.scholarshipDisplay(...)` sets the display mode of the entry.
- `BookModel.withDefaultScholarshipDisplay(...)` sets the default display mode of the book.
