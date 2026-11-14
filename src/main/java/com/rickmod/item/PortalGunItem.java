package com.rickmod.item;

import com.rickmod.RickMod;
import com.rickmod.client.PortalGunItemRenderer;
import com.rickmod.entity.PortalEntity;
import com.rickmod.init.ModEntities;
import com.rickmod.init.ModItems;
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
        RICK(100, 0x39FF14, false, "Rick"),
        PRIME(-1, 0x00FF44, true, "Prime"),
        EVIL_MORTY(250, 0xFFD700, true, "EvilMorty");

        public final int maxCharge;
        public final int color;
        public final boolean bypassBarriers;
        public final String id;

        GunType(int maxCharge, int color, boolean bypassBarriers, String id) {
            this.maxCharge = maxCharge;
            this.color = color;
            this.bypassBarriers = bypassBarriers;
            this.id = id;
        }
    }

    private final GunType gunType;

    public PortalGunItem(GunType gunType, Properties props) {
        super(props);
        this.gunType = gunType;
    }

    public PortalGunItem(Properties props, int maxCharge, int color, boolean bypass, String id) {
        super(props);
        if ("Prime".equalsIgnoreCase(id) || maxCharge < 0) {
            this.gunType = GunType.PRIME;
        } else if ("EvilMorty".equalsIgnoreCase(id) || color == 0xFFD700) {
            this.gunType = GunType.EVIL_MORTY;
        } else {
            this.gunType = GunType.RICK;
        }
    }

    public GunType getGunType() {
        return gunType;
    }

    public static int getCharge(ItemStack stack, GunType type) {
        if (type == GunType.PRIME || type.maxCharge < 0) return -1;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("PortalCharge")) {
            tag.putInt("PortalCharge", type.maxCharge);
        }
        return tag.getInt("PortalCharge");
    }

    public static void setCharge(ItemStack stack, int charge) {
        stack.getOrCreateTag().putInt("PortalCharge", Math.max(0, charge));
    }

    public static String getTargetDimension(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        String dim = tag.getString("TargetDim");
        if (dim.isEmpty() || dim.equals("minecraft:the_nether") || dim.equals("minecraft:the_end")) {
            dim = "rickmod:citadel_of_ricks";
            tag.putString("TargetDim", dim);
        }
        return dim;
    }

    public static void setTargetDimension(ItemStack stack, String dimId) {
        if ("minecraft:the_nether".equals(dimId) || "minecraft:the_end".equals(dimId)) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("TargetDim", dimId);
        tag.putBoolean("UseCustomPos", false);
    }

    public static void setTargetDestination(ItemStack stack, String dimId, BlockPos pos, String name) {
        if ("minecraft:the_nether".equals(dimId) || "minecraft:the_end".equals(dimId)) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("TargetDim", dimId);
        tag.putInt("TargetX", pos.getX());
        tag.putInt("TargetY", pos.getY());
        tag.putInt("TargetZ", pos.getZ());
        tag.putString("TargetName", name);
        tag.putBoolean("UseCustomPos", true);
    }

    public static BlockPos getTargetPos(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean("UseCustomPos")) {
            return new BlockPos(tag.getInt("TargetX"), tag.getInt("TargetY"), tag.getInt("TargetZ"));
        }
        return new BlockPos(0, 82, 0);
    }

    public static ListTag getWaypoints(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Waypoints", Tag.TAG_LIST)) {
            tag.put("Waypoints", new ListTag());
        }
        return tag.getList("Waypoints", Tag.TAG_COMPOUND);
    }

    public static void addWaypoint(ItemStack stack, String dimId, BlockPos pos, String name) {
        ListTag list = getWaypoints(stack);
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
    }

    public static void deleteWaypoint(ItemStack stack, int index) {
        ListTag list = getWaypoints(stack);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                String currentDim = level.dimension().location().toString();
                if (!"minecraft:the_nether".equals(currentDim) && !"minecraft:the_end".equals(currentDim)) {
                    BlockPos pos = player.blockPosition();
                    String wpName = "Точка #" + (getWaypoints(stack).size() + 1) + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
                    addWaypoint(stack, currentDim, pos, wpName);
                    setTargetDestination(stack, currentDim, pos, wpName);
                    player.displayClientMessage(Component.literal("Сохранена точка: " + wpName).withStyle(ChatFormatting.GREEN), true);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        int charge = getCharge(stack, gunType);
        if (gunType != GunType.PRIME && charge <= 0 && !player.getAbilities().instabuild) {
            if (!level.isClientSide) {
                if (tryReloadFromInventory(player, stack)) {
                    player.displayClientMessage(Component.literal("Пушка перезаряжена колбой портальной жидкости!").withStyle(ChatFormatting.GREEN), true);
                } else {
                    player.displayClientMessage(Component.literal("Нет портальной жидкости! Нужна колба для заправки.").withStyle(ChatFormatting.RED), true);
                }
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            if (gunType != GunType.PRIME && !player.getAbilities().instabuild) {
                setCharge(stack, charge - 1);
            }

            Vec3 look = player.getLookAngle();
            Vec3 spawnPos = player.getEyePosition().add(look.scale(2.8));

            PortalEntity portal = new PortalEntity(ModEntities.PORTAL.get(), level);
            portal.setPos(spawnPos.x, spawnPos.y - 1.0, spawnPos.z);
            portal.setYRot(player.getYRot());
            portal.setGunType(gunType);

            String targetDimStr = getTargetDimension(stack);
            BlockPos targetPos = getTargetPos(stack);
            ResourceKey<Level> destKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetDimStr));
            portal.setDestination(destKey, targetPos);

            level.addFreshEntity(portal);
            level.playSound(null, player.blockPosition(), ModSounds.PORTAL_GUN_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        player.getCooldowns().addCooldown(this, 12);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private boolean tryReloadFromInventory(Player player, ItemStack gunStack) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack inv = player.getInventory().getItem(i);
            if (inv.is(ModItems.PORTAL_FLUID_FLASK.get())) {
                inv.shrink(1);
                setCharge(gunStack, gunType.maxCharge);
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (gunType == GunType.PRIME) {
            tooltip.add(Component.literal("Заряд: ∞ (Без лимита)").withStyle(ChatFormatting.GREEN));
        } else {
            int cur = getCharge(stack, gunType);
            tooltip.add(Component.literal("Заряд: " + cur + " / " + gunType.maxCharge).withStyle(ChatFormatting.AQUA));
        }
        tooltip.add(Component.literal("Цель: " + getTargetDimension(stack)).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("[G] Навигатор | [Shift + ПКМ] Сохранить точку").withStyle(ChatFormatting.GRAY));
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
