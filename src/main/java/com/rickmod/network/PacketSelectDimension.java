package com.rickmod.network;

import com.rickmod.item.PortalGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSelectDimension {
    private final InteractionHand hand;
    private final String dimId;
    private final boolean usePos;
    private final int x, y, z;
    private final String label;
    private final int deleteIndex;

    public PacketSelectDimension(InteractionHand hand, String dimId, boolean usePos, int x, int y, int z, String label, int deleteIndex) {
        this.hand = hand;
        this.dimId = dimId;
        this.usePos = usePos;
        this.x = x;
        this.y = y;
        this.z = z;
        this.label = label;
        this.deleteIndex = deleteIndex;
    }

    public static void encode(PacketSelectDimension msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
        buf.writeUtf(msg.dimId);
        buf.writeBoolean(msg.usePos);
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
        buf.writeInt(msg.z);
        buf.writeUtf(msg.label);
        buf.writeInt(msg.deleteIndex);
    }

    public static PacketSelectDimension decode(FriendlyByteBuf buf) {
        return new PacketSelectDimension(
                buf.readEnum(InteractionHand.class),
                buf.readUtf(),
                buf.readBoolean(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readUtf(),
                buf.readInt()
        );
    }

    public static void handle(PacketSelectDimension msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            ItemStack stack = player.getItemInHand(msg.hand);
            if (!(stack.getItem() instanceof PortalGunItem)) return;

            if (msg.deleteIndex >= 0) {
                PortalGunItem.deleteWaypoint(stack, msg.deleteIndex);
            } else if (msg.usePos) {
                PortalGunItem.setTargetDestination(stack, msg.dimId, new BlockPos(msg.x, msg.y, msg.z), msg.label);
            } else {
                PortalGunItem.setTargetDimension(stack, msg.dimId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
