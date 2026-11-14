package com.rickmod.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rickmod.blockentity.PhoenixPodBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.UUID;

public class PhoenixPodBlockEntityRenderer implements BlockEntityRenderer<PhoenixPodBlockEntity> {
    private final PlayerModel<?> playerModel;

    public PhoenixPodBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.playerModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.playerModel.young = false;
    }

    @Override
    public void render(PhoenixPodBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        UUID ownerUUID = be.getOwnerUUID();
        if (ownerUUID == null) return;

        float time = be.getTickCount() + partialTick;
        float bob = Mth.sin(time * 0.08F) * 0.06F;

        // 1. Render floating player clone inside the glass cylinder
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.52D + bob, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(time * 0.03F) * 18.0F));
        poseStack.scale(-0.68F, -0.68F, 0.68F);

        ResourceLocation skin = DefaultPlayerSkin.getDefaultSkin(ownerUUID);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getUUID().equals(ownerUUID)) {
            skin = mc.player.getSkinTextureLocation();
        } else if (!be.getOwnerName().isEmpty()) {
            GameProfile profile = new GameProfile(ownerUUID, be.getOwnerName());
            skin = mc.getSkinManager().getInsecureSkinLocation(profile);
        }

        // Cryo-sleep relaxed pose
        this.playerModel.head.xRot = 0.22F + Mth.sin(time * 0.06F) * 0.04F;
        this.playerModel.head.yRot = 0.0F;
        this.playerModel.hat.copyFrom(this.playerModel.head);

        this.playerModel.leftArm.xRot = -0.35F;
        this.playerModel.leftArm.zRot = -0.18F;
        this.playerModel.rightArm.xRot = -0.35F;
        this.playerModel.rightArm.zRot = 0.18F;

        this.playerModel.leftLeg.xRot = 0.15F;
        this.playerModel.leftLeg.zRot = -0.06F;
        this.playerModel.rightLeg.xRot = -0.10F;
        this.playerModel.rightLeg.zRot = 0.06F;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(skin));
        this.playerModel.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 0.72F, 1.0F, 0.92F, 0.92F);
        poseStack.popPose();

        // 2. Render 4 rotating cyan-green DNA scan rings around the clone
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.35D, 0.5D);
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(
                new ResourceLocation("minecraft", "textures/misc/white.png")));

        for (int r = 0; r < 4; r++) {
            float yOffset = ((time * 0.025F + r * 0.35F) % 1.45F);
            float radius = 0.32F + Mth.sin(time * 0.1F + r) * 0.03F;
            poseStack.pushPose();
            poseStack.translate(0.0F, yOffset, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(time * (2.5F + r * 0.6F)));

            Matrix4f mat = poseStack.last().pose();
            int segments = 16;
            for (int i = 0; i < segments; i++) {
                float a1 = (float) (i * 2.0 * Math.PI / segments);
                float a2 = (float) ((i + 1) * 2.0 * Math.PI / segments);
                float x1 = Mth.cos(a1) * radius;
                float z1 = Mth.sin(a1) * radius;
                float x2 = Mth.cos(a2) * radius;
                float z2 = Mth.sin(a2) * radius;

                lineConsumer.vertex(mat, x1, 0.0F, z1).color(0, 255, 200, 170).uv(0, 0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
                lineConsumer.vertex(mat, x2, 0.0F, z2).color(57, 255, 20, 170).uv(1, 0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
                lineConsumer.vertex(mat, x2, 0.025F, z2).color(57, 255, 20, 170).uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
                lineConsumer.vertex(mat, x1, 0.025F, z1).color(0, 255, 200, 170).uv(0, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
