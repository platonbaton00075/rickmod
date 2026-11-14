package com.rickmod.events;

import com.rickmod.RickMod;
import com.rickmod.entity.GromflomiteSoldierEntity;
import com.rickmod.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID)
public class FederationInvasionEvents {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        if (level.getGameTime() % 24000 != 0) return;

        for (ServerPlayer player : level.players()) {
            if (level.random.nextFloat() < 0.25F) {
                BlockPos base = player.blockPosition().offset(level.random.nextInt(20) - 10, 0, level.random.nextInt(20) - 10);
                for (int i = 0; i < 2; i++) {
                    GromflomiteSoldierEntity soldier = ModEntities.GROMFLOMITE_SOLDIER.get().create(level);
                    if (soldier != null) {
                        soldier.moveTo(base.getX() + i, base.getY() + 1, base.getZ(), 0, 0);
                        soldier.finalizeSpawn(level, level.getCurrentDifficultyAt(base), MobSpawnType.EVENT, null, null);
                        level.addFreshEntity(soldier);
                    }
                }
            }
        }
    }
}
