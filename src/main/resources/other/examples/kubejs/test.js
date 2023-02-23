/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


ModonomiconEvents.updateUnlockedContent(event => {
    console.info(event.getUnlockedContent().length);
})

ModonomiconEvents.categoryUnlocked(event => {
    console.info(event.getCategory().getId());
})

ModonomiconEvents.entryUnlocked(event => {
    console.info(event.getEntry().getId());
})

ModonomiconEvents.entryRead(event => {
    console.info(event.getEntry().getId());
})