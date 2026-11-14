package com.rickmod.client;

import com.rickmod.RickMod;
import com.rickmod.client.gui.PortalGunScreen;
import com.rickmod.client.renderer.*;
import com.rickmod.item.PortalGunItem;
import com.rickmod.registry.ModBlockEntities;
import com.rickmod.registry.ModBlocks;
import com.rickmod.registry.ModEntities;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static final KeyMapping OPEN_PORTAL_GUI_KEY = new KeyMapping(
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
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PORTAL.get(), PortalEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.RICK_CITIZEN.get(), ctx -> new CitizenRenderer(ctx, "rick_citizen.png"));
        event.registerEntityRenderer(ModEntities.MORTY_CITIZEN.get(), ctx -> new CitizenRenderer(ctx, "morty_citizen.png"));
        event.registerEntityRenderer(ModEntities.MEESEEKS.get(), ctx -> new CitizenRenderer(ctx, "meeseeks.png"));
        event.registerEntityRenderer(ModEntities.GROMFLOMITE_SOLDIER.get(), GromflomiteRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PHOENIX_POD.get(), PhoenixPodBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_PORTAL_GUI_KEY);
    }

    @Mod.EventBusSubscriber(modid = RickMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END && OPEN_PORTAL_GUI_KEY.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    ItemStack main = mc.player.getMainHandItem();
                    ItemStack off = mc.player.getOffhandItem();
                    if (main.getItem() instanceof PortalGunItem) {
                        mc.setScreen(new PortalGunScreen(main));
                    } else if (off.getItem() instanceof PortalGunItem) {
                        mc.setScreen(new PortalGunScreen(off));
                    }
                }
            }
        }
    }
}
