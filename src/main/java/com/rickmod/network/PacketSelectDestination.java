package com.rickmod.network;

import com.rickmod.item.PortalGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSelectDestination {
    private final InteractionHand hand;
    private final String dimId;
    private final BlockPos pos;
    private final String name;

    public PacketSelectDestination(InteractionHand hand, String dimId, BlockPos pos, String name) {
        this.hand = hand;
        this.dimId = dimId;
        this.pos = pos;
        this.name = name;
    }

    public static void encode(PacketSelectDestination msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
        buf.writeUtf(msg.dimId);
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.name);
    }

    public static PacketSelectDestination decode(FriendlyByteBuf buf) {
        return new PacketSelectDestination(buf.readEnum(InteractionHand.class), buf.readUtf(), buf.readBlockPos(), buf.readUtf());
    }

    public static void handle(PacketSelectDestination msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            ItemStack stack = player.getItemInHand(msg.hand);
            if (stack.getItem() instanceof PortalGunItem) {
                PortalGunItem.setTargetDestination(stack, msg.dimId, msg.pos, msg.name);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
