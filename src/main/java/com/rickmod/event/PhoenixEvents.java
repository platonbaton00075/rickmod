package com.rickmod.event;

import com.rickmod.RickMod;
import com.rickmod.init.ModBlocks;
import com.rickmod.init.ModEffects;
import com.rickmod.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID)
public class PhoenixEvents {
    private static final String TAG_DIM = "RickModPhoenixDim";
    private static final String TAG_X = "RickModPhoenixX";
    private static final String TAG_Y = "RickModPhoenixY";
    private static final String TAG_Z = "RickModPhoenixZ";

    public static void setBoundPod(ServerPlayer player, ResourceKey<Level> dim, BlockPos pos) {
        CompoundTag data = player.getPersistentData();
        data.putString(TAG_DIM, dim.location().toString());
        data.putInt(TAG_X, pos.getX());
        data.putInt(TAG_Y, pos.getY());
        data.putInt(TAG_Z, pos.getZ());
    }

    public static void bindPod(ServerPlayer player, ResourceKey<Level> dim, BlockPos pos) {
        setBoundPod(player, dim, pos);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerFatalDamage(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_DIM)) return;

        String dimStr = data.getString(TAG_DIM);
        BlockPos podPos = new BlockPos(data.getInt(TAG_X), data.getInt(TAG_Y), data.getInt(TAG_Z));
        ResourceKey<Level> targetDimKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimStr));

        ServerLevel targetLevel = player.server.getLevel(targetDimKey);
        if (targetLevel == null) return;

        if (!targetLevel.getBlockState(podPos).is(ModBlocks.PHOENIX_POD.get())) {
            player.sendSystemMessage(Component.literal("§c[Проект «Феникс»] Капсула клонирования уничтожена или отсутствует!"));
            return;
        }

        event.setCanceled(true);
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(10.0F);
        player.removeAllEffects();
        player.clearFire();
        player.fallDistance = 0.0F;

        double spawnX = podPos.getX() + 0.5D;
        double spawnY = podPos.getY() + 0.2D;
        double spawnZ = podPos.getZ() + 0.5D;

        if (player.level() != targetLevel) {
            player.teleportTo(targetLevel, spawnX, spawnY, spawnZ, player.getYRot(), player.getXRot());
        } else {
            player.teleportTo(spawnX, spawnY, spawnZ);
        }

        player.addEffect(new MobEffectInstance(ModEffects.FRESH_CLONE.get(), 2400, 0));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));

        targetLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, spawnX, spawnY + 1.0, spawnZ, 40, 0.6, 0.9, 0.6, 0.1);
        targetLevel.sendParticles(ParticleTypes.PORTAL, spawnX, spawnY + 1.0, spawnZ, 50, 0.5, 0.8, 0.5, 0.2);
        targetLevel.playSound(null, podPos, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.1F);
        targetLevel.playSound(null, podPos, ModSounds.PHOENIX_REVIVE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

        player.sendSystemMessage(Component.literal("§a[Проект «Феникс»] Сознание перенесено в свежего клона! Вещи и опыт сохранены."));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();
        CompoundTag oldData = original.getPersistentData();
        if (oldData.contains(TAG_DIM)) {
            CompoundTag newData = clone.getPersistentData();
            newData.putString(TAG_DIM, oldData.getString(TAG_DIM));
            newData.putInt(TAG_X, oldData.getInt(TAG_X));
            newData.putInt(TAG_Y, oldData.getInt(TAG_Y));
            newData.putInt(TAG_Z, oldData.getInt(TAG_Z));
        }
    }
}
