package com.rickmod.client.renderer;

import com.rickmod.RickMod;
import com.rickmod.entity.GromflomiteSoldierEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class GromflomiteRenderer extends HumanoidMobRenderer<GromflomiteSoldierEntity, HumanoidModel<GromflomiteSoldierEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RickMod.MOD_ID, "textures/entity/gromflomite_soldier.png");

    public GromflomiteRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(GromflomiteSoldierEntity entity) {
        return TEXTURE;
    }
}
