package com.rickmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rickmod.entity.PortalEntity;
import com.rickmod.item.PortalGunItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {

    private static final ResourceLocation WHITE_TEX = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    public PortalEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(PortalEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, 1.1, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));

        float age = entity.tickCount + partialTicks;
        float closeScale = entity.getLifeLeft() < 12 ? (entity.getLifeLeft() / 12.0F) : 1.0F;
        float pulse = 1.0F + 0.05F * (float) Math.sin(age * 0.25F);
        poseStack.scale(0.95F * closeScale * pulse, 1.35F * closeScale * pulse, 1.0F);

        PortalGunItem.GunType type = entity.getGunType();
        int r = (type.color >> 16) & 0xFF;
        int g = (type.color >> 8) & 0xFF;
        int b = type.color & 0xFF;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE_TEX));

        renderLayer(poseStack, vc, age * 2.5F, 1.05F, r, g, b, 90, 0.0F);
        renderLayer(poseStack, vc, -age * 4.5F, 0.85F, r, g, b, 190, 0.01F);
        renderLayer(poseStack, vc, age * 7.0F, 0.55F, Math.min(255, r + 80), Math.min(255, g + 80), Math.min(255, b + 80), 235, 0.02F);

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderLayer(PoseStack poseStack, VertexConsumer vc, float rotDeg, float radius, int r, int g, int b, int a, float zOffset) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotDeg));
        Matrix4f mat = poseStack.last().pose();

        int segments = 24;
        for (int i = 0; i < segments; i++) {
            double a1 = (2 * Math.PI * i) / segments;
            double a2 = (2 * Math.PI * (i + 1)) / segments;
            float x1 = (float) Math.cos(a1) * radius;
            float y1 = (float) Math.sin(a1) * radius;
            float x2 = (float) Math.cos(a2) * radius;
            float y2 = (float) Math.sin(a2) * radius;

            vc.vertex(mat, 0, 0, zOffset).color(255, 255, 255, a).uv(0.5F, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            vc.vertex(mat, x1, y1, zOffset).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            vc.vertex(mat, x2, y2, zOffset).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            vc.vertex(mat, 0, 0, zOffset).color(255, 255, 255, a).uv(0.5F, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PortalEntity entity) {
        return WHITE_TEX;
    }
}
