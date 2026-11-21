---
sidebar_position: 5
---

# Scholarship Lang Keys

The scholarship block uses built-in texts for its rows, costs, effects, stages and actions. You do not write these keys, but you can override them in a language file. The keys of the research data itself are listed in [Lang Keys](../research/lang-keys.md).

## Block

| Key | Text |
|-----|------|
| `modonomicon.gui.scholarship.title` | Requirements |
| `modonomicon.gui.scholarship.next` | Next: %s |
| `modonomicon.gui.scholarship.next_steps` | Next steps |
| `modonomicon.gui.scholarship.stage_line` | Stage %s/%s |
| `modonomicon.gui.scholarship.stage_title` | %s: %s |
| `modonomicon.gui.scholarship.progress` | Progress: %s/%s |
| `modonomicon.gui.scholarship.page.text` | These are the requirements of the next stage of this entry. Completed requirements get a check mark. Once all requirements are met, the content behind this stage is unlocked. |
| `modonomicon.gui.scholarship.met` | ✔ Done |
| `modonomicon.gui.scholarship.not_met` | ✘ Not done yet |
| `modonomicon.gui.scholarship.not_met.hint` | Do this to unlock more of this entry. |
| `modonomicon.gui.scholarship.trigger.special` | Special requirement |

The title of the block is shown as "Requirements" by default. The block is always called scholarship in the keys, and only its visible title is "Requirements".

## Rows

| Key | Text |
|-----|------|
| `modonomicon.gui.scholarship.craft_item` | Craft %s |
| `modonomicon.gui.scholarship.obtain_item` | Obtain %s |
| `modonomicon.gui.scholarship.use_item` | Use %s |
| `modonomicon.gui.scholarship.read_entry` | Read %s |
| `modonomicon.gui.scholarship.not_met.craft` | ✘ Not crafted yet |
| `modonomicon.gui.scholarship.not_met.obtain` | ✘ Not obtained yet |
| `modonomicon.gui.scholarship.not_met.progress` | ✘ %s more to go |
| `modonomicon.gui.scholarship.not_used` | ✘ Not used yet |
| `modonomicon.gui.scholarship.click_to_open` | Click to open |

## Costs

| Key | Text |
|-----|------|
| `modonomicon.gui.scholarship.costs` | Costs: |
| `modonomicon.gui.scholarship.consumed_on_completion` | Consumed on completion |
| `modonomicon.gui.scholarship.cost.item` | %s× %s |
| `modonomicon.gui.scholarship.cost.durability` | %s durability: %s |
| `modonomicon.gui.scholarship.cost.levels` | %s levels |
| `modonomicon.gui.scholarship.cost.not_held` | Not held |
| `modonomicon.gui.scholarship.cost.special` | Special cost |

## Effects

| Key | Text |
|-----|------|
| `modonomicon.gui.scholarship.effect` | Effect: %s |
| `modonomicon.gui.scholarship.effect.pending` | Granted when the stage is completed. |
| `modonomicon.gui.scholarship.effect.experience` | %s experience points |
| `modonomicon.gui.scholarship.effect.levels` | %s levels |
| `modonomicon.gui.scholarship.effect.value` | +%s %s |
| `modonomicon.gui.scholarship.effect.special` | Special effect |

## Stages

| Key | Text |
|-----|------|
| `modonomicon.gui.scholarship.stage.complete` | Complete |
| `modonomicon.gui.scholarship.stage.complete.hint` | Click to complete this stage |
| `modonomicon.gui.scholarship.stage.completing` | Completing... |
| `modonomicon.gui.scholarship.stage.manual` | Requires completion by hand |
| `modonomicon.gui.scholarship.complete` | Research complete |

## Actions

| Key | Text |
|-----|------|
| `modonomicon.gui.scholarship.action.take` | Click to take %s |
| `modonomicon.gui.scholarship.action.take_again` | Click to take another %s |
| `modonomicon.gui.scholarship.action.perform` | Click to perform this action |
| `modonomicon.gui.scholarship.action.perform_titled` | Click to perform: %s |
| `modonomicon.gui.scholarship.action.already_held` | You already have this. |

See [Scholarship](./scholarship.md#action-verbs) for when each verb is shown.

## Feedback

Feedback messages tell the player why an action did not happen.

| Key | Text |
|-----|------|
| `modonomicon.gui.feedback.invalid` | Nothing to do here. |
| `modonomicon.gui.feedback.missing` | Missing: %s |
| `modonomicon.gui.feedback.unavailable` | You cannot do that right now. |

## Custom presenters

A custom presenter that describes its own rows can use its own keys, see [Custom Presenters](./custom-presenters.md).
