// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.network;

import com.klikli_dev.modonomicon.networking.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class NeoMessageWrapper implements CustomPacketPayload {

    private final Message message;

    public NeoMessageWrapper(Message message) {
        this.message = message;
    }

    public NeoMessageWrapper(RegistryFriendlyByteBuf buffer, Function<RegistryFriendlyByteBuf, Message> messageFactory) {
        this(messageFactory.apply(buffer));
    }



    @Override
    public void write(RegistryFriendlyByteBuf pBuffer) {
        this.message.encode(pBuffer);
    }

    public Message message(){
        return this.message;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return this.message.type();
    }
}
