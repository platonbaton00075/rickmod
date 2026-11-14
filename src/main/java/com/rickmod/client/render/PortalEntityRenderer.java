package com.rickmod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rickmod.entity.PortalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {
    private static final ResourceLocation WHITE_TEX = new ResourceLocation("textures/misc/white.png");

    public PortalEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(PortalEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.4D, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        float age = entity.tickCount + partialTicks;
        float openScale = Math.min(1.0F, age / 7.0F);
        float closeScale = entity.getLifeLeft() < 12 ? (entity.getLifeLeft() / 12.0F) : 1.0F;
        float baseScale = openScale * closeScale;

        int type = entity.getPortalType();
        int r, g, b;
        float speed1, speed2, pulse;

        if (type == 1) {
            // Rick Prime: Aggressive Acid Green + Ultra-fast Counter-Rotation + High-frequency Pulse
            r = 0; g = 255; b = 75;
            speed1 = 15.0F;
            speed2 = -22.0F;
            pulse = 1.0F + 0.11F * (float) Math.sin(age * 0.65F);
        } else if (type == 2) {
            // Evil Morty: Royal Golden Halo + Orbital Crown Rings
            r = 255; g = 215; b = 0;
            speed1 = 7.5F;
            speed2 = -11.0F;
            pulse = 1.0F + 0.08F * (float) Math.sin(age * 0.35F);
        } else {
            // C-137 Rick: Classic Hypnotic Emerald-Green Spiral
            r = 57; g = 255; b = 20;
            speed1 = 9.0F;
            speed2 = -6.5F;
            pulse = 1.0F + 0.07F * (float) Math.sin(age * 0.3F);
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE_TEX));

        // Layer 1: Outer Energy Halo Aura
        poseStack.pushPose();
        poseStack.scale(baseScale * pulse * 1.38F, baseScale * pulse * 1.85F, 1.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * speed1 * 0.4F));
        drawVortexDisc(poseStack, vc, 32, 0.98F, 0.55F, r, g, b, 55, 165);
        poseStack.popPose();

        // Layer 2: Primary Spiral Ring (Clockwise)
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.015F);
        poseStack.scale(baseScale * pulse * 1.15F, baseScale * pulse * 1.55F, 1.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * speed1));
        drawSpiralBlades(poseStack, vc, 16, 0.95F, r, g, b, 210);
        poseStack.popPose();

        // Layer 3: Counter-Rotating Inner Vortex (Counter-Clockwise)
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.03F);
        poseStack.scale(baseScale * 0.85F, baseScale * 1.15F, 1.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * speed2));
        drawSpiralBlades(poseStack, vc, 12, 0.88F, Math.min(255, r + 95), 255, Math.min(255, b + 95), 235);
        poseStack.popPose();

        // Layer 4: Incandescent White-Hot Dimensional Core
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.045F);
        float corePulse = 0.42F + 0.06F * (float) Math.cos(age * 0.5F);
        poseStack.scale(baseScale * corePulse, baseScale * corePulse * 1.35F, 1.0F);
        drawVortexDisc(poseStack, vc, 24, 0.85F, 0.0F, 240, 255, 240, 255, 255);
        poseStack.popPose();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void drawVortexDisc(PoseStack stack, VertexConsumer vc, int segments, float outerR, float innerR, int r, int g, int b, int outerA, int innerA) {
        Matrix4f mat = stack.last().pose();
        Matrix3f norm = stack.last().normal();
        for (int i = 0; i < segments; i++) {
            float a1 = (float) (i * 2.0 * Math.PI / segments);
            float a2 = (float) ((i + 1) * 2.0 * Math.PI / segments);

            float x1Out = (float) Math.cos(a1) * outerR;
            float y1Out = (float) Math.sin(a1) * outerR;
            float x2Out = (float) Math.cos(a2) * outerR;
            float y2Out = (float) Math.sin(a2) * outerR;

            float x1In = (float) Math.cos(a1) * innerR;
            float y1In = (float) Math.sin(a1) * innerR;
            float x2In = (float) Math.cos(a2) * innerR;
            float y2In = (float) Math.sin(a2) * innerR;

            vertex(vc, mat, norm, x1In, y1In, 0, r, g, b, innerA);
            vertex(vc, mat, norm, x1Out, y1Out, 0, r, g, b, outerA);
            vertex(vc, mat, norm, x2Out, y2Out, 0, r, g, b, outerA);
            vertex(vc, mat, norm, x2In, y2In, 0, r, g, b, innerA);
        }
    }

    private void drawSpiralBlades(PoseStack stack, VertexConsumer vc, int blades, float radius, int r, int g, int b, int alpha) {
        Matrix4f mat = stack.last().pose();
        Matrix3f norm = stack.last().normal();
        for (int i = 0; i < blades; i++) {
            float baseAngle = (float) (i * 2.0 * Math.PI / blades);
            float tipAngle = baseAngle + 0.55F;
            float nextAngle = baseAngle + (float) (Math.PI / blades);

            float x1 = (float) Math.cos(baseAngle) * 0.22F;
            float y1 = (float) Math.sin(baseAngle) * 0.22F;
            float x2 = (float) Math.cos(tipAngle) * radius;
            float y2 = (float) Math.sin(tipAngle) * radius;
            float x3 = (float) Math.cos(tipAngle + 0.25F) * (radius * 0.92F);
            float y3 = (float) Math.sin(tipAngle + 0.25F) * (radius * 0.92F);
            float x4 = (float) Math.cos(nextAngle) * 0.22F;
            float y4 = (float) Math.sin(nextAngle) * 0.22F;

            vertex(vc, mat, norm, x1, y1, 0, 235, 255, 235, alpha);
            vertex(vc, mat, norm, x2, y2, 0, r, g, b, 30);
            vertex(vc, mat, norm, x3, y3, 0, r, g, b, alpha);
            vertex(vc, mat, norm, x4, y4, 0, 235, 255, 235, alpha);
        }
    }

    private void vertex(VertexConsumer vc, Matrix4f mat, Matrix3f norm, float x, float y, float z, int r, int g, int b, int a) {
        vc.vertex(mat, x, y, z)
          .color(r, g, b, a)
          .uv(0.5F, 0.5F)
          .overlayCoords(OverlayTexture.NO_OVERLAY)
          .uv2(15728880)
          .normal(norm, 0, 0, 1)
          .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(PortalEntity entity) {
        return WHITE_TEX;
    }
}
