package com.rickmod.client;

import com.rickmod.client.renderer.PickleRickRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rickmod", value = Dist.CLIENT)
public class PickleRenderEvents {
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player.getPersistentData().getBoolean("IsPickleRick")) {
            event.setCanceled(true);
            PickleRickRenderer.render(
                    player,
                    player.getYRot(),
                    event.getPartialTick(),
                    event.getPoseStack(),
                    event.getMultiBufferSource(),
                    event.getPackedLight()
            );
        }
    }
}
