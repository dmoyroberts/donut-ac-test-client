package com.donuttest;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TestAuthorizationPayload(boolean enabled) implements CustomPacketPayload {
    public static final Type<TestAuthorizationPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("donut_ac_test", "authorization"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TestAuthorizationPayload> CODEC =
            StreamCodec.of((buf, value) -> buf.writeBoolean(value.enabled),
                    buf -> new TestAuthorizationPayload(buf.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
