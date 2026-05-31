<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Provider API Design

Date: 2026-05-31
Branch: `feat/research-system/main`

## Summary

This slice restructures research datagen to follow the same top-level provider/subprovider pattern used by the book datagen API.

The goal is to move research authoring away from a single standalone `ResearchDataProvider` plus one flat `DemoResearchData.populate(...)` function and toward a reusable API with:

- a top-level research provider/orchestrator
- a research subprovider extension seam
- a shared research provider base
- an opinionated single-research subprovider base
- platform registration helpers matching the existing book datagen setup

This slice is structural only. It does not change runtime research semantics, generated resource shapes, or validation rules.

## Goals

- Mirror the book datagen API at the provider/subprovider layer.
- Separate research datagen orchestration from authored research content.
- Move demo research content onto a reusable subprovider pattern.
- Keep emitted research data fully explicit and identical in meaning.
- Keep platform datagen entrypoints thin.

## Non-Goals

- Do not add book-side research glue in this slice.
- Do not add a research group/provider hierarchy below the subprovider layer yet.
- Do not add merge/patch or addon semantics for research data in this slice.
- Do not change runtime research loading or validation.
- Do not redesign `ResearchDataBuilder` into a broader DSL.

## Problem Statement

The book datagen API already has a clear structure:

- `BookProvider` writes output
- `BookSubProvider` defines the extension seam
- `SingleBookSubProvider` provides the normal ergonomic base
- platform wrapper classes keep entrypoint registration thin

Research datagen currently lacks that same structure.

At the moment:

- `ResearchDataProvider` both orchestrates writing and acts as the top-level content entrypoint
- `DemoResearchData.populate(...)` is a single flat content function
- platform datagen entrypoints instantiate the provider directly

That makes research datagen harder to extend in the same style as the book API.

## Target Architecture

### Top-Level Provider

Add `ResearchProvider` as the research equivalent of `BookProvider`.

It owns:

- `PackOutput`
- `CompletableFuture<HolderLookup.Provider>`
- `modId`
- `List<ResearchSubProvider>`
- a map of authored research bundles keyed by root id

It should:

1. ask each subprovider to generate one research bundle
2. reject duplicate bundle ids
3. write the generated `facts.json`, `nodes.json`, `hooks.json`, and `advancement_hooks.json` files for each bundle

`ResearchProvider` must not author demo content directly.

### Subprovider Seam

Add `ResearchSubProvider` as the research equivalent of `BookSubProvider`.

Its job is to hand one complete authored research bundle to the top-level provider.

The intended shape is a subprovider that emits:

- a root id such as `modonomicon:demo`
- a populated `ResearchDataBuilder`

### Shared Base

Add `ResearchProviderBase` as the research equivalent of the shared datagen helper base.

Unlike `ModonomiconProviderBase`, this base should stay narrow.

It should own only what research-side authoring actually needs:

- `modId`
- registry access
- namespace/path helpers
- helper methods that construct typed refs through the current `ResearchDataBuilder`

It should not pull in book language, macros, or condition helpers.

### Opinionated Base

Add `SingleResearchSubProvider` as the research equivalent of `SingleBookSubProvider`.

This should be the normal ergonomic base for most research authoring.

It should:

- hold a single research id/path
- create the active `ResearchDataBuilder`
- set the builder on the base helper methods
- call `generateResearch()`
- hand the finished bundle to the top-level consumer

This gives research datagen the same normal entry style books already use.

## Data Shape Rule

This refactor changes structure, not output semantics.

The emitted research resources must remain:

- explicit fact definitions
- explicit node definitions
- explicit entry-viewed hook definitions
- explicit advancement hook definitions

No hidden generation or additional grouping semantics may be introduced.

## Bundle Identity Rule

The top-level provider should treat each subprovider as responsible for one explicit research bundle id.

For this slice, duplicate bundle ids should fail clearly rather than merge implicitly.

That matches the current explicit-authoring posture and avoids sneaking merge semantics into the API.

## Platform Registration

Add platform helper classes matching the book-side pattern:

- `FabricResearchProvider`
- `ForgeResearchProvider`
- `NeoResearchProvider`

These should be thin constructors/helpers only.

They should not contain authored research content.

## Demo Content Migration

Replace the flat `DemoResearchData.populate(...)` helper with a real research subprovider such as `DemoResearch`.

That class should:

- extend `SingleResearchSubProvider`
- populate the current demo facts, nodes, hooks, and advancement hooks
- use the existing typed-ref/builder helpers

The content itself should remain the same.

## Package Layout

The API/infrastructure should live under:

- `common/.../api/datagen/research/`

Authored demo content should live under:

- `common/.../datagen/research/`

Platform registration wrappers should live beside the existing platform-specific datagen API wrappers.

## Validation and Verification

Runtime validation remains unchanged and authoritative.

Manual verification for this slice should confirm:

1. common/fabric/neo compile
2. Fabric and Neo datagen succeed
3. generated research JSON remains semantically unchanged
4. platform datagen entrypoints now use the new research provider wrapper flow
5. the working tree stays scoped to the structural refactor

## Completion Criteria

This slice is complete when:

- research datagen has a top-level `ResearchProvider`
- research datagen has a `ResearchSubProvider` seam
- research datagen has a `ResearchProviderBase`
- research datagen has a `SingleResearchSubProvider`
- platform-specific research provider wrappers exist
- demo research content has moved from a flat helper to a real subprovider
- `ResearchDataProvider` is no longer the registered top-level entrypoint
- generated research output remains explicitly authored and semantically unchanged
