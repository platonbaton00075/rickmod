package com.rickmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rickmod.RickMod;
import com.rickmod.item.PortalGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PortalGunItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation RICK_MODEL = new ResourceLocation(RickMod.MOD_ID, "models/item/ricks_portal_gun.glb");
    private static final ResourceLocation PRIME_MODEL = new ResourceLocation(RickMod.MOD_ID, "models/item/rick_primes_portal_gun.glb");
    private static final ResourceLocation EVIL_MODEL = new ResourceLocation(RickMod.MOD_ID, "models/item/evil_morty_golden_portal_gun.glb");
    private static final ResourceLocation WHITE_TEX = new ResourceLocation("minecraft", "textures/misc/white.png");

    public PortalGunItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof PortalGunItem gun)) return;

        ResourceLocation modelLoc;
        float defR, defG, defB;
        switch (gun.getGunType()) {
            case PRIME -> {
                modelLoc = PRIME_MODEL;
                defR = 0.1f; defG = 0.95f; defB = 0.3f;
            }
            case EVIL_MORTY -> {
                modelLoc = EVIL_MODEL;
                defR = 0.95f; defG = 0.8f; defB = 0.15f;
            }
            default -> {
                modelLoc = RICK_MODEL;
                defR = 0.85f; defG = 0.88f; defB = 0.9f;
            }
        }

        GlbModelLoader.LoadedModel model = GlbModelLoader.getOrLoad(modelLoc, defR, defG, defB);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        float sizeX = model.maxX - model.minX;
        float sizeY = model.maxY - model.minY;
        float sizeZ = model.maxZ - model.minZ;
        float maxDim = Math.max(sizeX, Math.max(sizeY, sizeZ));
        if (maxDim < 0.001f) maxDim = 1.0f;

        float targetSize = 0.85f;
        if (displayContext == ItemDisplayContext.GUI) {
            targetSize = 0.9f;
            poseStack.mulPose(Axis.XP.rotationDegrees(25f));
            poseStack.mulPose(Axis.YP.rotationDegrees(-45f));
        } else if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            targetSize = 0.75f;
            poseStack.translate(0.0, 0.05, -0.05);
            poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            targetSize = 0.65f;
            poseStack.translate(0.0, 0.05, 0.05);
            poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        } else if (displayContext == ItemDisplayContext.GROUND) {
            targetSize = 0.55f;
            long time = System.currentTimeMillis();
            poseStack.mulPose(Axis.YP.rotationDegrees((time % 3600L) * 0.1f));
        }

        float scale = targetSize / maxDim;
        poseStack.scale(scale, scale, scale);

        float cx = (model.minX + model.maxX) * 0.5f;
        float cy = (model.minY + model.maxY) * 0.5f;
        float cz = (model.minZ + model.maxZ) * 0.5f;
        poseStack.translate(-cx, -cy, -cz);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(WHITE_TEX));
        model.render(poseStack, consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
