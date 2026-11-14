package com.rickmod.item;

import com.rickmod.entity.PortalEntity;
import com.rickmod.init.ModEntities;
import com.rickmod.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import javax.annotation.Nullable;
import java.util.List;

public class PortalGunItem extends Item {

    public enum GunType {
        RICK(0, 100, 0x39FF14, false, "Rick C-137"),
        PRIME(1, -1, 0x00FF44, true, "Rick Prime"),
        EVIL_MORTY(2, 250, 0xFFD700, true, "Evil Morty");

        public final int id;
        public final int maxCharges;
        public final int color;
        public final boolean bypassRestrictions;
        public final String label;

        GunType(int id, int maxCharges, int color, boolean bypassRestrictions, String label) {
            this.id = id;
            this.maxCharges = maxCharges;
            this.color = color;
            this.bypassRestrictions = bypassRestrictions;
            this.label = label;
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
        } else if ("EvilMorty".equalsIgnoreCase(variant) || maxCharges >= 200) {
            this.gunType = GunType.EVIL_MORTY;
        } else {
            this.gunType = GunType.RICK;
        }
    }

    public GunType getGunType() {
        return gunType;
    }

    public boolean isInfinite() {
        return gunType.maxCharges < 0;
    }

    public int getMaxCharges() {
        return gunType.maxCharges;
    }

    public static int getCharges(ItemStack stack) {
        if (stack.getItem() instanceof PortalGunItem gun) {
            if (gun.isInfinite()) return 999999;
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("Charges")) {
                tag.putInt("Charges", gun.gunType.maxCharges);
            }
            return tag.getInt("Charges");
        }
        return 0;
    }

    public static void setCharges(ItemStack stack, int value) {
        if (stack.getItem() instanceof PortalGunItem gun && !gun.isInfinite()) {
            int clamped = Math.max(0, Math.min(gun.gunType.maxCharges, value));
            stack.getOrCreateTag().putInt("Charges", clamped);
        }
    }

    public static int getFluid(ItemStack stack) {
        return getCharges(stack);
    }

    public static int getMaxFluid(ItemStack stack) {
        if (stack.getItem() instanceof PortalGunItem gun) {
            return gun.gunType.maxCharges < 0 ? 999999 : gun.gunType.maxCharges;
        }
        return 100;
    }

    public static void setFluid(ItemStack stack, int value) {
        setCharges(stack, value);
    }

    public static String getTargetDimension(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains("TargetDim") ? tag.getString("TargetDim") : "rickmod:citadel_of_ricks";
    }

    public static void setTargetDimension(ItemStack stack, String dimId) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("TargetDim", dimId);
        tag.putBoolean("UseCustomPos", false);
    }

    public static void setCustomDestination(ItemStack stack, String dimId, BlockPos pos, String name) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("TargetDim", dimId);
        tag.putBoolean("UseCustomPos", true);
        tag.putInt("TargetX", pos.getX());
        tag.putInt("TargetY", pos.getY());
        tag.putInt("TargetZ", pos.getZ());
        tag.putString("TargetName", name);
    }

    public static void saveWaypoint(ItemStack stack, String dimId, BlockPos pos, String name) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList("Waypoints", Tag.TAG_COMPOUND);
        if (list.size() >= 16) {
            list.remove(0);
        }
        CompoundTag wp = new CompoundTag();
        wp.putString("Dim", dimId);
        wp.putInt("X", pos.getX());
        wp.putInt("Y", pos.getY());
        wp.putInt("Z", pos.getZ());
        wp.putString("Name", name);
        list.add(wp);
        tag.put("Waypoints", list);
    }

    public static void deleteWaypoint(ItemStack stack, int index) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList("Waypoints", Tag.TAG_COMPOUND);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            tag.put("Waypoints", list);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                BlockPos pos = player.blockPosition();
                String dimStr = level.dimension().location().toString();
                String wpName = "Точка (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
                saveWaypoint(stack, dimStr, pos, wpName);
                player.sendSystemMessage(Component.literal("§a[Портальная пушка] Координаты сохранены: §f" + wpName));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (!isInfinite() && !player.getAbilities().instabuild) {
            int charges = getCharges(stack);
            if (charges <= 0) {
                if (!level.isClientSide) {
                    player.sendSystemMessage(Component.literal("§c[Портальная пушка] Заряд пуст! Заправьте колбой с портальной жидкостью."));
                }
                return InteractionResultHolder.fail(stack);
            }
            setCharges(stack, charges - 1);
        }

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 end = eyePos.add(look.scale(14.0D));

            BlockHitResult hit = level.clip(new ClipContext(eyePos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            Vec3 spawnPos;
            Direction facing = player.getDirection().getOpposite();

            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos adj = hit.getBlockPos().relative(hit.getDirection());
                spawnPos = new Vec3(adj.getX() + 0.5D, adj.getY(), adj.getZ() + 0.5D);
            } else {
                Vec3 air = eyePos.add(look.scale(4.0D));
                spawnPos = new Vec3(air.x, Math.floor(air.y), air.z);
            }

            String targetDimStr = getTargetDimension(stack);
            if ("minecraft:the_nether".equals(targetDimStr) || "minecraft:the_end".equals(targetDimStr)) {
                targetDimStr = "rickmod:citadel_of_ricks";
            }

            CompoundTag tag = stack.getOrCreateTag();
            BlockPos targetPos = tag.getBoolean("UseCustomPos")
                    ? new BlockPos(tag.getInt("TargetX"), tag.getInt("TargetY"), tag.getInt("TargetZ"))
                    : new BlockPos(0, 82, 0);

            PortalEntity portal = new PortalEntity(ModEntities.PORTAL.get(), serverLevel);
            portal.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            portal.setYRot(facing.toYRot());
            portal.setGunType(gunType);
            portal.setDestination(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetDimStr)), targetPos);

            serverLevel.addFreshEntity(portal);
            serverLevel.playSound(null, BlockPos.containing(spawnPos), ModSounds.PORTAL_GUN_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        player.getCooldowns().addCooldown(this, 15);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !isInfinite();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (isInfinite()) return 13;
        return Math.round(13.0F * getCharges(stack) / (float) gunType.maxCharges);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return gunType.color;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (isInfinite()) {
            tooltip.add(Component.literal("Заряд: ∞ (Бесконечный)").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.literal("Заряд: " + getCharges(stack) + " / " + gunType.maxCharges).withStyle(ChatFormatting.GREEN));
        }
        tooltip.add(Component.literal("Цель: " + getTargetDimension(stack)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("[G] Навигатор измерений | [Shift+ПКМ] Сохранить точку").withStyle(ChatFormatting.GRAY));
    }
}
