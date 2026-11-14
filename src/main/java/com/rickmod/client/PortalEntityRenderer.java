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
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {
    private static final ResourceLocation WHITE_TEX = new ResourceLocation("minecraft", "textures/misc/white.png");

    public PortalEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PortalEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, 1.0, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));

        float age = entity.tickCount + partialTicks;
        float openScale = Mth.clamp(age / 8.0f, 0.05f, 1.0f);
        float pulse = 1.0f + 0.06f * Mth.sin(age * 0.25f);
        poseStack.scale(openScale * pulse, openScale * pulse * 1.35f, openScale);

        PortalGunItem.GunType type = entity.getGunType();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.eyes(WHITE_TEX));

        if (type == PortalGunItem.GunType.PRIME) {
            // Кислотно-зеленый вихрь Рика Прайма: быстрое двойное противовращение
            renderSwirlLayer(poseStack, consumer, age * 14.0f, 0.95f, 0.0f, 1.0f, 0.27f, 0.85f, 7);
            renderSwirlLayer(poseStack, consumer, -age * 20.0f, 0.65f, 0.4f, 1.0f, 0.55f, 0.95f, 5);
            renderCore(poseStack, consumer, 0.35f, 0.85f, 1.0f, 0.85f, 1.0f);
        } else if (type == PortalGunItem.GunType.EVIL_MORTY) {
            // Золотой портал Злого Морти: многослойное гало + орбитальные кольца
            renderSwirlLayer(poseStack, consumer, age * 9.0f, 1.0f, 1.0f, 0.84f, 0.0f, 0.85f, 8);
            renderSwirlLayer(poseStack, consumer, -age * 12.0f, 0.72f, 1.0f, 0.95f, 0.35f, 0.95f, 6);
            renderCore(poseStack, consumer, 0.4f, 1.0f, 0.98f, 0.75f, 1.0f);
        } else {
            // Классический зеленый портал Рика C-137: плавное спиральное вращение
            renderSwirlLayer(poseStack, consumer, age * 8.0f, 0.9f, 0.22f, 1.0f, 0.08f, 0.85f, 6);
            renderSwirlLayer(poseStack, consumer, age * 13.0f, 0.6f, 0.55f, 1.0f, 0.45f, 0.92f, 5);
            renderCore(poseStack, consumer, 0.32f, 0.85f, 1.0f, 0.85f, 1.0f);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderSwirlLayer(PoseStack poseStack, VertexConsumer consumer, float rotDeg,
                                  float radius, float r, float g, float b, float alpha, int arms) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotDeg));
        Matrix4f mat = poseStack.last().pose();

        int segments = 36;
        for (int i = 0; i < segments; i++) {
            float a0 = (float) (2.0 * Math.PI * i / segments);
            float a1 = (float) (2.0 * Math.PI * (i + 1) / segments);

            float wave0 = 0.82f + 0.18f * Mth.sin(a0 * arms);
            float wave1 = 0.82f + 0.18f * Mth.sin(a1 * arms);

            float rOut0 = radius * wave0;
            float rOut1 = radius * wave1;
            float rIn0 = rOut0 * 0.45f;
            float rIn1 = rOut1 * 0.45f;

            float x0Out = Mth.cos(a0) * rOut0, y0Out = Mth.sin(a0) * rOut0;
            float x1Out = Mth.cos(a1) * rOut1, y1Out = Mth.sin(a1) * rOut1;
            float x0In = Mth.cos(a0) * rIn0, y0In = Mth.sin(a0) * rIn0;
            float x1In = Mth.cos(a1) * rIn1, y1In = Mth.sin(a1) * rIn1;

            addQuad(consumer, mat, x0In, y0In, x0Out, y0Out, x1Out, y1Out, x1In, y1In, r, g, b, alpha);
        }
        poseStack.popPose();
    }

    private void renderCore(PoseStack poseStack, VertexConsumer consumer, float radius, float r, float g, float b, float alpha) {
        Matrix4f mat = poseStack.last().pose();
        int segments = 24;
        for (int i = 0; i < segments; i++) {
            float a0 = (float) (2.0 * Math.PI * i / segments);
            float a1 = (float) (2.0 * Math.PI * (i + 1) / segments);
            float x0 = Mth.cos(a0) * radius, y0 = Mth.sin(a0) * radius;
            float x1 = Mth.cos(a1) * radius, y1 = Mth.sin(a1) * radius;

            addQuad(consumer, mat, 0f, 0f, x0, y0, x1, y1, 0f, 0f, r, g, b, alpha);
        }
    }

    private void addQuad(VertexConsumer consumer, Matrix4f mat,
                         float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3,
                         float r, float g, float b, float a) {
        consumer.vertex(mat, x0, y0, 0.01f).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(mat, x1, y1, 0.01f).color(r, g, b, a * 0.6f).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(mat, x2, y2, 0.01f).color(r, g, b, a * 0.6f).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(mat, x3, y3, 0.01f).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(PortalEntity entity) {
        return WHITE_TEX;
    }
}
