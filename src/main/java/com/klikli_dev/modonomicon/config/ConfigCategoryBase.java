/*
 * LGPL-3-0
 *
 * Copyright (C) 2021 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.config;


import com.klikli_dev.modonomicon.config.value.ICachedValue;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCategoryBase implements IConfigCache {
    public IConfigCache parent;
    public ForgeConfigSpec.Builder builder;

    public ConfigCategoryBase(IConfigCache parent, ForgeConfigSpec.Builder builder) {
        this.parent = parent;
        this.builder = builder;
    }

    @Override
    public void cache(ICachedValue value) {
        this.parent.cache(value);
    }

    @Override
    public void clear() {
        this.parent.clear();
    }
}
