package com.rickmod.event;

import com.rickmod.RickMod;
import com.rickmod.entity.MeeseeksEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID)
public class MeeseeksChatHandler {

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String msg = event.getRawText();
        String lower = msg.toLowerCase();

        if (lower.contains("мисикс") || lower.contains("meeseeks") || lower.contains("синий")) {
            AABB searchBox = player.getBoundingBox().inflate(32.0D);
            List<MeeseeksEntity> list = player.level().getEntitiesOfClass(MeeseeksEntity.class, searchBox,
                    e -> player.getUUID().equals(e.getOwnerUUID()));
            for (MeeseeksEntity meeseeks : list) {
                meeseeks.handleRussianCommand(player, msg);
            }
        }
    }
}
