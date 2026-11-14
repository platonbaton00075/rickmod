package com.rickmod.network;

import com.rickmod.pickle.PickleStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPicklePacket {
    private final int entityId;
    private final boolean isPickle;

    public SyncPicklePacket(int entityId, boolean isPickle) {
        this.entityId = entityId;
        this.isPickle = isPickle;
    }

    public static void encode(SyncPicklePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.isPickle);
    }

    public static SyncPicklePacket decode(FriendlyByteBuf buf) {
        return new SyncPicklePacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(SyncPicklePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(msg.entityId);
                if (entity instanceof Player player) {
                    player.getPersistentData().putBoolean(PickleStateManager.TAG_PICKLE, msg.isPickle);
                    player.refreshDimensions();
                }
            }
        }));
        ctx.get().setPacketHandled(true);
    }
}
