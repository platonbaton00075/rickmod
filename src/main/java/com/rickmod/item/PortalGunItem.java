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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class PortalGunItem extends Item {
    public enum GunType {
        RICK(100, 0x39FF14, false),
        PRIME(-1, 0x00FF44, true),
        EVIL_MORTY(250, 0xFFD700, true);

        public final int maxCharges;
        public final int color;
        public final boolean bypassRestrictions;

        GunType(int maxCharges, int color, boolean bypassRestrictions) {
            this.maxCharges = maxCharges;
            this.color = color;
            this.bypassRestrictions = bypassRestrictions;
        }
    }

    private final GunType gunType;

    public PortalGunItem(GunType gunType, Properties properties) {
        super(properties);
        this.gunType = gunType;
    }

    public PortalGunItem(Properties properties, int maxCharges, int color, boolean bypassRestrictions, String variant) {
        super(properties);
        if ("Prime".equalsIgnoreCase(variant) || maxCharges < 0) {
            this.gunType = GunType.PRIME;
        } else if ("EvilMorty".equalsIgnoreCase(variant) || color == 0xFFD700) {
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
        if (!(stack.getItem() instanceof PortalGunItem gun)) return 0;
        if (gun.isInfinite()) return 999999;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Charges")) {
            tag.putInt("Charges", gun.getMaxCharges());
        }
        return tag.getInt("Charges");
    }

    public static void setCharges(ItemStack stack, int value) {
        if (!(stack.getItem() instanceof PortalGunItem gun)) return;
        if (gun.isInfinite()) return;
        stack.getOrCreateTag().putInt("Charges", Math.max(0, Math.min(gun.getMaxCharges(), value)));
    }

    public static int getFluid(ItemStack stack) {
        return getCharges(stack);
    }

    public static int getMaxFluid(ItemStack stack) {
        if (stack.getItem() instanceof PortalGunItem gun) return gun.getMaxCharges();
        return 100;
    }

    public static void setFluid(ItemStack stack, int value) {
        setCharges(stack, value);
    }

    public static void setTargetDimension(ItemStack stack, String dimId) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("TargetDim", dimId);
    }

    public static void setTargetDestination(ItemStack stack, String dimId, BlockPos pos, String label) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("TargetDim", dimId);
        tag.putInt("TargetX", pos.getX());
        tag.putInt("TargetY", pos.getY());
        tag.putInt("TargetZ", pos.getZ());
        if (label != null && !label.isEmpty()) {
            tag.putString("TargetLabel", label);
        }
    }

    public static ListTag getWaypoints(ItemStack stack) {
        return stack.getOrCreateTag().getList("Waypoints", Tag.TAG_COMPOUND);
    }

    public static void addWaypoint(ItemStack stack, String name, String dimId, BlockPos pos) {
        ListTag list = getWaypoints(stack);
        if (list.size() >= 16) return;
        CompoundTag wp = new CompoundTag();
        wp.putString("Name", name);
        wp.putString("Dim", dimId);
        wp.putInt("X", pos.getX());
        wp.putInt("Y", pos.getY());
        wp.putInt("Z", pos.getZ());
        list.add(wp);
        stack.getOrCreateTag().put("Waypoints", list);
    }

    public static void removeWaypoint(ItemStack stack, int index) {
        ListTag list = getWaypoints(stack);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            stack.getOrCreateTag().put("Waypoints", list);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (player.isShiftKeyDown()) {
            String dimStr = level.dimension().location().toString();
            BlockPos pos = player.blockPosition();
            String wpName = "Точка #" + (getWaypoints(stack).size() + 1) + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
            addWaypoint(stack, wpName, dimStr, pos);
            setTargetDestination(stack, dimStr, pos, wpName);
            player.displayClientMessage(Component.literal("Координаты сохранены: " + wpName).withStyle(ChatFormatting.GREEN), true);
            return InteractionResultHolder.success(stack);
        }

        if (!isInfinite() && !player.getAbilities().instabuild) {
            int charges = getCharges(stack);
            if (charges <= 0) {
                player.displayClientMessage(Component.literal("Недостаточно портальной жидкости!").withStyle(ChatFormatting.RED), true);
                return InteractionResultHolder.fail(stack);
            }
            setCharges(stack, charges - 1);
        }

        CompoundTag tag = stack.getOrCreateTag();
        String targetDimStr = tag.contains("TargetDim") ? tag.getString("TargetDim") : "rickmod:citadel_of_ricks";
        if ("minecraft:the_nether".equals(targetDimStr) || "minecraft:the_end".equals(targetDimStr)) {
            targetDimStr = "minecraft:overworld";
        }
        BlockPos targetPos = tag.contains("TargetX")
                ? new BlockPos(tag.getInt("TargetX"), tag.getInt("TargetY"), tag.getInt("TargetZ"))
                : new BlockPos(0, 82, 0);

        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.position().add(look.x * 2.5, 0.2, look.z * 2.5);

        PortalEntity portal = new PortalEntity(ModEntities.PORTAL.get(), level);
        portal.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        portal.setYRot(player.getYRot());
        portal.setGunType(gunType);
        portal.setDestination(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetDimStr)), targetPos);
        level.addFreshEntity(portal);

        level.playSound(null, player.blockPosition(), ModSounds.PORTAL_GUN_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(this, 15);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (isInfinite()) {
            tooltip.add(Component.literal("Заряд: ∞ (Без лимита)").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.literal("Заряд: " + getCharges(stack) + " / " + getMaxCharges()).withStyle(ChatFormatting.GREEN));
        }
        String dim = stack.getOrCreateTag().getString("TargetDim");
        if (!dim.isEmpty()) {
            tooltip.add(Component.literal("Цель: " + dim).withStyle(ChatFormatting.AQUA));
        }
        tooltip.add(Component.literal("[G] Выбор измерения | [Shift + ПКМ] Сохранить точку").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PortalGunItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new PortalGunItemRenderer();
                }
                return renderer;
            }
        });
    }
}
