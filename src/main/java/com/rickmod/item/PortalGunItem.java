package com.rickmod.item;

import com.rickmod.client.PortalGunItemRenderer;
import com.rickmod.entity.PortalEntity;
import com.rickmod.init.ModEntities;
import com.rickmod.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class PortalGunItem extends Item {

    public enum GunType {
        RICK(100, 0x39FF14, false, "Rick"),
        PRIME(-1, 0x00FF44, true, "Prime"),
        EVIL_MORTY(250, 0xFFD700, true, "EvilMorty");

        public final int maxCharges;
        public final int color;
        public final boolean bypassRestrictions;
        public final String id;

        GunType(int maxCharges, int color, boolean bypassRestrictions, String id) {
            this.maxCharges = maxCharges;
            this.color = color;
            this.bypassRestrictions = bypassRestrictions;
            this.id = id;
        }
    }

    private final GunType gunType;

    public PortalGunItem(GunType gunType, Properties props) {
        super(props);
        this.gunType = gunType;
    }

    public PortalGunItem(Properties props, int maxCharges, int color, boolean bypassRestrictions, String variant) {
        super(props);
        if (maxCharges < 0 || "Prime".equalsIgnoreCase(variant)) {
            this.gunType = GunType.PRIME;
        } else if ("EvilMorty".equalsIgnoreCase(variant) || color == 0xFFD700) {
            this.gunType = GunType.EVIL_MORTY;
        } else {
            this.gunType = GunType.RICK;
        }
    }

    public GunType getGunType() {
        return this.gunType;
    }

    public boolean isInfinite() {
        return this.gunType == GunType.PRIME || this.gunType.maxCharges < 0;
    }

    public int getMaxCharges() {
        return this.gunType.maxCharges;
    }

    public static int getCharges(ItemStack stack) {
        if (stack.getItem() instanceof PortalGunItem gun) {
            if (gun.isInfinite()) return 999999;
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("Charges")) {
                tag.putInt("Charges", gun.getMaxCharges());
            }
            return tag.getInt("Charges");
        }
        return 0;
    }

    public static void setCharges(ItemStack stack, int amount) {
        if (stack.getItem() instanceof PortalGunItem gun && !gun.isInfinite()) {
            int clamped = Math.max(0, Math.min(gun.getMaxCharges(), amount));
            stack.getOrCreateTag().putInt("Charges", clamped);
        }
    }

    public static String getTargetDimension(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains("TargetDim") ? tag.getString("TargetDim") : "rickmod:citadel_of_ricks";
    }

    public static void setTargetDimension(ItemStack stack, String dimId) {
        stack.getOrCreateTag().putString("TargetDim", dimId);
    }

    public static void setTargetDestination(ItemStack stack, String dimId, BlockPos pos, String label) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("TargetDim", dimId);
        tag.putInt("TargetX", pos.getX());
        tag.putInt("TargetY", pos.getY());
        tag.putInt("TargetZ", pos.getZ());
        tag.putBoolean("HasCustomPos", true);
        if (label != null && !label.isEmpty()) {
            tag.putString("TargetLabel", label);
        }
    }

    public static ListTag getWaypoints(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Waypoints", Tag.TAG_LIST)) {
            tag.put("Waypoints", new ListTag());
        }
        return tag.getList("Waypoints", Tag.TAG_COMPOUND);
    }

    public static void deleteWaypoint(ItemStack stack, int index) {
        ListTag list = getWaypoints(stack);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            stack.getOrCreateTag().put("Waypoints", list);
        }
    }

    public static void addWaypoint(ItemStack stack, String name, String dimId, BlockPos pos) {
        ListTag list = getWaypoints(stack);
        if (list.size() >= 16) {
            list.remove(0);
        }
        CompoundTag wp = new CompoundTag();
        wp.putString("Name", name);
        wp.putString("Dim", dimId);
        wp.putInt("X", pos.getX());
        wp.putInt("Y", pos.getY());
        wp.putInt("Z", pos.getZ());
        list.add(wp);
        stack.getOrCreateTag().put("Waypoints", list);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                String dimStr = level.dimension().location().toString();
                BlockPos pos = player.blockPosition();
                String name = "Точка #" + (getWaypoints(stack).size() + 1);
                addWaypoint(stack, name, dimStr, pos);
                player.displayClientMessage(Component.literal("Точка сохранена: " + name + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                        .withStyle(ChatFormatting.GREEN), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (!this.isInfinite() && !player.getAbilities().instabuild) {
                int cur = getCharges(stack);
                if (cur <= 0) {
                    player.displayClientMessage(Component.literal("Нет портальной жидкости! Заправьте пушку колбой.")
                            .withStyle(ChatFormatting.RED), true);
                    return InteractionResultHolder.fail(stack);
                }
                setCharges(stack, cur - 1);
            }

            String targetDimStr = getTargetDimension(stack);
            if ("minecraft:the_nether".equals(targetDimStr) || "minecraft:the_end".equals(targetDimStr)) {
                targetDimStr = "rickmod:citadel_of_ricks";
                setTargetDimension(stack, targetDimStr);
            }

            CompoundTag tag = stack.getOrCreateTag();
            BlockPos targetPos = tag.getBoolean("HasCustomPos")
                    ? new BlockPos(tag.getInt("TargetX"), tag.getInt("TargetY"), tag.getInt("TargetZ"))
                    : new BlockPos(0, 82, 0);

            BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            Vec3 spawnPos = hit.getType() != HitResult.Type.MISS
                    ? hit.getLocation().add(Vec3.atLowerCornerOf(hit.getDirection().getNormal()).scale(0.8))
                    : player.getEyePosition().add(player.getLookAngle().scale(3.5));

            PortalEntity portal = ModEntities.PORTAL.get().create(serverLevel);
            if (portal != null) {
                portal.setPos(spawnPos.x, spawnPos.y - 1.0, spawnPos.z);
                portal.setYRot(player.getYRot());
                portal.setGunType(this.gunType);
                portal.setDestination(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetDimStr)), targetPos);
                serverLevel.addFreshEntity(portal);
                serverLevel.playSound(null, portal.blockPosition(), ModSounds.PORTAL_GUN_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                serverLevel.playSound(null, portal.blockPosition(), ModSounds.PORTAL_OPEN.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (this.isInfinite()) {
            tooltip.add(Component.literal("Заряд: ∞ (Бесконечно)").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.literal("Заряд: " + getCharges(stack) + " / " + this.getMaxCharges()).withStyle(ChatFormatting.AQUA));
        }
        tooltip.add(Component.literal("Цель: " + getTargetDimension(stack)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("[G] Выбор измерения | [Shift+ПКМ] Сохранить точку").withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PortalGunItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new PortalGunItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}
