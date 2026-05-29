<!--
SPDX-FileCopyrightText: 2026 klikli-dev

SPDX-License-Identifier: MIT
-->

# Research Legacy Advancement Cleanup Design

Date: 2026-05-29
Branch: `feat/research-system/main`

## Summary

This document defines the next research-system slice after the research condition cutover.

The slice is a cleanup-only slice. Its purpose is to remove obsolete legacy advancement-specific runtime and config paths that no longer fit the revised research architecture now that authored advancement progression has moved onto explicit research-backed ingress.

This slice does not add new triggers, compatibility shims, fallback behavior, or a new mechanism for bypassing research. It removes dead legacy paths while keeping the current research-backed advancement flow unchanged.

## Goals

- Remove obsolete advancement-specific legacy networking and client-cache paths.
- Remove advancement-locking config plumbing that belonged to the old advancement condition model.
- Preserve the current research-backed advancement ingress and book visibility behavior.
- Keep failure behavior clear for removed legacy condition ids in authored data.
- Keep the slice narrow, cleanup-oriented, and manually verifiable.

## Non-Goals

- Do not add a new trigger family.
- Do not add compatibility handling for worlds or configs that relied on old advancement-lock skipping.
- Do not add a sanctioned research-skip or bypass mechanism in this slice.
- Do not clean Forge generated leftovers in this slice.
- Do not do translation/lang cleanup in this slice.
- Do not remove book read/unlock persistence or visual-state systems.

## Architectural Constraints From The Revised Design

This slice must continue to respect the revised architecture:

- research is the authoritative durable progression layer
- books are downstream consumers of research for progression-facing visibility
- external advancement events must enter progression through explicit research-owned ingress
- books must not regain a separate advancement-based progression authority
- cleanup must not silently reintroduce legacy fallback semantics

Because of those constraints, the slice should remove old advancement-side infrastructure rather than preserve it as an unused alternate path.

## Slice Boundary

This slice covers exactly these changes:

1. remove old advancement request/send networking that existed for book-side advancement checks
2. remove old client-side advancement cache/data plumbing that supported those checks
3. remove server-config plumbing for disabling advancement locking
4. keep clear validation/load-time rejection for removed legacy progression condition ids

This slice does not change current research resource shapes, hook semantics, or authored progression content.

## Cleanup Targets

## Legacy Advancement Networking And Client Cache

The old advancement-specific client sync path is now obsolete.

This slice should remove the legacy request/send message flow and any associated client-cache path that only existed to support direct advancement-based book condition evaluation.

The intended target state is:

- no advancement-specific client sync message remains registered
- no client-side advancement cache remains for book condition evaluation
- no runtime code still expects a direct book-side advancement query path

If a referenced class or registration turns out to support a still-live non-legacy feature, that usage must be proven explicitly before keeping it. The default expectation for this slice is removal.

## Advancement-Locking Config Plumbing

The old config path for disabling advancement locking belonged to the legacy advancement condition model.

Now that advancement-facing progression is expected to flow through research, this config surface no longer matches the architecture and should be removed.

The intended target state is:

- no common service API exposes advancement-lock disabling
- no platform config helper implements advancement-lock disabling
- no runtime path branches on an advancement-locking toggle

This slice is cleanup only. It does not replace this toggle with a new research-aware bypass feature.

## Explicitly Preserved Behavior

This slice must not change:

- research-backed advancement ingress
- research hook evaluation
- research node unlocking semantics
- book visibility driven by research-backed conditions
- clear rejection of removed legacy condition ids such as `entry_read`, `entry_unlocked`, and `advancement`

If a cleanup candidate is currently needed to preserve one of those behaviors, it is out of scope for this slice and should be deferred rather than partially reworked here.

## Runtime Outcome

After this slice:

- advancement-backed progression remains available only through research-owned ingress
- there is no separate book-side advancement cache/sync path
- there is no config-level advancement-lock skip toggle
- old authored data using removed legacy condition ids should still fail clearly rather than degrade silently

## Validation Requirements

Validate that:

- no advancement-specific request/send packet remains registered on supported platforms
- no common runtime path still references removed advancement cache/network classes
- no config API or platform helper still exposes advancement-lock disabling
- legacy condition-id rejection remains intact for removed progression condition types
- the cleanup does not introduce a second progression authority or silent fallback path

Validation failures should stay explicit and should prefer compile-time or load-time failure over silent legacy preservation.

## Manual Verification

Manual verification for this slice should confirm:

1. the project compiles after the legacy advancement cleanup
2. research-backed advancement progression still works through the current explicit ingress path
3. no removed advancement networking path is still referenced at runtime startup
4. no advancement-locking config option remains exposed through the common/platform config helpers
5. old removed condition ids still fail clearly rather than being accepted or emulated

## Completion Criteria

This slice is complete when:

- the obsolete advancement request/send and client-cache path is removed
- advancement-locking config plumbing is removed across common and platform layers
- current research-backed advancement behavior remains unchanged
- removed legacy progression condition ids still reject clearly
- the slice ships without adding any replacement compatibility or bypass mechanism

## Recommended Next Step After This Slice

Once this cleanup is complete, the next useful follow-up should be either:

1. a separate design for a sanctioned research skip/bypass mechanism for narrowly-defined scenarios
2. a broader legacy-state audit for what parts of read/unlock persistence can eventually move or remain strictly visual

Neither follow-up should be folded into this cleanup slice.
