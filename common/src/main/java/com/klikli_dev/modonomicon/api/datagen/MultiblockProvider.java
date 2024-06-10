/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klikli_dev.modonomicon.api.multiblock.TriPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class MultiblockProvider implements DataProvider {

    protected final PackOutput.PathProvider pathProvider;
    protected String modid;

    protected BiConsumer<ResourceLocation, JsonObject> multiblockConsumer;

    public MultiblockProvider(PackOutput packOutput, String modid) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "modonomicon/multiblocks");
        this.modid = modid;
    }

    protected ResourceLocation modLoc(String name) {
        return ResourceLocation.fromNamespaceAndPath(this.modid, name);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> futures = new ArrayList<>();
        this.multiblockConsumer = (id, recipe) -> {
            if (!set.add(id)) {
                throw new IllegalStateException("Duplicate multiblock " + id);
            } else {
                futures.add(DataProvider.saveStable(pOutput, recipe, this.pathProvider.json(id)));
            }
        };
        this.buildMultiblocks();
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    protected void add(ResourceLocation id, MultiblockBuilder multiblock) {
        this.add(id, multiblock.build());
    }


    protected void add(ResourceLocation id, JsonObject multiblock) {
        this.multiblockConsumer.accept(id, multiblock);
    }

    @Override
    public String getName() {
        return "Multiblocks: " + this.modid;
    }

    public abstract void buildMultiblocks();

    protected abstract class MultiblockBuilder {
        protected JsonObject multiblock;

        public MultiblockBuilder() {
            this.multiblock = new JsonObject();
        }

        public JsonObject build() {
            return this.build(true);
        }

        public JsonObject build(boolean displayGroundLayer) {
            return this.build(displayGroundLayer, 1);
        }

        public abstract JsonObject build(boolean displayGroundLayer, int groundLayerPadding);
    }

    protected class DenseMultiblockBuilder extends MultiblockBuilder {
        protected JsonObject multiblock;

        public DenseMultiblockBuilder() {
            this.multiblock = new JsonObject();
            this.multiblock.addProperty("type", "modonomicon:dense");
            this.multiblock.add("pattern", new JsonArray());
            this.multiblock.add("mapping", new JsonObject());
        }

        private List<String> createPattern(String... rows) {
            List<String> pattern = new ArrayList<>();
            for (String row : rows) {
                pattern.add(row.replace(" ", "_"));
            }
            return pattern;
        }

        /**
         * Creates a new layer from a list of strings representing rows of blocks.
         * Does NOT replace spaces with underscores, so likely you'll want the layer(String...) method instead.
         */
        public DenseMultiblockBuilder layer(List<String> rows) {
            var pattern = this.multiblock.getAsJsonArray("pattern");
            var layer = new JsonArray();
            for (String row : rows)
                layer.add(row);
            pattern.add(layer);
            return this;
        }

        /**
         * Creates a new layer from a string array, each row representing a row of blocks in the layer.
         * Replaces spaces (which would be mapped to AIR) with underscores (which will be mapped to ANY).
         */
        public DenseMultiblockBuilder layer(String... rows) {
            return this.layer(this.createPattern(rows));
        }

        /**
         * Creates a new mapping from a character to a JSON representation of a modonomicon state matcher representing a block.
         */
        private DenseMultiblockBuilder map(String key, JsonObject stateMatcher) {
            var mapping = this.multiblock.getAsJsonObject("mapping");
            mapping.add(key, stateMatcher);
            return this;
        }

        /**
         * Creates a block matcher that will match any block.
         *
         * @param c the character
         */
        public DenseMultiblockBuilder any(char c) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:any");

            return this.map(String.valueOf(c), json);
        }

        /**
         * Creates a block matcher that will match the predicate with the given id.
         * Predicates are registered via {@link com.klikli_dev.modonomicon.data.LoaderRegistry#registerPredicate(ResourceLocation, TriPredicate)}
         *
         * @param c the character
         * @param predicateId the id of the predicate to match
         * @param countsTowardsTotalBlocks whether this block counts towards the total number of blocks in the multiblock, if false behaves like the air matcher.
         * @param display the block to display in previews
         */
        public DenseMultiblockBuilder predicate(char c, ResourceLocation predicateId, boolean countsTowardsTotalBlocks, Supplier<? extends Block> display) {
            return this.predicate(c, predicateId, countsTowardsTotalBlocks, display, "");
        }

        /**
         * Creates a block matcher that will match the predicate with the given id.
         * Predicates are registered via {@link com.klikli_dev.modonomicon.data.LoaderRegistry#registerPredicate(ResourceLocation, TriPredicate)}
         *
         * @param c the character
         * @param predicateId the id of the predicate to match
         * @param countsTowardsTotalBlocks whether this block counts towards the total number of blocks in the multiblock, if false behaves like the air matcher.
         * @param display the block to display in previews
         * @param displayState a state string in the minecraft format, e.g. [facing=north]. "" will display the default state.
         */
        public DenseMultiblockBuilder predicate(char c, ResourceLocation predicateId, boolean countsTowardsTotalBlocks, Supplier<? extends Block> display, String displayState) {
            JsonObject json = new JsonObject();

            json.addProperty("type", "modonomicon:predicate");
            json.addProperty("predicate", predicateId.toString());
            json.addProperty("counts_towards_total_blocks", countsTowardsTotalBlocks);
            json.addProperty("display", BuiltInRegistries.BLOCK.getKey(display.get()) + displayState);

            return this.map(String.valueOf(c), json);
        }


        /**
         * Creates a block matcher that will match the given block.
         *
         * @param c the character
         * @param b the block to match
         */
        public DenseMultiblockBuilder block(char c, Supplier<? extends Block> b) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:block");
            json.addProperty("block", BuiltInRegistries.BLOCK.getKey(b.get()).toString());

            return this.map(String.valueOf(c), json);
        }

        /**
         * Creates a block matcher with a display block that is different from the block to match.
         *
         * @param c            the character
         * @param b            the block to match
         * @param display      the block to display in previews
         * @param displayState a state string in the minecraft format, e.g. [facing=north]. "" will display the default state.
         */
        public DenseMultiblockBuilder block(char c, Supplier<? extends Block> b, Supplier<? extends Block> display, String displayState) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:block");
            json.addProperty("block", BuiltInRegistries.BLOCK.getKey(b.get()).toString());
            json.addProperty("display", BuiltInRegistries.BLOCK.getKey(display.get()) + displayState);

            return this.map(String.valueOf(c), json);
        }

        /**
         * Creates a blockstate matcher that will match the given blockstate.
         *
         * @param c          the character
         * @param b          the block to match
         * @param matchState a state string in the minecraft format, e.g. [facing=north]. "" will match any state.
         */
        public DenseMultiblockBuilder blockstate(char c, Supplier<? extends Block> b, String matchState) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:blockstate");
            json.addProperty("block", BuiltInRegistries.BLOCK.getKey(b.get()) + matchState);

            return this.map(String.valueOf(c), json);
        }

        /**
         * Creates a blockstate matcher that will match the given blockstate, and displays the default blockstate of the given display block.
         *
         * @param c          the character
         * @param b          the block to match
         * @param matchState a state string in the minecraft format, e.g. [facing=north]. "" will match any state.
         * @param display    the block to display in previews
         */
        public DenseMultiblockBuilder blockstate(char c, Supplier<? extends Block> b, String matchState, Supplier<? extends Block> display) {
            return this.blockstate(c, b, matchState, display, "");
        }

        /**
         * Creates a blockstate matcher that will match the given blockstate, and display the given other blockstate.
         *
         * @param c            the character
         * @param b            the block to match
         * @param matchState   a state string in the minecraft format, e.g. [facing=north]. "" will match any state.
         * @param display      the block to display in previews
         * @param displayState a state string in the minecraft format, e.g. [facing=north]. "" will display the default state.
         */
        public DenseMultiblockBuilder blockstate(char c, Supplier<? extends Block> b, String matchState, Supplier<? extends Block> display, String displayState) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:blockstate");
            json.addProperty("block", BuiltInRegistries.BLOCK.getKey(b.get()) + matchState);
            json.addProperty("display", BuiltInRegistries.BLOCK.getKey(display.get()) + displayState);

            return this.map(String.valueOf(c), json);
        }

        /**
         * Creates a blockstateproperty matcher that will match ONLY the given block state properties.
         *
         * @param c          the character
         * @param b          the block to match
         * @param matchState a state string in the minecraft format, e.g. [facing=north]. "" will match any state.
         */
        public DenseMultiblockBuilder blockstateproperty(char c, Supplier<? extends Block> b, String matchState) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:blockstateproperty");
            json.addProperty("block", BuiltInRegistries.BLOCK.getKey(b.get()) + matchState);

            return this.map(String.valueOf(c), json);
        }

        /**
         * Creates a blockstateproperty matcher that will  match ONLY the given block state properties, and displays the default blockstate of the given display block.
         *
         * @param c          the character
         * @param b          the block to match
         * @param matchState a state string in the minecraft format, e.g. [facing=north]. "" will match any state.
         * @param display    the block to display in previews
         */
        public DenseMultiblockBuilder blockstateproperty(char c, Supplier<? extends Block> b, String matchState, Supplier<? extends Block> display) {
            return this.blockstateproperty(c, b, matchState, display, "");
        }

        /**
         * Creates a blockstateproperty matcher that will match ONLY the given block state properties, and display the given other blockstate.
         * For the differentiation to blockstate, see <a href="https://klikli-dev.github.io/modonomicon/docs/multiblocks/state-matchers/blockstate-property-matcher">...</a>
         *
         * @param c            the character
         * @param b            the block to match
         * @param matchState   a state string in the minecraft format, e.g. [facing=north]. "" will match any state.
         * @param display      the block to display in previews
         * @param displayState a state string in the minecraft format, e.g. [facing=north]. "" will display the default state.
         */
        public DenseMultiblockBuilder blockstateproperty(char c, Supplier<? extends Block> b, String matchState, Supplier<? extends Block> display, String displayState) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:blockstblockstatepropertyate");
            json.addProperty("block", BuiltInRegistries.BLOCK.getKey(b.get()) + matchState);
            json.addProperty("display", BuiltInRegistries.BLOCK.getKey(display.get()) + displayState);

            return this.map(String.valueOf(c), json);
        }

        public DenseMultiblockBuilder display(char c, Supplier<? extends Block> display) {
            return this.display(c, display, "");
        }

        /**
         * @param c            the character
         * @param display      the block to display in previews
         * @param displayState a state string in the minecraft format, e.g. [facing=north]. "" will display the default state.
         */
        public DenseMultiblockBuilder display(char c, Supplier<? extends Block> display, String displayState) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:display");
            json.addProperty("display", BuiltInRegistries.BLOCK.getKey(display.get()) + displayState);

            return this.map(String.valueOf(c), json);
        }

        /**
         * Creates a tag matcher.
         *
         * @param c       the character.
         * @param tag     the tag to match
         * @param display the block to display in previews.
         */
        public DenseMultiblockBuilder tag(char c, TagKey<Block> tag, Supplier<? extends Block> display) {
            return this.tag(c, tag, "", display, "");
        }

        /**
         * Creates a tag matcher.
         *
         * @param c            the character.
         * @param tag          the tag to match.
         * @param matchState   a state string in the minecraft format, e.g. [facing=north]. "" will match any state.
         * @param display      the block to display in previews.
         * @param displayState a state string in the minecraft format, e.g. [facing=north]. "" will display the default state.
         */
        public DenseMultiblockBuilder tag(char c, TagKey<Block> tag, String matchState, Supplier<? extends Block> display, String displayState) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "modonomicon:tag");
            json.addProperty("tag", "#" + tag.location() + matchState);
            json.addProperty("display", BuiltInRegistries.BLOCK.getKey(display.get()) + displayState);

            return this.map(String.valueOf(c), json);
        }

        public JsonObject build() {
            return this.build(true);
        }

        public JsonObject build(boolean displayGroundLayer) {
            return this.build(displayGroundLayer, 1);
        }

        public JsonObject build(boolean displayGroundLayer, int groundLayerPadding) {
            if (displayGroundLayer) {
                //add mapping for the ground layer
                this.display('*', () -> Blocks.BASALT).display('+', () -> Blocks.STONE);

                var pattern = this.multiblock.getAsJsonArray("pattern");

                //build ground layer
                var width = pattern.get(0).getAsJsonArray().get(0).getAsString().length();
                var length = pattern.get(0).getAsJsonArray().size();
                var groundLayer = new JsonArray();
                for (int i = 0; i < length + groundLayerPadding * 2; i++) {
                    var row = new StringBuilder();
                    for (int j = 0; j < width + groundLayerPadding * 2; j++) {
                        //create a checkerboard, alternatively adding "*" and "+" to the row
                        if ((i + j) % 2 == 0)
                            row.append("*");
                        else
                            row.append("+");
                    }
                    groundLayer.add(row.toString());
                }

                //loop through all layers in the pattern, and add groundLayerPadding number of rows at the beginning and the end, and for existing rows, add groundLayerPadding number of "_" to the beginning and the end.
                for (int i = 0; i < pattern.size(); i++) {
                    var layer = pattern.get(i).getAsJsonArray();
                    //add "_" = any matcher to the beginning and the end of each row to account for the padding
                    for (int j = 0; j < layer.size(); j++) {
                        var row = layer.get(j).getAsString();
                        var newRow = new StringBuilder();
                        for (int k = 0; k < groundLayerPadding; k++) {
                            newRow.append("_");
                        }
                        newRow.append(row);
                        for (int k = 0; k < groundLayerPadding; k++) {
                            newRow.append("_");
                        }
                        layer.set(j, new JsonPrimitive(newRow.toString()));
                    }

                    //add empty rows / "_" any match rows to the beginning and the end of the layer to account for the padding
                    var emptyRow = "_".repeat(width + groundLayerPadding * 2);
                    var updatedLayer = new JsonArray();
                    for (int j = 0; j < groundLayerPadding; j++) {
                        updatedLayer.add(new JsonPrimitive(emptyRow));
                    }
                    updatedLayer.addAll(layer);
                    for (int j = 0; j < groundLayerPadding; j++) {
                        updatedLayer.add(new JsonPrimitive(emptyRow));
                    }
                    pattern.set(i, updatedLayer);
                }

                //finally add ground layer to pattern
                pattern.add(groundLayer);
            }

            return this.multiblock;
        }
    }
}
