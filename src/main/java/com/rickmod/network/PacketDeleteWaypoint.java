package com.rickmod.network;

import com.rickmod.item.PortalGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDeleteWaypoint {
    private final InteractionHand hand;
    private final int index;

    public PacketDeleteWaypoint(InteractionHand hand, int index) {
        this.hand = hand;
        this.index = index;
    }

    public static void encode(PacketDeleteWaypoint msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
        buf.writeInt(msg.index);
    }

    public static PacketDeleteWaypoint decode(FriendlyByteBuf buf) {
        return new PacketDeleteWaypoint(buf.readEnum(InteractionHand.class), buf.readInt());
    }

    public static void handle(PacketDeleteWaypoint msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            ItemStack stack = player.getItemInHand(msg.hand);
            if (stack.getItem() instanceof PortalGunItem) {
                PortalGunItem.deleteWaypoint(stack, msg.index);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
