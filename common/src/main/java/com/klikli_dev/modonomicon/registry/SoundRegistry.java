/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
    public static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(Registries.SOUND_EVENT, Modonomicon.MOD_ID);

    public static final RegistryObject<SoundEvent> TURN_PAGE = SOUNDS.register("turn_page", () -> loadSoundEvent("turn_page"));

    /**
     * Creates the sound event object for the given sound event name, as specified in sounds.json Automatically appends
     * MODID.
     *
     * @param name the sound event name without domain.
     * @return the sound event.
     */
    private static SoundEvent loadSoundEvent(String name) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Modonomicon.MOD_ID, name);
        return SoundEvent.createVariableRangeEvent(location);
    }

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {
    }
}
