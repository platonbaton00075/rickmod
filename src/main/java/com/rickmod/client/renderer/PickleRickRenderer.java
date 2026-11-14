package com.rickmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public class PickleRickRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation("rickmod", "textures/entity/pickle_rick.png");
    private static ModelPart root;

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -14.0F, -3.0F, 6.0F, 14.0F, 6.0F)
                .texOffs(24, 0).addBox(-3.5F, -11.0F, -2.0F, 1.0F, 2.0F, 2.0F)
                .texOffs(24, 4).addBox(2.5F, -7.0F, -1.0F, 1.0F, 2.0F, 2.0F)
                .texOffs(24, 8).addBox(-1.5F, -13.0F, 2.5F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        part.addOrReplaceChild("exoskeleton", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 10.0F, 8.0F)
                .texOffs(32, 20).addBox(-5.0F, -3.0F, -2.0F, 2.0F, 3.0F, 4.0F)
                .texOffs(32, 20).addBox(3.0F, -3.0F, -2.0F, 2.0F, 3.0F, 4.0F)
                .texOffs(0, 38).addBox(-6.0F, -14.0F, -1.0F, 2.0F, 4.0F, 5.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }

    private static ModelPart getRoot() {
        if (root == null) {
            root = createBodyLayer().bakeRoot();
        }
        return root;
    }

    public static void render(Player player, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float bodyRot = Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        ModelPart model = getRoot();
        ModelPart body = model.getChild("body");
        ModelPart exo = model.getChild("exoskeleton");

        boolean hasExo = player.getItemBySlot(EquipmentSlot.CHEST).getDescriptionId().contains("rat_exoskeleton");
        exo.visible = hasExo;

        float walkSpeed = player.walkAnimation.speed(partialTicks);
        float walkPos = player.walkAnimation.position(partialTicks);
        if (!hasExo && walkSpeed > 0.05F) {
            body.zRot = Mth.cos(walkPos * 1.2F) * 0.25F * walkSpeed;
        } else {
            body.zRot = 0.0F;
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        body.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        if (hasExo) {
            exo.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }
}
