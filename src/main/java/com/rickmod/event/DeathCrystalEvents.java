package com.rickmod.event;

import com.rickmod.item.DeathCrystalItem;
import com.rickmod.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rickmod")
public class DeathCrystalEvents {

    @SubscribeEvent
    public static void onPlayerFatalDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Level level = player.level();
        if (level.isClientSide) return;

        // Check if damage would kill the player
        if (player.getHealth() - event.getAmount() > 0.0f) return;

        ItemStack crystalStack = findActiveCrystal(player);
        if (crystalStack.isEmpty()) return;

        if (player.getCooldowns().isOnCooldown(crystalStack.getItem())) return;

        // Cancel lethal blow via temporal foresight shift
        event.setCanceled(true);
        player.setHealth(Math.max(4.0f, player.getMaxHealth() * 0.35f));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 2));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 1));

        // Cooldown: 30 seconds (600 ticks)
        player.getCooldowns().addCooldown(crystalStack.getItem(), 600);

        // Short phase-step backwards to evade danger
        double yawRad = Math.toRadians(player.getYRot());
        double backX = player.getX() + Math.sin(yawRad) * 3.0;
        double backZ = player.getZ() - Math.cos(yawRad) * 3.0;
        BlockPos safePos = BlockPos.containing(backX, player.getY(), backZ);
        if (level.isEmptyBlock(safePos) && level.isEmptyBlock(safePos.above())) {
            player.teleportTo(backX, player.getY(), backZ);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.8f, 1.35f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(DeathCrystalItem.CRIMSON_DUST,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    45, 0.6, 0.8, 0.6, 0.08);
        }

        player.displayClientMessage(
                Component.literal("§d[Кристалл Смерти] §fТы увидел свою гибель и шагнул в соседнюю ветку реальности!"),
                true
        );
    }

    private static ItemStack findActiveCrystal(Player player) {
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(ModItems.DEATH_CRYSTAL.get())) {
            return offhand;
        }
        ItemStack mainhand = player.getMainHandItem();
        if (mainhand.is(ModItems.DEATH_CRYSTAL.get())) {
            return mainhand;
        }
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (head.is(ModItems.DEATH_CRYSTAL_HEADBAND.get())) {
            return head;
        }
        return ItemStack.EMPTY;
    }
}
