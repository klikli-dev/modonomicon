package com.klikli_dev.modonomicon;

import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class Modonomicon {

    public static final String MOD_ID = ModonomiconAPI.ID;
    public static final String MOD_NAME = "Modonomicon";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {

    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}