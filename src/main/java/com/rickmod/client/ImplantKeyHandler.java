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

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ImplantKeyHandler {

    public static final KeyMapping KEY_PLASMA_ARM = new KeyMapping("key.rickmod.plasma_arm", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.rickmod");
    public static final KeyMapping KEY_HACK_EYE = new KeyMapping("key.rickmod.hack_eye", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.rickmod");
    public static final KeyMapping KEY_PHASE_DASH = new KeyMapping("key.rickmod.phase_dash", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.rickmod");

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_PLASMA_ARM);
        event.register(KEY_HACK_EYE);
        event.register(KEY_PHASE_DASH);
    }

    @Mod.EventBusSubscriber(modid = RickMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) return;

            while (KEY_PLASMA_ARM.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new UseImplantAbilityPacket(0));
            }
            while (KEY_HACK_EYE.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new UseImplantAbilityPacket(1));
            }
            while (KEY_PHASE_DASH.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new UseImplantAbilityPacket(2));
            }
        }
    }
}
