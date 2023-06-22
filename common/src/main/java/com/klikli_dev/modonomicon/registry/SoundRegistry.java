/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.registry;

import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    //TODO: make mod loader agnostic
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Modonomicon.MOD_ID);

    public static final RegistryObject<SoundEvent> TURN_PAGE = SOUNDS.register("turn_page", () -> loadSoundEvent("turn_page"));

    /**
     * Creates the sound event object for the given sound event name, as specified in sounds.json Automatically appends
     * MODID.
     *
     * @param name the sound event name without domain.
     * @return the sound event.
     */
    private static SoundEvent loadSoundEvent(String name) {
        ResourceLocation location = new ResourceLocation(Modonomicon.MOD_ID, name);
        return SoundEvent.createVariableRangeEvent(location);
    }
}
