package com.rickmod.client.renderer;

import com.rickmod.RickMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;

public class CitizenRenderer<T extends PathfinderMob> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
    private final ResourceLocation texture;

    public CitizenRenderer(EntityRendererProvider.Context ctx, String textureName, float shadowSize) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), shadowSize);
        this.texture = new ResourceLocation(RickMod.MOD_ID, "textures/entity/" + textureName + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
