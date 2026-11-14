package com.rickmod.events;

import com.rickmod.entity.GromflomiteSoldierEntity;
import com.rickmod.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rickmod")
public class FederationInvasionEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        ServerLevel level = player.serverLevel();
        ResourceLocation dim = level.dimension().location();

        // Patrol spawn every ~4 minutes in Overworld / Cronenberg, or raid in Citadel
        if (level.getGameTime() % 4800 == 0 && level.random.nextFloat() < 0.45F) {
            int dx = level.random.nextIntBetweenInclusive(-18, 18);
            int dz = level.random.nextIntBetweenInclusive(-18, 18);
            if (Math.abs(dx) < 8) dx = 10;
            BlockPos base = player.blockPosition().offset(dx, 0, dz);
            int y = dim.getPath().equals("citadel_of_ricks") ? player.blockPosition().getY() : level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base.getX(), base.getZ());
            BlockPos spawnPos = new BlockPos(base.getX(), y, base.getZ());

            if (level.getBlockState(spawnPos).isAir() && !level.getBlockState(spawnPos.below()).isAir()) {
                for (int i = 0; i < 3; i++) {
                    GromflomiteSoldierEntity soldier = ModEntities.GROMFLOMITE_SOLDIER.get().create(level);
                    if (soldier != null) {
                        soldier.moveTo(spawnPos.getX() + i * 1.2, spawnPos.getY(), spawnPos.getZ(), 0, 0);
                        level.addFreshEntity(soldier);
                    }
                }
                level.sendParticles(ParticleTypes.PORTAL, spawnPos.getX(), spawnPos.getY() + 1, spawnPos.getZ(), 40, 1.5, 1.0, 1.5, 0.2);
                level.playSound(null, spawnPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.2F, 0.7F);
                player.sendSystemMessage(Component.literal("§c[Галактическая Федерация] §fВнимание! Обнаружен нарушитель межпространственного режима. Выслан отряд зачистки!"));
            }
        }
    }
}
