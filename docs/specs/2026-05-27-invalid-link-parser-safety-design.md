# Invalid link parser safety design

## Summary

Modonomicon's lenient invalid-link mode should never leave book data in a broken or non-serializable state. Invalid markdown or link render failures must affect only user-facing behavior:

- When `allowOpenBooksWithInvalidLinks = false`, the book should not open and should use the existing error screen.
- When `allowOpenBooksWithInvalidLinks = true`, the book should open, the page should still render, and the bad link should render as broken and non-clickable.

In both modes, the book must still finish build and prerender without leaving nulls or codec-hostile state behind. Multiplayer sync must never fail because a book contains invalid links.

## Problem statement

Current behavior mixes rendered markdown state into the synced book model. That is risky in multiplayer because prerendering can happen before `sync_book_data` is encoded.

Observed symptoms:

1. Markdown prerender logs a recoverable link parsing failure.
2. Later, `sync_book_data` encoding fails with a `NullPointerException` inside string serialization.

The likely cause is that prerender replaces original `BookTextHolder` fields with `RenderedBookTextHolder` instances. Those rendered holders are fine for UI use, but they are not safe as persisted/networked model state. In particular, codec-based serialization can observe null backing string data on rendered holders even though the original source text still exists indirectly.

## Goals

- Invalid markdown/link render failures do not permanently break the book model.
- Invalid links do not cause packet encoding failures.
- Strict mode still blocks book opening through the existing error-screen path.
- Lenient mode still opens the book and renders broken links as non-clickable.
- The behavior applies to any markdown render-time link parsing/render failure, not only invalid Modonomicon internal links.

## Non-goals

- Redesigning the entire markdown renderer.
- Changing the JSON schema for books.
- Making invalid links silently succeed.

## Current code areas

- `common/.../client/gui/book/markdown/BookLinkRenderer.java`
  - Catches exceptions and already supports lenient fallback rendering for invalid links.
- `common/.../book/Book.java`
- `common/.../book/BookCategory.java`
- `common/.../book/page/BookTextPage.java`
- other page types that replace text holders during prerender
- `common/.../book/BookTextHolder.java`
- `common/.../book/RenderedBookTextHolder.java`
- `common/.../networking/SyncBookDataMessage.java`

## Design

### 1. Separate model state from rendered UI state

Rendered markdown output is UI cache, not authoritative book data.

The design must ensure that prerender does not mutate networked model fields into a representation that codecs cannot safely serialize. Original source text/components must remain the canonical serialized state for books, categories, entries, and pages.

Acceptable implementations include:

- storing rendered markdown in separate transient cache fields, or
- ensuring any rendered-holder wrapper always serializes back to the original valid source data even when used through codec getters.

The important rule is that `sync_book_data` only ever sees safe, non-null serializable values.

### 2. Preserve strict vs lenient open behavior

Render-time failures must still be recorded so the existing error-screen flow can block opening when `allowOpenBooksWithInvalidLinks = false`.

Behavioral contract:

- strict mode (`false`)
  - record the render failure as a book error
  - book data still completes build/prerender in a safe state
  - opening the book shows the existing error screen instead of the content UI
- lenient mode (`true`)
  - log the render failure
  - do not treat it as open-blocking
  - keep the page renderable with a broken/non-clickable link fallback

This means "book cannot open" and "book data became invalid" are separate outcomes. Only the first is allowed in strict mode.

### 3. Broken-link rendering contract

For any markdown render-time link parsing/render failure:

- preserve surrounding page rendering
- render the link text with broken/error styling
- attach hover text explaining the link is invalid
- do not attach a click event

This should remain local to rendering and must not mutate the book into a partially built state.

### 4. Error recording

Currently lenient handling in `BookLinkRenderer` logs but does not necessarily feed the same open-blocking error path used by the error screen.

The implementation should make error recording explicit:

- render failures are always captured in a stable error-recording path
- strict mode consults that path to block opening
- lenient mode ignores those recorded errors for opening purposes, while still exposing logs/tooltips

This keeps diagnostics available without coupling them to model corruption.

### 5. Multiplayer safety

Server-side rebuild/prerender followed by `sync_book_data` must be safe even when a book contains invalid links.

Required invariants after build/prerender:

- no null text payloads introduced by prerender
- no null pages or null page-local text holders introduced by prerender
- no mutation that makes codec-based serialization depend on renderer-only state

If prerender caches are transmitted at all, they must encode identically to original source state. Prefer not transmitting them.

## Recommended implementation shape

1. Audit all prerender sites that replace `BookTextHolder` fields with `RenderedBookTextHolder`.
2. Change those sites so rendered output is stored separately from the canonical serialized holder, or make serialization of rendered holders always use original source data even through codec getters.
3. Update link-render failure handling so failures are recorded for the error-screen flow.
4. Preserve lenient fallback UI rendering for broken links.
5. Verify that server-side rebuild plus network sync never serializes null text data.

## Validation plan

### Functional validation

Use a book with an intentionally invalid markdown link or anchor.

#### Strict mode

- Set `allowOpenBooksWithInvalidLinks = false`
- Rebuild/prerender the book
- Confirm the book remains built and syncable
- Attempt to open the book
- Confirm the existing error screen appears

#### Lenient mode

- Set `allowOpenBooksWithInvalidLinks = true`
- Rebuild/prerender the book
- Confirm the book opens normally
- Confirm the bad link renders visibly broken and is non-clickable
- Confirm surrounding page content still renders

### Multiplayer validation

- Trigger a server-side path that rebuilds and syncs books
- Confirm `SyncBookDataMessage.encode` does not throw
- Confirm client receives synced data successfully
- Confirm book open behavior matches strict/lenient rules on the client

### Regression validation

- Validate pages with normal links still render and remain clickable
- Validate books without markdown errors still open normally
- Validate runtime book-content rebuild paths remain safe

## Risks and mitigations

- Risk: fixing only `BookLinkRenderer` leaves the serialization hazard untouched.
  - Mitigation: treat rendered-holder/network safety as the primary fix area.
- Risk: strict mode could accidentally revert to hard build failure.
  - Mitigation: explicitly keep model completion separate from open blocking.
- Risk: multiple page types have the same prerender mutation pattern.
  - Mitigation: audit all prerender replacements, not just `BookTextPage`.

## Success criteria

- Invalid links never leave books in a broken or unbuilt state.
- No null codec data is produced by prerender.
- Strict mode blocks opening through the existing error screen.
- Lenient mode opens the book and renders broken links as non-clickable.
- Multiplayer sync remains stable with invalid links present.
