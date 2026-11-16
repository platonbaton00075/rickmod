package com.rickmod.network;

import com.rickmod.implant.ImplantEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ImplantAbilityPacket {
    private final int abilityId;

    public ImplantAbilityPacket(int abilityId) {
        this.abilityId = abilityId;
    }

    public static void encode(ImplantAbilityPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.abilityId);
    }

    public static ImplantAbilityPacket decode(FriendlyByteBuf buf) {
        return new ImplantAbilityPacket(buf.readInt());
    }

    public static void handle(ImplantAbilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ImplantEvents.triggerActiveAbility(player, msg.abilityId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
