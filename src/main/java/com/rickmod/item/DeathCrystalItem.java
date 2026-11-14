package com.rickmod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

public class DeathCrystalItem extends Item {
    public static final DustParticleOptions CRIMSON_DUST =
            new DustParticleOptions(new Vector3f(0.95f, 0.12f, 0.42f), 1.1f);

    public DeathCrystalItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }
            // Active vision pulse: reveals all threats within 32 blocks & grants brief foresight speed
            AABB area = player.getBoundingBox().inflate(32.0);
            List<LivingEntity> threats = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != player && e.isAlive());
            int dangerCount = 0;
            for (LivingEntity target : threats) {
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0, false, false));
                if (target instanceof Enemy) {
                    dangerCount++;
                }
            }
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1, false, false));
            player.getCooldowns().addCooldown(this, 100);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 0.65f);

            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 24; i++) {
                    double angle = (Math.PI * 2 * i) / 24.0;
                    serverLevel.sendParticles(CRIMSON_DUST,
                            player.getX() + Math.cos(angle) * 1.1,
                            player.getY() + 1.1,
                            player.getZ() + Math.sin(angle) * 1.1,
                            1, 0, 0.05, 0, 0.01);
                }
            }

            player.displayClientMessage(
                    Component.literal("§d[Кристалл Смерти] §7Линий вероятной гибели рядом: §c" + dangerCount),
                    true
            );
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            boolean inOffhand = player.getOffhandItem() == stack;
            if ((isSelected || inOffhand) && level.getGameTime() % 20 == 0) {
                // Passive short-range foresight highlights nearby enemies
                AABB area = player.getBoundingBox().inflate(14.0);
                List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, area,
                        e -> e instanceof Enemy && e.isAlive());
                for (LivingEntity enemy : enemies) {
                    enemy.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
                }
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Показывает все варианты твоей смерти").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("• В левой руке: авто-уклонение от смертельного удара").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• ПКМ: импульс предвидения врагов (32 блока)").withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.literal("• Можно встроить в обруч на лоб (без потери защиты)").withStyle(ChatFormatting.GOLD));
    }
}
