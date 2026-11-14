package com.rickmod.network;

import com.rickmod.pickle.PickleStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncPicklePacket {
    public final int entityId;
    public final UUID playerUuid;
    public final boolean isPickle;

    public SyncPicklePacket(int entityId, boolean isPickle) {
        this.entityId = entityId;
        this.playerUuid = null;
        this.isPickle = isPickle;
    }

    public SyncPicklePacket(UUID playerUuid, boolean isPickle) {
        this.entityId = -1;
        this.playerUuid = playerUuid;
        this.isPickle = isPickle;
    }

    public static void encode(SyncPicklePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.playerUuid != null);
        if (msg.playerUuid != null) {
            buf.writeUUID(msg.playerUuid);
        }
        buf.writeBoolean(msg.isPickle);
    }

    public static SyncPicklePacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        UUID uuid = buf.readBoolean() ? buf.readUUID() : null;
        boolean isPickle = buf.readBoolean();
        return uuid != null ? new SyncPicklePacket(uuid, isPickle) : new SyncPicklePacket(entityId, isPickle);
    }

    public static void handle(SyncPicklePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Player target = null;
                if (msg.entityId >= 0) {
                    Entity entity = mc.level.getEntity(msg.entityId);
                    if (entity instanceof Player p) {
                        target = p;
                    }
                }
                if (target == null && msg.playerUuid != null) {
                    target = mc.level.getPlayerByUUID(msg.playerUuid);
                }
                if (target != null) {
                    PickleStateManager.setPickle(target, msg.isPickle);
                }
            }
        }));
        ctx.get().setPacketHandled(true);
    }
}
