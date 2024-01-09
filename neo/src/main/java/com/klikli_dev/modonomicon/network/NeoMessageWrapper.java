// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class NeoMessageWrapper implements CustomPacketPayload {

    private final Message message;

    public NeoMessageWrapper(Message message) {
        this.message = message;
    }

    public NeoMessageWrapper(FriendlyByteBuf buffer, Function<FriendlyByteBuf, Message> messageFactory) {
        this(messageFactory.apply(buffer));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        this.message.encode(pBuffer);
    }

    @Override
    public ResourceLocation id() {
        return this.message.getId();
    }

    public Message message(){
        return this.message;
    }
}
