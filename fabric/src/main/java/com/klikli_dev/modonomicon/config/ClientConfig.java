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

public class ClientConfig {

    public static PropertyMirror<Boolean> enableSmoothZoom = PropertyMirror.create(ConfigTypes.BOOLEAN);
    public static PropertyMirror<Boolean> storeLastOpenPageWhenClosingEntry = PropertyMirror.create(ConfigTypes.BOOLEAN);

    private static final ConfigTree CONFIG = ConfigTree.builder()
            .fork("qol")
            .withComment("Quality of Life Settings")
            .beginValue("enableSmoothZoom", ConfigTypes.BOOLEAN, true)
            .withComment("Enable smooth zoom in book categories")
            .finishValue(enableSmoothZoom::mirror)
            .beginValue("storeLastOpenPageWhenClosingEntry", ConfigTypes.BOOLEAN, false)
            .withComment("Enable keeping the last open page stored when closing an entry. " +
                    "Regardless of this setting it will be stored when closing the entire book with Esc.")
            .finishValue(storeLastOpenPageWhenClosingEntry::mirror)
            .finishBranch()
            .build();

    public static void init() {
        var serializer = new JanksonValueSerializer(false);
        var path = FabricLoader.getInstance().getConfigDir().resolve(Modonomicon.MOD_ID + ".json5");

        try (var s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
            FiberSerialization.serialize(CONFIG, s, serializer);
        } catch (IOException ignored) {
        }

        try (var s = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
            FiberSerialization.deserialize(CONFIG, s, serializer);
        } catch (IOException | ValueDeserializationException e) {
            Modonomicon.LOG.error("Error loading config", e);
        }
    }
}
