/*
 * MIT License
 *
 * Copyright 2021 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.klikli_dev.modonomicon.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.registry.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AdvancementsGenerator implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

    private static TranslatableComponent text(String name, String type) {
        return new TranslatableComponent("advancements." + Modonomicon.MODID + "." + name + "." + type);
    }

    public static TranslatableComponent title(String name) {
        return text(name, "title");
    }

    public static TranslatableComponent descr(String name) {
        return text(name, "description");
    }

    private void start() {
        Advancement root = this.add(Advancement.Builder.advancement()
                .display(ItemRegistry.MODONOMICON.get(), title("root"), descr("root"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, true,
                        true, false)
                .addCriterion("modonomicon_present",
                        new TickTrigger.TriggerInstance(EntityPredicate.Composite.ANY))
                .build(new ResourceLocation(Modonomicon.MODID, Modonomicon.MODID + "/root")));
    }

    private Advancement add(Advancement advancement) {
        if (this.advancements.containsKey(advancement.getId()))
            throw new IllegalStateException("Duplicate advancement " + advancement.getId());
        this.advancements.put(advancement.getId(), advancement);
        return advancement;
    }

    @Override
    public void run(HashCache cache) throws IOException {
        Path folder = this.generator.getOutputFolder();
        this.start();

        for (Advancement advancement : this.advancements.values()) {
            Path path = getPath(folder, advancement);
            try {
                DataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path);
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
