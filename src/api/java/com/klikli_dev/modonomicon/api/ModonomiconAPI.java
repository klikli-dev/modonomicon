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

package com.klikli_dev.modonomicon.api;

import com.klikli_dev.modonomicon.api.stub.ModonomiconAPIStub;
import net.minecraftforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;

public interface ModonomiconAPI {

    String ID = "modonomicon";
    String Name = "Modonomicon";

    static ModonomiconAPI get() {
        return Helper.lazyInstance.get();
    }

    /**
     * False if a real API instance is provided
     */
    boolean isStub();

    class Helper {
        private static final Lazy<ModonomiconAPI> lazyInstance = Lazy.concurrentOf(() -> {
            try {
                return (ModonomiconAPI) Class.forName("com.klikli_dev.modonomicon.apiimpl.ModonomiconAPIImpl").getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                LogManager.getLogger().warn("Could not find ModonomiconAPI, using stub.");
                return ModonomiconAPIStub.get();
            }
        });
    }

}
