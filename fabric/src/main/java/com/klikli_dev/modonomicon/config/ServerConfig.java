/*
 * SPDX-FileCopyrightText: 2024 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.config;

import com.klikli_dev.modonomicon.Modonomicon;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ServerConfig {

    public static PropertyMirror<Boolean> disableAdvancementLocking = PropertyMirror.create(ConfigTypes.BOOLEAN);

    private static final ConfigTree CONFIG = ConfigTree.builder()
            .fork("unlock")
            .withComment("Unlock Settings")
            .beginValue("disableAdvancementLocking", ConfigTypes.BOOLEAN, false)
            .withComment("If true, advancement-based unlock conditions will always return true, " +
                    "effectively disabling advancement-gated progression in all books. " +
                    "Other unlock conditions (e.g. entry read, mod loaded) are not affected.")
            .finishValue(disableAdvancementLocking::mirror)
            .finishBranch()
            .build();

    public static void init() {
        var serializer = new JanksonValueSerializer(false);
        var path = FabricLoader.getInstance().getConfigDir().resolve(Modonomicon.MOD_ID + "-server.json5");

        try (var s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
            FiberSerialization.serialize(CONFIG, s, serializer);
        } catch (IOException ignored) {
        }

        try (var s = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
            FiberSerialization.deserialize(CONFIG, s, serializer);
        } catch (IOException | ValueDeserializationException e) {
            Modonomicon.LOG.error("Error loading server config", e);
        }
    }
}
