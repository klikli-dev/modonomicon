package com.klikli_dev.modonomicon.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryUnlockedConditionModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookTrueConditionModel;
import com.klikli_dev.modonomicon.datagen.book.other.AEntry;
import com.klikli_dev.modonomicon.datagen.book.other.BEntry;
import com.klikli_dev.modonomicon.datagen.book.other.RootEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class OtherCategoryProvider extends CategoryProvider {
    public OtherCategoryProvider(BookProvider parent) {
        super(parent, "other");
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_____________________",
                "__b___a______________",
                "__________r_____x____",
                "_____________________",
                "_____________________"
        };
    }

    @Override
    protected void generateEntries() {
        var rootEntry = new RootEntry(this).generate('r');
        var aEntry = new AEntry(this).generate('a');
        var bEntry = new BEntry(this).generate('b');
        bEntry.withCondition(this.condition().entryRead(rootEntry));

        aEntry.withCondition(
                this.condition().and(
                        BookEntryUnlockedConditionModel.builder().withEntry(bEntry.getId()).build(),
                        BookTrueConditionModel.builder().build()
                )
        );
    }

    @Override
    protected BookCategoryModel generateCategory() {
        this.add(this.context().categoryName(), "Other");
        return BookCategoryModel.create(this.modLoc(this.context().categoryId()), this.context().categoryName())
                .withIcon(Items.FEATHER);
    }
}
