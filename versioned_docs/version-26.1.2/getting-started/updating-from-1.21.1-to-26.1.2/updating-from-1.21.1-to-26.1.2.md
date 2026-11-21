---
sidebar_position: 6
title: Updating from 1.21.1 to 26.1.2
---

# Updating from 1.21.1 to 26.1.2

This upgrade has three separate migration areas:

- [Codec and content type changes](./codecs)
- [Theme and styling changes](./theme)
- [Research system and datagen](./research-datagen)

Most mods need at least two of them:

- If you only changed book appearance, textures, colors, or layout, go to [Theme and styling changes](./theme).
- If you maintain custom Modonomicon integrations in code or raw JSON content, go to [Codec and content type changes](./codecs).
- If you use unlock conditions or want to author research content, go to [Research system and datagen](./research-datagen).

:::warning Mod loaded condition

`modonomicon:mod_loaded` used as regular `condition` no longer prevents content from being loaded, it only hides it. Move it to `load_condition` (datagen: `withLoadCondition(...)`) to remove content if the mod is not loaded. See [Load Conditions](../../basics/unlock-conditions/load-conditions).

Custom conditions overriding `BookCondition#testOnLoad()` need to override `supportsLoadTime()` and `testOnLoad(BookLoadConditionContext)` instead.

:::

## Renamed identifiers

The research and scholarship refactor renamed several identifiers. Existing books and code need the new names. The table lists the renames of this version. Saved player data is read with the old keys where noted.

### JSON and data

| Previously | Now | Notes |
|------------|-----|-------|
| entry key `progression` | `research` | The entry↔research link, see [Entry Research](../../basics/research/entry-research) |
| `requirements_display` | `scholarship_display` | Inside the `research` object of an entry, see [Scholarship Display](../../basics/scholarship/display) |
| page type `stage_requirements` | `scholarship` | See [Scholarship Page](../../basics/page-types/scholarship-page) |
| stage key `rewards` | `effects` | See [Effects](../../basics/research/effects) |
| display key `category` | `group` | See [Research Groups](../../basics/research/groups) |
| research data folder `categories` | `groups` | `groups.json` in the research bundle |
| built-in group `modonomicon:rewards` | `modonomicon:effects` | |
| generated folder `entry_progression` | `entry_research` | Generated research paths |
| player state `rewarded_stages` | `granted_effect_stages` | Old saves are still read, so their effects are not granted again |

### Language keys

| Previously | Now |
|------------|-----|
| `modonomicon.gui.requirements.*` | `modonomicon.gui.scholarship.*` |
| `research_category.*` | `research_group.*` |
| `action.receive`, `action.receive_again` | `action.take`, `action.take_again` |
| `action.record` | `action.perform_titled` |
| `reward.*` | `effect.*` |

### Textures

| Previously | Now |
|------------|-----|
| `research_icons/` | `scholarship/icons/` |
| `requirement_categories/` (`rewards.png`) | `scholarship/groups/` (`effects.png`) |
| `requirement_row_line`, `requirement_separator`, `requirement_check` | `scholarship_row_line`, `scholarship_separator`, `scholarship_check` |
| `buttons/stage/complete_button_*` | `buttons/scholarship/complete_button_*` |

### Datagen API

| Previously | Now |
|------------|-----|
| `this.ingress()` | `this.hook()` |
| `ResearchIngress`, `ResearchIngressHelper`, `*Ingress` | `ResearchHookBuilder`, `ResearchHookHelper`, `*HookBuilder` |
| `ResearchCategoryModel` | `ResearchGroupModel` |
| `ProgressionStageBuilder` | `ResearchStageBuilder` |
| `BookEntryProgression` | `BookEntryResearch` |
| `withProgression`, `hasProgression`, `getProgression*` | `withResearch`, `hasResearch`, `getResearch*` |
| `ResearchStageBuilder.reward` / `ResearchStageSpec.withReward` | `effect` / `withEffect` |
| `requirementsPage` | `scholarshipPage` |
| `requirementsDisplay`, `RequirementsDisplay` | `scholarshipDisplay`, `ScholarshipDisplay` |
| `withDefaultRequirementsDisplay` | `withDefaultScholarshipDisplay` |
| `ResearchDisplayModel.withCategory` | `withGroup` |
| `BookStageRequirementsPage` | `BookScholarshipPage` |
| `ConditionHelper` research methods (`entryViewedOnce`, `markerUntil*`, `crafted`, `acquired`, ...) | `ResearchHelper`, available as `this.research()` in a book provider |

### Client classes

Client classes of the requirement block moved to `client.gui.book.entry.scholarship` and the names changed accordingly: `RequirementBlock`, `RequirementRow`, `RequirementIcon` and `RequirementContext` are now `ScholarshipBlock`, `ScholarshipRow`, `ScholarshipIcon` and `ScholarshipContext`. The presenter registries are `Scholarship*PresenterRegistry`, see [Custom Presenters](../../basics/scholarship/custom-presenters).