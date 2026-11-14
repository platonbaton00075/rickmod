package com.rickmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = "rickmod", value = Dist.CLIENT)
public class ImplantRenderEvents {
    private static final ResourceLocation WHITE_TEX = new ResourceLocation("minecraft", "textures/misc/white.png");

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        float shield = player.getPersistentData().getFloat("rickmod_shield_hp");
        int shieldType = player.getPersistentData().getInt("rickmod_shield_type"); // 1 = Rick Green, 2 = Evil Morty Gold
        if (shield <= 0.1f || shieldType == 0) return;

        PoseStack pose = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        float age = player.tickCount + event.getPartialTick();

        pose.pushPose();
        pose.translate(0.0, 1.0, 0.0);
        pose.mulPose(Axis.YP.rotationDegrees(age * 1.8f));

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE_TEX));
        Matrix4f mat = pose.last().pose();

        int r = shieldType == 2 ? 255 : 57;
        int g = shieldType == 2 ? 215 : 255;
        int b = shieldType == 2 ? 0 : 20;
        int a = (int) (45 + 20 * Math.sin(age * 0.15f));

        float rad = 0.85f;
        float h = 1.05f;
        // 4-sided holographic hex/prism barrier around player
        float[][] corners = {{-rad, -rad}, {rad, -rad}, {rad, rad}, {-rad, rad}};
        for (int i = 0; i < 4; i++) {
            float[] c1 = corners[i];
            float[] c2 = corners[(i + 1) % 4];
            vc.vertex(mat, c1[0], -h, c1[1]).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            vc.vertex(mat, c2[0], -h, c2[1]).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            vc.vertex(mat, c2[0], h, c2[1]).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            vc.vertex(mat, c1[0], h, c1[1]).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        }
        pose.popPose();
    }
}
