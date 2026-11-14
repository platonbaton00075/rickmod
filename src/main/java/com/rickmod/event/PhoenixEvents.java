package com.rickmod.event;

import com.rickmod.RickMod;
import com.rickmod.registry.ModBlocks;
import com.rickmod.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID)
public class PhoenixEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean("rickmod_phoenix_active")) return;

        int px = data.getInt("rickmod_phoenix_x");
        int py = data.getInt("rickmod_phoenix_y");
        int pz = data.getInt("rickmod_phoenix_z");
        String dimStr = data.getString("rickmod_phoenix_dim");

        MinecraftServer server = player.getServer();
        if (server == null) return;

        ResourceKey<Level> targetDimKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimStr));
        ServerLevel targetLevel = server.getLevel(targetDimKey);
        if (targetLevel == null) return;

        BlockPos podPos = new BlockPos(px, py, pz);
        if (!targetLevel.getBlockState(podPos).is(ModBlocks.PHOENIX_POD.get())) {
            data.putBoolean("rickmod_phoenix_active", false);
            player.sendSystemMessage(Component.literal("§c[Проект Феникс] Ошибка переноса: капсула клонирования разрушена!"));
            return;
        }

        // Cancel death completely!
        event.setCanceled(true);

        // Save persistent data across dimension transfer if needed
        CompoundTag savedClone = data.copy();

        player.removeAllEffects();
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        player.fallDistance = 0.0F;
        player.clearFire();

        double destX = px + 0.5D;
        double destY = py + 0.2D;
        double destZ = pz + 0.5D;

        if (player.level() != targetLevel) {
            player.teleportTo(targetLevel, destX, destY, destZ, player.getYRot(), player.getXRot());
        } else {
            player.teleportTo(destX, destY, destZ);
        }

        player.getPersistentData().merge(savedClone);

        // Apply Fresh Clone + elite combat buffs
        player.addEffect(new MobEffectInstance(ModEffects.FRESH_CLONE.get(), 2400, 0));
        player.setHealth(player.getMaxHealth());
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 2)); // +12 gold hearts (2 min)
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1)); // -40% damage (30 sec)
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1)); // Regen II (20 sec)
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0)); // Fire res (1 min)

        // Bursting out of glass pod visual & sound effects
        targetLevel.playSound(null, podPos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.3F, 0.9F);
        targetLevel.playSound(null, podPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.2F, 1.4F);
        targetLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CYAN_STAINED_GLASS.defaultBlockState()),
                destX, destY + 1.0D, destZ, 45, 0.4D, 0.6D, 0.4D, 0.15D);
        targetLevel.sendParticles(ParticleTypes.END_ROD, destX, destY + 1.0D, destZ, 35, 0.5D, 0.8D, 0.5D, 0.08D);
        targetLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, destX, destY + 0.5D, destZ, 25, 0.4D, 0.5D, 0.4D, 0.05D);

        player.sendSystemMessage(Component.literal("§b[Проект Феникс] §aСознание перенесено в свежего клона! Снаряжение сохранено, активирован протокол защиты."));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        if (oldData.getBoolean("rickmod_phoenix_active")) {
            CompoundTag newData = event.getEntity().getPersistentData();
            newData.putInt("rickmod_phoenix_x", oldData.getInt("rickmod_phoenix_x"));
            newData.putInt("rickmod_phoenix_y", oldData.getInt("rickmod_phoenix_y"));
            newData.putInt("rickmod_phoenix_z", oldData.getInt("rickmod_phoenix_z"));
            newData.putString("rickmod_phoenix_dim", oldData.getString("rickmod_phoenix_dim"));
            newData.putBoolean("rickmod_phoenix_active", true);
        }
    }
}
