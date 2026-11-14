package com.rickmod.implant;

import com.rickmod.entity.GromflomiteSoldierEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "rickmod")
public class ImplantEvents {

    private static final UUID SPEED_UUID = UUID.fromString("91a4f1b2-3d4e-4c5a-9b8e-111111111101");
    private static final UUID DAMAGE_UUID = UUID.fromString("91a4f1b2-3d4e-4c5a-9b8e-111111111102");
    private static final UUID REACH_UUID = UUID.fromString("91a4f1b2-3d4e-4c5a-9b8e-111111111103");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        CompoundTag data = player.getPersistentData();

        // 1. Щиты (Силовое поле Рика и Золотое поле Злого Морти)
        ItemStack rickShield = CuriosImplantHelper.getImplantStack(player, ImplantType.RICK_FORCE_FIELD);
        ItemStack mortyShield = CuriosImplantHelper.getImplantStack(player, ImplantType.EVIL_MORTY_FORCE_FIELD);
        ItemStack activeShieldStack = !mortyShield.isEmpty() ? mortyShield : rickShield;
        ImplantType activeShieldType = !mortyShield.isEmpty() ? ImplantType.EVIL_MORTY_FORCE_FIELD : ImplantType.RICK_FORCE_FIELD;

        if (!activeShieldStack.isEmpty()) {
            float current = data.getFloat("rickmod_shield_hp");
            int timer = data.getInt("rickmod_shield_timer");
            if (timer > 0) {
                data.putInt("rickmod_shield_timer", timer - 1);
            } else if (current < activeShieldType.maxShield) {
                current = Math.min(activeShieldType.maxShield, current + 0.1f);
                data.putFloat("rickmod_shield_hp", current);
            }
            data.putInt("rickmod_active_shield_color", activeShieldType == ImplantType.EVIL_MORTY_FORCE_FIELD ? 2 : 1);
        } else {
            data.putFloat("rickmod_shield_hp", 0);
            data.putInt("rickmod_active_shield_color", 0);
        }

        if (player.level().isClientSide) return;

