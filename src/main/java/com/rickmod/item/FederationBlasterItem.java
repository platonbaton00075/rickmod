package com.rickmod.item;

import com.rickmod.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class FederationBlasterItem extends Item {
    public static final int MAX_AMMO = 30;
    public static final int AMMO_PER_CELL = 15;
    private static final String TAG_AMMO = "PlasmaAmmo";

    public FederationBlasterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static int getAmmo(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(TAG_AMMO)) {
            setAmmo(stack, MAX_AMMO);
        }
        return stack.getOrCreateTag().getInt(TAG_AMMO);
    }

    public static void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateTag().putInt(TAG_AMMO, Math.max(0, Math.min(MAX_AMMO, ammo)));
    }

    private boolean reloadFromInventory(Player player, ItemStack blaster) {
        int current = getAmmo(blaster);
        if (current >= MAX_AMMO) return false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(ModItems.PLASMA_CELL.get())) {
                invStack.shrink(1);
                setAmmo(blaster, current + AMMO_PER_CELL);
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Manual reload on Shift + Right Click
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (reloadFromInventory(player, stack)) {
                    level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.7F, 1.8F);
                    player.displayClientMessage(Component.literal("Бластер перезаряжен: " + getAmmo(stack) + "/" + MAX_AMMO).withStyle(ChatFormatting.GREEN), true);
                } else {
                    player.displayClientMessage(Component.literal("Нет плазменных энергоячеек!").withStyle(ChatFormatting.RED), true);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        int ammo = getAmmo(stack);
        if (!player.getAbilities().instabuild) {
            if (ammo <= 0) {
                if (!level.isClientSide) {
                    if (reloadFromInventory(player, stack)) {
                        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.7F, 1.8F);
                        player.displayClientMessage(Component.literal("Автоперезарядка: " + getAmmo(stack) + "/" + MAX_AMMO).withStyle(ChatFormatting.YELLOW), true);
                    } else {
                        level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.4F);
                        player.displayClientMessage(Component.literal("Патроны закончились! Нужны плазменные энергоячейки.").withStyle(ChatFormatting.RED), true);
                    }
                }
                return InteractionResultHolder.fail(stack);
            }
            setAmmo(stack, ammo - 1);
        }

        player.getCooldowns().addCooldown(this, 8);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.8F, 1.7F);
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            double range = 36.0;
            Vec3 end = eye.add(look.scale(range));

            BlockHitResult blockHit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            double maxDist = blockHit.getLocation().distanceTo(eye);
            Vec3 actualEnd = eye.add(look.scale(maxDist));

            AABB box = player.getBoundingBox().expandTowards(look.scale(maxDist)).inflate(1.0);
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, eye, actualEnd, box, e -> e instanceof LivingEntity && e != player);

            if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                actualEnd = entityHit.getLocation();
                target.hurt(level.damageSources().playerAttack(player), 11.0F);
                target.setSecondsOnFire(3);
            }

            DustParticleOptions plasmaRed = new DustParticleOptions(new Vector3f(1.0F, 0.12F, 0.22F), 1.3F);
            double dist = eye.distanceTo(actualEnd);
            for (double d = 0.6; d < dist; d += 0.45) {
                Vec3 p = eye.add(look.scale(d));
                serverLevel.sendParticles(plasmaRed, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int ammo = getAmmo(stack);
        ChatFormatting color = ammo > 10 ? ChatFormatting.GREEN : (ammo > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED);
        tooltip.add(Component.literal("Заряд плазмы: " + ammo + " / " + MAX_AMMO).withStyle(color));
        tooltip.add(Component.literal("Shift + ПКМ: Перезарядить энергоячейкой").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
