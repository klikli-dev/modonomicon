/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
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

package com.klikli_dev.modonomicon.multiblock;

import com.klikli_dev.modonomicon.api.multiblock.StateMatcher;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public abstract class AbstractStateMatcher implements StateMatcher {

    protected final BlockState displayState;

    public AbstractStateMatcher(BlockState displayState) {
        this.displayState = displayState;
    }

    @Override
    public BlockState getDisplayedState(long ticks) {
        return this.displayState;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(BlockStateParser.serialize(this.displayState));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getStatePredicate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StateMatcher that = (StateMatcher) o;
        return this.getStatePredicate().equals(that.getStatePredicate());
    }
}
