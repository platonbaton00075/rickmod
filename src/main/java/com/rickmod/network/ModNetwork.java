package com.rickmod.network;

import com.rickmod.RickMod;
import com.rickmod.item.PortalGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RickMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SelectDestinationPacket.class, SelectDestinationPacket::encode, SelectDestinationPacket::decode, SelectDestinationPacket::handle);
        CHANNEL.registerMessage(id++, DeleteWaypointPacket.class, DeleteWaypointPacket::encode, DeleteWaypointPacket::decode, DeleteWaypointPacket::handle);
    }

    public static class SelectDestinationPacket {
        private final String dimId;
        private final int waypointIndex;

        public SelectDestinationPacket(String dimId, int waypointIndex) {
            this.dimId = dimId;
            this.waypointIndex = waypointIndex;
        }

        public static void encode(SelectDestinationPacket msg, FriendlyByteBuf buf) {
            buf.writeUtf(msg.dimId);
            buf.writeInt(msg.waypointIndex);
        }

        public static SelectDestinationPacket decode(FriendlyByteBuf buf) {
            return new SelectDestinationPacket(buf.readUtf(), buf.readInt());
        }

        public static void handle(SelectDestinationPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;
                ItemStack stack = player.getMainHandItem();
                if (!(stack.getItem() instanceof PortalGunItem)) stack = player.getOffhandItem();
                if (stack.getItem() instanceof PortalGunItem) {
                    PortalGunItem.setTargetDimension(stack, msg.dimId);
                    stack.getOrCreateTag().putInt("SelectedWp", msg.waypointIndex);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static class DeleteWaypointPacket {
        private final int index;

        public DeleteWaypointPacket(int index) {
            this.index = index;
        }

        public static void encode(DeleteWaypointPacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.index);
        }

        public static DeleteWaypointPacket decode(FriendlyByteBuf buf) {
            return new DeleteWaypointPacket(buf.readInt());
        }

        public static void handle(DeleteWaypointPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;
                ItemStack stack = player.getMainHandItem();
                if (!(stack.getItem() instanceof PortalGunItem)) stack = player.getOffhandItem();
                if (stack.getItem() instanceof PortalGunItem) {
                    CompoundTag tag = stack.getOrCreateTag();
                    ListTag list = tag.getList("Waypoints", Tag.TAG_COMPOUND);
                    if (msg.index >= 0 && msg.index < list.size()) {
                        list.remove(msg.index);
                        tag.put("Waypoints", list);
                        tag.putInt("SelectedWp", -1);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
