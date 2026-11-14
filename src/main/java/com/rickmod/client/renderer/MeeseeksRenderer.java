package com.rickmod.client.renderer;

import com.rickmod.RickMod;
import com.rickmod.entity.MeeseeksEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MeeseeksRenderer extends HumanoidMobRenderer<MeeseeksEntity, HumanoidModel<MeeseeksEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RickMod.MOD_ID, "textures/entity/meeseeks.png");

    public MeeseeksRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MeeseeksEntity entity) {
        return TEXTURE;
    }
}
