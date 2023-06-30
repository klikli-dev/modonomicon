package com.klikli_dev.modonomicon.api.datagen;

import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.*;
import net.minecraft.resources.ResourceLocation;

public class ConditionHelper {
    public BookAdvancementConditionModel advancement(ResourceLocation advancementId) {
        return BookAdvancementConditionModel.builder().withAdvancementId(advancementId).build();
    }

    public BookAdvancementConditionModel.Builder advancementBuilder(ResourceLocation advancementId) {
        return BookAdvancementConditionModel.builder().withAdvancementId(advancementId);
    }

    public BookEntryReadConditionModel entryRead(ResourceLocation entryId) {
        return BookEntryReadConditionModel.builder().withEntry(entryId).build();
    }

    public BookEntryReadConditionModel.Builder entryReadBuilder(ResourceLocation entryId) {
        return BookEntryReadConditionModel.builder().withEntry(entryId);
    }

    public BookEntryReadConditionModel entryRead(BookEntryModel entry) {
        return BookEntryReadConditionModel.builder().withEntry(entry.getId()).build();
    }
    public BookEntryReadConditionModel.Builder entryReadBuilder(BookEntryModel entry) {
        return BookEntryReadConditionModel.builder().withEntry(entry.getId());
    }

    public BookAndConditionModel and(BookConditionModel... children) {
        return BookAndConditionModel.builder().withChildren(children).build();
    }

    public BookAndConditionModel.Builder andBuilder(BookConditionModel... children) {
        return BookAndConditionModel.builder().withChildren(children);
    }

    public BookOrConditionModel or(BookConditionModel... children) {
        return BookOrConditionModel.builder().withChildren(children).build();
    }

    public BookOrConditionModel.Builder orBuilder(BookConditionModel... children) {
        return BookOrConditionModel.builder().withChildren(children);
    }
}
