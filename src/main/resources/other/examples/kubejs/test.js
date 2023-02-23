/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


onEvent('modonomicon.update_unlocked_content', event => {
    console.info(event.getUnlockedContent().length);
})

onEvent('modonomicon.category_unlocked', event => {
    console.info(event.getCategory().getId());
})

onEvent('modonomicon.entry_unlocked', event => {
    console.info(event.getEntry().getId());
})

onEvent('modonomicon.entry_read', event => {
    console.info(event.getEntry().getId());
})