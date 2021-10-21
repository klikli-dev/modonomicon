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

package com.klikli_dev.modonomicon.config.value;

import com.klikli_dev.modonomicon.config.IConfigCache;
import net.minecraftforge.common.ForgeConfigSpec;

public class CachedPrimitive<T> implements ICachedValue {
    protected ForgeConfigSpec.ConfigValue<T> configValue;
    protected boolean cacheAvailable;

    protected CachedPrimitive(IConfigCache cache, ForgeConfigSpec.ConfigValue<T> configValue) {
        this.configValue = configValue;
        cache.cache(this);
    }

    public void clear() {
        this.cacheAvailable = false;
    }
}