        // 2. Глаз-сканер Рика
        if (CuriosImplantHelper.hasImplant(player, ImplantType.RICK_SCANNER_EYE)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, false, false, false));
            if (player.tickCount % 20 == 0) {
                List<LivingEntity> mobs = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16.0), e -> e != player);
                for (LivingEntity mob : mobs) {
                    mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false, false));
                }
            }
        }

        // 3. Синтетическая печень Рика
        if (CuriosImplantHelper.hasImplant(player, ImplantType.RICK_SYNTHETIC_LIVER)) {
            player.removeEffect(MobEffects.POISON);
            player.removeEffect(MobEffects.CONFUSION);
            player.removeEffect(MobEffects.WITHER);
            player.removeEffect(MobEffects.HUNGER);
        }

        // 4. Нано-регенератор Прайма
        if (CuriosImplantHelper.hasImplant(player, ImplantType.PRIME_NANO_REGENERATOR)) {
            if (player.isOnFire()) player.clearFire();
            if (player.tickCount % 15 == 0 && player.getHealth() < player.getMaxHealth()) {
                player.heal(1.5f);
            }
        }

        // 5. Магнитный манипулятор Злого Морти
        boolean hasMagnet = CuriosImplantHelper.hasImplant(player, ImplantType.EVIL_MORTY_MAGNET_HAND);
        if (hasMagnet) {
            List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(12.0));
            for (ItemEntity item : items) {
                if (!item.hasPickUpDelay()) {
                    Vec3 pull = player.position().add(0, 0.7, 0).subtract(item.position()).normalize().scale(0.35);
                    item.setDeltaMovement(item.getDeltaMovement().add(pull));
                }
            }
        }

        // 6. Пружинные кибер-ноги Рика и Квантовые лезвия Прайма
        boolean hasSpringLegs = CuriosImplantHelper.hasImplant(player, ImplantType.RICK_SPRING_LEGS);
        boolean hasBlades = CuriosImplantHelper.hasImplant(player, ImplantType.PRIME_QUANTUM_BLADES);

        if (hasSpringLegs) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 30, 1, false, false, false));
        }

        updateAttribute(player, Attributes.MOVEMENT_SPEED, SPEED_UUID, "rickmod_spring_speed", hasSpringLegs ? 0.30 : 0.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        updateAttribute(player, Attributes.ATTACK_DAMAGE, DAMAGE_UUID, "rickmod_prime_blades", hasBlades ? 10.0 : 0.0, AttributeModifier.Operation.ADDITION);
        if (ForgeMod.BLOCK_REACH.isPresent()) {
            updateAttribute(player, ForgeMod.BLOCK_REACH.get(), REACH_UUID, "rickmod_morty_reach", hasMagnet ? 2.5 : 0.0, AttributeModifier.Operation.ADDITION);
        }
    }

    private static void updateAttribute(Player player, net.minecraft.world.entity.ai.attributes.Attribute attr, UUID uuid, String name, double value, AttributeModifier.Operation op) {
        var inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(uuid);
        if (value != 0.0) {
            inst.addTransientModifier(new AttributeModifier(uuid, name, value, op));
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        CompoundTag data = player.getPersistentData();
        float shield = data.getFloat("rickmod_shield_hp");
        if (shield > 0) {
            float incoming = event.getAmount();
            float absorbed = Math.min(shield, incoming);
            shield -= absorbed;
            data.putFloat("rickmod_shield_hp", shield);

            boolean isMorty = data.getInt("rickmod_active_shield_color") == 2;
            data.putInt("rickmod_shield_timer", isMorty ? 160 : 240);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(isMorty ? ParticleTypes.WAX_ON : ParticleTypes.HAPPY_VILLAGER,
                        player.getX(), player.getY() + 1.0, player.getZ(), 18, 0.6, 0.8, 0.6, 0.1);
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0f, isMorty ? 0.8f : 1.3f);
            }

            if (isMorty && event.getSource().getEntity() instanceof LivingEntity attacker) {
                Vec3 knock = attacker.position().subtract(player.position()).normalize().scale(1.1);
                attacker.setDeltaMovement(knock.x, 0.4, knock.z);
            }

            float remaining = incoming - absorbed;
            if (remaining <= 0) {
                event.setCanceled(true);
            } else {
                event.setAmount(remaining);
            }
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && CuriosImplantHelper.hasImplant(player, ImplantType.RICK_SPRING_LEGS)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPotionApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player && CuriosImplantHelper.hasImplant(player, ImplantType.RICK_SYNTHETIC_LIVER)) {
            var eff = event.getEffectInstance().getEffect();
            if (eff == MobEffects.POISON || eff == MobEffects.CONFUSION || eff == MobEffects.WITHER || eff == MobEffects.HUNGER) {
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            }
        }
    }

    // Активация способностей имплантов по клавишам (V - плазменная рука, B - взлом Злого Морти, R - фазовый рывок Прайма)
    public static void triggerActiveAbility(ServerPlayer player, int abilityId) {
        ServerLevel level = player.serverLevel();
        CompoundTag data = player.getPersistentData();
        long now = level.getGameTime();

        if (abilityId == 1 && CuriosImplantHelper.hasImplant(player, ImplantType.RICK_PLASMA_ARM)) {
            if (now - data.getLong("rickmod_cd_plasma") < 30) return;
            data.putLong("rickmod_cd_plasma", now);

            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.9f, 1.6f);

            for (int i = 1; i <= 24; i++) {
                Vec3 pos = eye.add(look.scale(i * 0.8));
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y, pos.z, 3, 0.1, 0.1, 0.1, 0.02);
                AABB box = new AABB(pos.x - 0.6, pos.y - 0.6, pos.z - 0.6, pos.x + 0.6, pos.y + 0.6, pos.z + 0.6);
                List<LivingEntity> hits = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                if (!hits.isEmpty()) {
                    for (LivingEntity target : hits) {
                        target.hurt(level.damageSources().playerAttack(player), 12.0f);
                    }
                    break;
                }
            }
        } else if (abilityId == 2 && CuriosImplantHelper.hasImplant(player, ImplantType.EVIL_MORTY_GOLD_EYE)) {
            if (now - data.getLong("rickmod_cd_hack") < 120) return;
            data.putLong("rickmod_cd_hack", now);

            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
            level.sendParticles(ParticleTypes.WAX_ON, player.getX(), player.getY() + 1.5, player.getZ(), 40, 3.0, 1.0, 3.0, 0.1);

            List<Mob> enemies = level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(14.0), e -> e instanceof Enemy || e instanceof GromflomiteSoldierEntity);
            Mob primaryTarget = enemies.isEmpty() ? null : enemies.get(0);
            for (Mob mob : enemies) {
                mob.setTarget(null);
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0));
                if (enemies.size() > 1) {
                    Mob other = enemies.get((enemies.indexOf(mob) + 1) % enemies.size());
                    mob.setTarget(other);
                } else if (primaryTarget != null) {
                    mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 4));
                }
            }
            player.sendSystemMessage(Component.literal("§6[Взлом Злого Морти] §eНейросети врагов в радиусе 14 блоков перехвачены!"));
        } else if (abilityId == 3 && CuriosImplantHelper.hasImplant(player, ImplantType.PRIME_PHASE_ACCELERATOR)) {
            if (now - data.getLong("rickmod_cd_dash") < 40) return;
            data.putLong("rickmod_cd_dash", now);

            Vec3 look = player.getLookAngle();
            Vec3 dash = new Vec3(look.x, Math.max(0.15, look.y * 0.5), look.z).normalize().scale(1.85);
            player.setDeltaMovement(dash);
            player.hurtMarked = true;
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25, 4, false, false));
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.4f);
            level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 35, 0.5, 0.8, 0.5, 0.2);
        }
    }
}
