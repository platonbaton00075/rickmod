package com.rickmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.rickmod.RickMod;
import com.rickmod.network.ModNetwork;
import com.rickmod.network.UseImplantAbilityPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RickMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ImplantKeyHandler {

    public static final KeyMapping PLASMA_ARM_KEY = new KeyMapping(
            "key.rickmod.plasma_arm",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.rickmod"
    );

    public static final KeyMapping HACK_PULSE_KEY = new KeyMapping(
            "key.rickmod.hack_pulse",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.categories.rickmod"
    );

    public static final KeyMapping PHASE_DASH_KEY = new KeyMapping(
            "key.rickmod.phase_dash",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.rickmod"
    );

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(PLASMA_ARM_KEY);
        event.register(HACK_PULSE_KEY);
        event.register(PHASE_DASH_KEY);
    }

    @Mod.EventBusSubscriber(modid = RickMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) return;

            while (PLASMA_ARM_KEY.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new UseImplantAbilityPacket(1));
            }
            while (HACK_PULSE_KEY.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new UseImplantAbilityPacket(2));
            }
            while (PHASE_DASH_KEY.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new UseImplantAbilityPacket(3));
            }
        }
    }
}
