package com.rickmod.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class UseImplantAbilityPacket {
    private final int abilityId;

    public UseImplantAbilityPacket(int abilityId) {
        this.abilityId = abilityId;
    }

    public static void encode(UseImplantAbilityPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.abilityId);
    }

    public static UseImplantAbilityPacket decode(FriendlyByteBuf buf) {
        return new UseImplantAbilityPacket(buf.readInt());
    }

    public static void handle(UseImplantAbilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            ServerLevel level = player.serverLevel();
            long now = level.getGameTime();
            long cd = player.getPersistentData().getLong("rickmod_imp_cd_" + msg.abilityId);
            if (now < cd) return;

            if (msg.abilityId == 1 && player.getPersistentData().getBoolean("rickmod_has_plasma_arm")) {
                player.getPersistentData().putLong("rickmod_imp_cd_1", now + 25);
                Vec3 eye = player.getEyePosition();
                Vec3 look = player.getLookAngle();
                level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8f, 1.8f);
                for (int i = 1; i <= 20; i++) {
                    Vec3 p = eye.add(look.scale(i * 0.8));
                    level.sendParticles(ParticleTypes.GLOW, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0.01);
                    List<LivingEntity> hits = level.getEntitiesOfClass(LivingEntity.class, new AABB(p.x - 0.6, p.y - 0.6, p.z - 0.6, p.x + 0.6, p.y + 0.6, p.z + 0.6), e -> e != player);
                    if (!hits.isEmpty()) {
                        hits.get(0).hurt(player.damageSources().playerAttack(player), 12.0f);
                        hits.get(0).setSecondsOnFire(3);
                        break;
                    }
                }
            } else if (msg.abilityId == 2 && player.getPersistentData().getBoolean("rickmod_has_morty_eye")) {
                player.getPersistentData().putLong("rickmod_imp_cd_2", now + 100);
                List<Mob> mobs = level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(14.0));
                int count = 0;
                for (Mob m : mobs) {
                    if (m.getTarget() == player) m.setTarget(null);
                    m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
                    m.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2));
                    count++;
                }
                level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.4f);
                player.displayClientMessage(Component.literal("§6[Кибер-глаз] Перехвачено целей: " + count), true);
            } else if (msg.abilityId == 3 && player.getPersistentData().getBoolean("rickmod_has_phase_legs")) {
                player.getPersistentData().putLong("rickmod_imp_cd_3", now + 40);
                Vec3 dash = player.getLookAngle().multiply(1.8, 0.35, 1.8);
                player.setDeltaMovement(dash.x, Math.max(0.25, dash.y), dash.z);
                player.hurtMarked = true;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 4));
                level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 25, 0.4, 0.6, 0.4, 0.2);
                level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.9f, 1.5f);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
