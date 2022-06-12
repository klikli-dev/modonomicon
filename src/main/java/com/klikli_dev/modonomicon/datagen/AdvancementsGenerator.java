/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klikli_dev.modonomicon.Modonomicon;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AdvancementsGenerator implements DataProvider {
    private final DataGenerator generator;
    private final Map<ResourceLocation, Advancement> advancements;

    public AdvancementsGenerator(DataGenerator generator) {
        this.generator = generator;
        this.advancements = new HashMap<>();
    }

    private static Path getPath(Path path, Advancement advancement) {
        ResourceLocation id = advancement.getId();
        return path.resolve("data/" + id.getNamespace() + "/advancements/" + id.getPath() + ".json");
    }

    private static MutableComponent text(String name, String type) {
        return Component.translatable("advancements." + Modonomicon.MODID + "." + name + "." + type);
    }

    public static MutableComponent title(String name) {
        return text(name, "title");
    }

    public static MutableComponent descr(String name) {
        return text(name, "description");
    }

    private void start() {
        Advancement root = this.add(Advancement.Builder.advancement()
//                .display(ItemRegistry.MODONOMICON.get(), title("root"), descr("root"),
//                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, true,
//                        true, false)
                .addCriterion("modonomicon_present",
                        new PlayerTrigger.TriggerInstance(CriteriaTriggers.TICK.getId(), EntityPredicate.Composite.ANY))
                .build(new ResourceLocation(Modonomicon.MODID, Modonomicon.MODID + "/root")));
    }

    private Advancement add(Advancement advancement) {
        if (this.advancements.containsKey(advancement.getId()))
            throw new IllegalStateException("Duplicate advancement " + advancement.getId());
        this.advancements.put(advancement.getId(), advancement);
        return advancement;
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        Path folder = this.generator.getOutputFolder();
        this.start();

        for (Advancement advancement : this.advancements.values()) {
            Path path = getPath(folder, advancement);
            try {
                DataProvider.saveStable(cache, advancement.deconstruct().serializeToJson(), path);
            } catch (IOException exception) {
                Modonomicon.LOGGER.error("Couldn't save advancement {}", path, exception);
            }
        }
    }

    @Override
    public String getName() {
        return "Advancements: " + Modonomicon.MODID;
    }
}
