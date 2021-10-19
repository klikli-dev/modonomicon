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

package com.klikli_dev.modonomicon.config;

import com.klikli_dev.modonomicon.config.value.CachedBoolean;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig extends ConfigBase {

    private static final CommonConfig instance = new CommonConfig();

    public final SampleCategory sampleCategory;
    public final ForgeConfigSpec spec;

    private CommonConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.sampleCategory = new SampleCategory(this, builder);
        this.spec = builder.build();
    }

    public static CommonConfig get() {
        return instance;
    }

    public static class SampleCategory extends ConfigCategoryBase {
        public final CachedBoolean sampleBoolean;

        public SampleCategory(IConfigCache parent, ForgeConfigSpec.Builder builder) {
            super(parent, builder);
            builder.comment("Sample Settings").push("sample");
            this.sampleBoolean = CachedBoolean.cache(this,
                    builder.comment("Sample Boolean.")
                            .define("sampleBoolean", true));
            builder.pop();
        }
    }
}
