package com.rickmod.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharacterArmorModel extends HumanoidModel<LivingEntity> {

    public CharacterArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createC137Layer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.55F), 0.0F);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("rick_hair_top", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-4.5F, -10.5F, -4.5F, 9.0F, 3.0F, 9.0F), PartPose.ZERO);
        head.addOrReplaceChild("rick_hair_spikes", CubeListBuilder.create()
                .texOffs(32, 12).addBox(-5.5F, -9.0F, -2.0F, 11.0F, 3.0F, 5.0F), PartPose.ZERO);
        PartDefinition body = root.getChild("body");
        body.addOrReplaceChild("lab_collar", CubeListBuilder.create()
                .texOffs(32, 20).addBox(-4.8F, -0.5F, -3.2F, 9.6F, 4.0F, 6.4F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition createEvilMortyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.6F), 0.0F);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("gold_eyepatch", CubeListBuilder.create()
                .texOffs(32, 0).addBox(0.5F, -5.0F, -4.8F, 3.5F, 3.0F, 1.2F), PartPose.ZERO);
        root.getChild("right_arm").addOrReplaceChild("gold_pauldron_r", CubeListBuilder.create()
                .texOffs(32, 8).addBox(-4.0F, -2.8F, -2.8F, 5.2F, 4.0F, 5.6F), PartPose.ZERO);
        root.getChild("left_arm").addOrReplaceChild("gold_pauldron_l", CubeListBuilder.create()
                .texOffs(32, 8).addBox(-1.2F, -2.8F, -2.8F, 5.2F, 4.0F, 5.6F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition createPrimeLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.65F), 0.0F);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("prime_visor", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-4.6F, -6.0F, -4.9F, 9.2F, 3.2F, 2.0F), PartPose.ZERO);
        PartDefinition body = root.getChild("body");
        body.addOrReplaceChild("prime_reactor_plate", CubeListBuilder.create()
                .texOffs(32, 8).addBox(-3.5F, 1.5F, -3.6F, 7.0F, 6.0F, 1.5F), PartPose.ZERO);
        root.getChild("right_arm").addOrReplaceChild("prime_pad_r", CubeListBuilder.create()
                .texOffs(32, 18).addBox(-4.2F, -3.0F, -3.0F, 5.5F, 4.5F, 6.0F), PartPose.ZERO);
        root.getChild("left_arm").addOrReplaceChild("prime_pad_l", CubeListBuilder.create()
                .texOffs(32, 18).addBox(-1.3F, -3.0F, -3.0F, 5.5F, 4.5F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }
}
