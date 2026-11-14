package com.rickmod.pickle;

import com.rickmod.network.ModNetwork;
import com.rickmod.network.SyncPicklePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "rickmod")
public class PickleStateManager {
    public static final String TAG_PICKLE = "rickmod_is_pickle";
    private static final UUID PICKLE_HEALTH_UUID = UUID.fromString("a1b2c3d4-1111-2222-3333-444455556666");
    private static final UUID PICKLE_DAMAGE_UUID = UUID.fromString("a1b2c3d4-1111-2222-3333-444455557777");

    public static boolean isPickle(Player player) {
        return player.getPersistentData().getBoolean(TAG_PICKLE);
    }

    public static void setPickle(Player player, boolean pickle) {
        player.getPersistentData().putBoolean(TAG_PICKLE, pickle);
        player.refreshDimensions();
        updateAttributes(player, pickle);
        if (player instanceof ServerPlayer sp) {
            ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sp),
                    new SyncPicklePacket(sp.getId(), pickle));
        }
    }

    private static void updateAttributes(Player player, boolean pickle) {
        var maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        var attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (maxHealth != null) {
            maxHealth.removeModifier(PICKLE_HEALTH_UUID);
            if (pickle) {
                maxHealth.addTransientModifier(new AttributeModifier(PICKLE_HEALTH_UUID, "Pickle Fragility", -10.0, AttributeModifier.Operation.ADDITION));
            }
        }
        if (attackDamage != null) {
            attackDamage.removeModifier(PICKLE_DAMAGE_UUID);
            if (pickle) {
                attackDamage.addTransientModifier(new AttributeModifier(PICKLE_DAMAGE_UUID, "Pickle Weakness", -0.8, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player && isPickle(player)) {
            event.setNewSize(EntityDimensions.scalable(0.42f, 0.85f), true);
            event.setNewEyeHeight(0.72f);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (isPickle(player) && player.getBbHeight() > 1.0f) {
            player.refreshDimensions();
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            setPickle(event.getEntity(), false);
        } else {
            setPickle(event.getEntity(), isPickle(event.getOriginal()));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            boolean state = isPickle(sp);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new SyncPicklePacket(sp.getId(), state));
        }
    }
}
