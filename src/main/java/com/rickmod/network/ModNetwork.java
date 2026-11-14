package com.rickmod.network;

import com.rickmod.RickMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RickMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static final SimpleChannel INSTANCE = CHANNEL;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.messageBuilder(PacketSelectDimension.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketSelectDimension::decode)
                .encoder(PacketSelectDimension::encode)
                .consumerMainThread(PacketSelectDimension::handle)
                .add();

        CHANNEL.messageBuilder(PacketSelectDestination.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketSelectDestination::decode)
                .encoder(PacketSelectDestination::encode)
                .consumerMainThread(PacketSelectDestination::handle)
                .add();

        CHANNEL.messageBuilder(PacketDeleteWaypoint.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketDeleteWaypoint::decode)
                .encoder(PacketDeleteWaypoint::encode)
                .consumerMainThread(PacketDeleteWaypoint::handle)
                .add();

        CHANNEL.messageBuilder(UseImplantAbilityPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UseImplantAbilityPacket::decode)
                .encoder(UseImplantAbilityPacket::encode)
                .consumerMainThread(UseImplantAbilityPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncPicklePacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncPicklePacket::decode)
                .encoder(SyncPicklePacket::encode)
                .consumerMainThread(SyncPicklePacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        CHANNEL.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
