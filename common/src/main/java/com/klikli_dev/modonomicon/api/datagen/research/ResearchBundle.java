/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

/**
 * One authored research bundle emitted by a {@link ResearchSubProvider}.
 *
 * @param data the collected authoring data for one bundle root such as {@code modonomicon:demo}
 */
public record ResearchBundle(ResearchDataBuilder data) {
}
