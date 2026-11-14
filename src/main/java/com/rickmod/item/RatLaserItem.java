package com.rickmod.item;

import com.rickmod.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RatLaserItem extends Item {
    public RatLaserItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            level.playSound(null, player.blockPosition(), ModSounds.BLASTER_FIRE.get(), SoundSource.PLAYERS, 0.9F, 1.3F);
            Vec3 start = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            for (int i = 1; i <= 24; i++) {
                Vec3 pos = start.add(look.scale(i));
                serverLevel.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 2, 0.03, 0.03, 0.03, 0.0);
                AABB box = new AABB(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
                List<LivingEntity> hits = serverLevel.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive());
                if (!hits.isEmpty()) {
                    for (LivingEntity target : hits) {
                        target.hurt(level.damageSources().playerAttack(player), 9.0F);
                        target.setSecondsOnFire(4);
                    }
                    break;
                }
            }
            player.getCooldowns().addCooldown(this, 10);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Наручный лазер Огурчика Рика [ПКМ]").withStyle(ChatFormatting.RED));
    }
}
