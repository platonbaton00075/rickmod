package com.rickmod.pickle;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class RatLaserItem extends Item {
    public RatLaserItem(Properties props) {
        super(props.stacksTo(1).durability(400));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!PickleStateManager.isPickle(player)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("Наручный лазер работает только у Огурчика Рика!").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            boolean hasExo = player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof RatExoskeletonItem;
            float damage = hasExo ? 16.0f : 9.0f;
            Vec3 start = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            DustParticleOptions redLaser = new DustParticleOptions(new Vector3f(1.0f, 0.08f, 0.08f), 1.3f);

            for (int i = 1; i <= 24; i++) {
                Vec3 pos = start.add(look.scale(i * 0.75));
                serverLevel.sendParticles(redLaser, pos.x, pos.y, pos.z, 2, 0.03, 0.03, 0.03, 0.0);
                if (i % 4 == 0) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 1, 0.05, 0.05, 0.05, 0.01);
                }
                AABB box = new AABB(pos.x - 0.6, pos.y - 0.6, pos.z - 0.6, pos.x + 0.6, pos.y + 0.6, pos.z + 0.6);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player)) {
                    target.hurt(player.damageSources().playerAttack(player), damage);
                    target.setSecondsOnFire(4);
                }
            }
            level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.8f, 1.8f);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            player.getCooldowns().addCooldown(this, hasExo ? 8 : 16);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Наплечный батареечный лазер Огурчика Рика.").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Урон и скорострельность удваиваются в Крысином экзоскелете.").withStyle(ChatFormatting.GRAY));
    }
}
