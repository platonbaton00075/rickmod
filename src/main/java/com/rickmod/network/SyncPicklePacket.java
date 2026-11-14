package com.rickmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncPicklePacket {
    private final UUID playerUuid;
    private final boolean isPickle;

    public SyncPicklePacket(UUID playerUuid, boolean isPickle) {
        this.playerUuid = playerUuid;
        this.isPickle = isPickle;
    }

    public SyncPicklePacket(FriendlyByteBuf buf) {
        this.playerUuid = buf.readUUID();
        this.isPickle = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.playerUuid);
        buf.writeBoolean(this.isPickle);
    }

    public static void encode(SyncPicklePacket msg, FriendlyByteBuf buf) {
        msg.toBytes(buf);
    }

    public static SyncPicklePacket decode(FriendlyByteBuf buf) {
        return new SyncPicklePacket(buf);
    }

    public static void handle(SyncPicklePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance().level != null) {
                    com.rickmod.pickle.PickleStateManager.setPickle(msg.playerUuid, msg.isPickle);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
