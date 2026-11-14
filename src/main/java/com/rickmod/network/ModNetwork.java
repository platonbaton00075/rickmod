package com.rickmod.network;

import com.rickmod.RickMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(RickMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, PacketSelectDimension.class,
                PacketSelectDimension::encode, PacketSelectDimension::decode, PacketSelectDimension::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetId++, PacketSelectDestination.class,
                PacketSelectDestination::encode, PacketSelectDestination::decode, PacketSelectDestination::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetId++, PacketDeleteWaypoint.class,
                PacketDeleteWaypoint::encode, PacketDeleteWaypoint::decode, PacketDeleteWaypoint::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetId++, UseImplantAbilityPacket.class,
                UseImplantAbilityPacket::encode, UseImplantAbilityPacket::decode, UseImplantAbilityPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
