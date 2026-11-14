package com.rickmod.item;

import com.rickmod.client.PortalGunItemRenderer;
import com.rickmod.entity.PortalEntity;
import com.rickmod.registry.ModEntities;
import com.rickmod.registry.ModItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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
        RICK(100, 0x39FF14),
        PRIME(-1, 0x00FF44),
        EVIL_MORTY(250, 0xFFD700);

        public final int maxCharge;
        public final int color;

        GunType(int maxCharge, int color) {
            this.maxCharge = maxCharge;
            this.color = color;
        }
    }

    private final GunType gunType;

    public PortalGunItem(GunType gunType, Properties props) {
        super(props.stacksTo(1));
        this.gunType = gunType;
    }

    public GunType getGunType() {
        return gunType;
    }

    public boolean isInfinite() {
        return gunType.maxCharge < 0;
    }

    public int getMaxCharge() {
        return gunType.maxCharge;
    }

    public int getCharge(ItemStack stack) {
        if (isInfinite()) return Integer.MAX_VALUE;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Charge")) {
            tag.putInt("Charge", gunType.maxCharge);
        }
        return tag.getInt("Charge");
    }

    public void setCharge(ItemStack stack, int charge) {
        if (isInfinite()) return;
        stack.getOrCreateTag().putInt("Charge", Math.max(0, Math.min(gunType.maxCharge, charge)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift + ПКМ: сохранить текущую точку координат
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                saveCurrentWaypoint(level, player, stack);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        // Перезарядка колбой портальной жидкости (если заряд не полный)
        if (!isInfinite() && getCharge(stack) < getMaxCharge()) {
            ItemStack flask = findFlask(player);
            if (!flask.isEmpty()) {
                if (!level.isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        flask.shrink(1);
                    }
                    setCharge(stack, getCharge(stack) + 50);
                    level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0f, 1.2f);
                    player.displayClientMessage(Component.literal("§aПортальная пушка перезаряжена! Заряд: " + getCharge(stack) + "/" + getMaxCharge()), true);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
        }

        // Проверка наличия заряда
        if (!isInfinite() && getCharge(stack) <= 0) {
            if (!level.isClientSide) {
                level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 0.8f);
                player.displayClientMessage(Component.literal("§cНет портальной жидкости! Возьмите колбу и нажмите ПКМ."), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        // Выстрел порталом
        if (!level.isClientSide) {
            if (!isInfinite() && !player.getAbilities().instabuild) {
                setCharge(stack, getCharge(stack) - 1);
            }

            Vec3 look = player.getLookAngle();
            Vec3 spawnPos = player.position().add(look.x * 2.5, 0.2, look.z * 2.5);

            CompoundTag tag = stack.getOrCreateTag();
            String targetDimStr = tag.contains("TargetDim") ? tag.getString("TargetDim") : "minecraft:overworld";
            BlockPos targetPos = tag.contains("TargetX")
                    ? new BlockPos(tag.getInt("TargetX"), tag.getInt("TargetY"), tag.getInt("TargetZ"))
                    : new BlockPos(0, 80, 0);

            PortalEntity portal = new PortalEntity(ModEntities.PORTAL.get(), level);
            portal.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            portal.setYRot(player.getYRot());
            portal.setGunType(gunType);
            portal.setDestination(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(targetDimStr)), targetPos);

            level.addFreshEntity(portal);
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.4f);
            player.getCooldowns().addCooldown(this, 15);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private void saveCurrentWaypoint(Level level, Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag waypoints = tag.getList("Waypoints", Tag.TAG_COMPOUND);
        if (waypoints.size() >= 16) {
            waypoints.remove(0);
        }
        String dimName = level.dimension().location().toString();
        BlockPos pos = player.blockPosition();
        String label = dimName.substring(dimName.indexOf(':') + 1) + " [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";

        CompoundTag wp = new CompoundTag();
        wp.putString("Name", label);
        wp.putString("Dim", dimName);
        wp.putInt("X", pos.getX());
        wp.putInt("Y", pos.getY());
        wp.putInt("Z", pos.getZ());
        waypoints.add(wp);
        tag.put("Waypoints", waypoints);

        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8f, 1.5f);
        player.displayClientMessage(Component.literal("§aТочка сохранена: §f" + label), true);
    }

    private ItemStack findFlask(Player player) {
        if (player.getOffhandItem().is(ModItems.PORTAL_FLUID_FLASK.get())) {
            return player.getOffhandItem();
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(ModItems.PORTAL_FLUID_FLASK.get())) {
                return invStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (isInfinite()) {
            tooltip.add(Component.literal("§aЗаряд: §l∞ (Без лимита)"));
        } else {
            tooltip.add(Component.literal("§aЗаряд: §f" + getCharge(stack) + " / " + getMaxCharge()));
        }
        CompoundTag tag = stack.getOrCreateTag();
        String destName = tag.contains("TargetName") ? tag.getString("TargetName") : "Обычный мир (C-137)";
        tooltip.add(Component.literal("§7Цель: §e" + destName));
        tooltip.add(Component.literal("§8[G] — Навигатор | [Shift+ПКМ] — Сохранить точку"));
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
