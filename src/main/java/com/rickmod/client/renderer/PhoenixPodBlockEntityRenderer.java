package com.rickmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rickmod.block.PhoenixPodBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class PhoenixPodBlockEntityRenderer implements BlockEntityRenderer<PhoenixPodBlockEntity> {
    private static final ResourceLocation DEFAULT_SKIN = new ResourceLocation("minecraft", "textures/entity/player/wide/steve.png");
    private static final ResourceLocation WHITE_TEX = new ResourceLocation("minecraft", "textures/misc/white.png");
    private final PlayerModel<AbstractClientPlayer> cloneModel;

    public PhoenixPodBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.cloneModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.cloneModel.young = false;
    }

    @Override
    public void render(PhoenixPodBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be.getOwnerUuid() == null) return;

        Minecraft mc = Minecraft.getInstance();
        float time = (be.getLevel() != null ? be.getLevel().getGameTime() : 0) + partialTicks;
        float bob = Mth.sin(time * 0.08F) * 0.05F;

        ResourceLocation skin = DEFAULT_SKIN;
        if (mc.player != null && mc.player.getUUID().equals(be.getOwnerUuid())) {
            skin = mc.player.getSkinTextureLocation();
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 1.55 + bob, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 0.6F));
        poseStack.scale(0.68F, 0.68F, 0.68F);

        cloneModel.head.xRot = 0.25F;
        cloneModel.leftArm.xRot = -0.35F;
        cloneModel.rightArm.xRot = -0.35F;
        cloneModel.leftLeg.xRot = 0.15F;
        cloneModel.rightLeg.xRot = -0.15F;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(skin));
        cloneModel.renderToBuffer(poseStack, vc, 15728880, OverlayTexture.NO_OVERLAY, 0.75F, 1.0F, 0.92F, 0.88F);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.35, 0.5);
        VertexConsumer ringVc = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE_TEX));
        for (int i = 0; i < 4; i++) {
            float ringY = ((time * 0.025F + i * 0.35F) % 1.4F);
            poseStack.pushPose();
            poseStack.translate(0, ringY, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(time * 2.5F + i * 45.0F));
            Matrix4f mat = poseStack.last().pose();
            float r = 0.34F;
            for (int seg = 0; seg < 16; seg++) {
                float a1 = (float) (seg * Math.PI * 2 / 16);
                float a2 = (float) ((seg + 1) * Math.PI * 2 / 16);
                ringVc.vertex(mat, Mth.cos(a1) * r, 0.0F, Mth.sin(a1) * r).color(57, 255, 180, 160).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
                ringVc.vertex(mat, Mth.cos(a2) * r, 0.0F, Mth.sin(a2) * r).color(57, 255, 180, 160).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
                ringVc.vertex(mat, Mth.cos(a2) * r, 0.03F, Mth.sin(a2) * r).color(57, 255, 180, 160).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
                ringVc.vertex(mat, Mth.cos(a1) * r, 0.03F, Mth.sin(a1) * r).color(57, 255, 180, 160).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
