package com.rickmod.client;

import com.rickmod.RickMod;
import com.rickmod.client.gui.PortalGunScreen;
import com.rickmod.client.renderer.CitizenRenderer;
import com.rickmod.client.renderer.GromflomiteRenderer;
import com.rickmod.client.renderer.PhoenixPodBlockEntityRenderer;
import com.rickmod.init.ModBlockEntities;
import com.rickmod.init.ModBlocks;
import com.rickmod.init.ModEntities;
import com.rickmod.item.PortalGunItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static final KeyMapping OPEN_PORTAL_GUI = new KeyMapping(
            "key.rickmod.open_portal_gui",
            GLFW.GLFW_KEY_G,
            "key.categories.rickmod"
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.PHOENIX_POD.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.PORTAL_CRYSTAL_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MEGA_LEAVES.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_PORTAL_GUI);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PORTAL.get(), PortalEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.RICK_CITIZEN.get(), ctx -> new CitizenRenderer<>(ctx, "rick_citizen.png", 0.5F));
        event.registerEntityRenderer(ModEntities.MORTY_CITIZEN.get(), ctx -> new CitizenRenderer<>(ctx, "morty_citizen.png", 0.45F));
        event.registerEntityRenderer(ModEntities.MEESEEKS.get(), ctx -> new CitizenRenderer<>(ctx, "meeseeks.png", 0.5F));
        event.registerEntityRenderer(ModEntities.GROMFLOMITE_SOLDIER.get(), GromflomiteRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PHOENIX_POD_BE.get(), PhoenixPodBlockEntityRenderer::new);
    }

    @Mod.EventBusSubscriber(modid = RickMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null && OPEN_PORTAL_GUI.consumeClick()) {
                ItemStack main = mc.player.getMainHandItem();
                ItemStack off = mc.player.getOffhandItem();
                if (main.getItem() instanceof PortalGunItem) {
                    mc.setScreen(new PortalGunScreen(InteractionHand.MAIN_HAND, main));
                } else if (off.getItem() instanceof PortalGunItem) {
                    mc.setScreen(new PortalGunScreen(InteractionHand.OFF_HAND, off));
                }
            }
        }
    }
}
