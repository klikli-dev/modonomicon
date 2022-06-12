/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.stub;


import com.klikli_dev.modonomicon.api.ModonomiconAPI;

public class ModonomiconAPIStub implements ModonomiconAPI {
    private static final ModonomiconAPIStub instance = new ModonomiconAPIStub();

    private ModonomiconAPIStub() {
    }

    public static ModonomiconAPIStub get() {
        return instance;
    }

    @Override
    public boolean isStub() {
        return true;
    }

}
