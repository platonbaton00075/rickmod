package com.rickmod.entity;

import com.rickmod.registry.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GromflomiteSoldierEntity extends PathfinderMob {
    private int shootCooldown = 0;

    public GromflomiteSoldierEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Attacks everyone except fellow Federation soldiers
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                target -> !(target instanceof GromflomiteSoldierEntity)));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FEDERATION_BLASTER.get()));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable net.minecraft.nbt.CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return data;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (shootCooldown > 0) shootCooldown--;
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && this.hasLineOfSight(target)) {
                double distSq = this.distanceToSqr(target);
                if (distSq > 16.0D && distSq < 625.0D && shootCooldown == 0) {
                    shootCooldown = 35;
                    this.swing(InteractionHand.MAIN_HAND);
                    serverLevel.playSound(null, this.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 0.8F, 1.6F);

                    Vec3 start = this.getEyePosition();
                    Vec3 end = target.getEyePosition();
                    Vec3 dir = end.subtract(start).normalize();
                    double dist = start.distanceTo(end);

                    DustParticleOptions redBeam = new DustParticleOptions(new Vector3f(1.0F, 0.1F, 0.2F), 1.2F);
                    for (double d = 0.5; d < dist; d += 0.5) {
                        Vec3 p = start.add(dir.scale(d));
                        serverLevel.sendParticles(redBeam, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
                    }
                    target.hurt(this.damageSources().mobAttack(this), 7.5F);
                }
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        int cells = 1 + this.random.nextInt(3 + looting);
        this.spawnAtLocation(new ItemStack(ModItems.PLASMA_CELL.get(), cells));
        if (this.random.nextFloat() < 0.25F + (looting * 0.05F)) {
            ItemStack dropBlaster = new ItemStack(ModItems.FEDERATION_BLASTER.get());
            FederationBlasterItem.setAmmo(dropBlaster, 5 + this.random.nextInt(15));
            this.spawnAtLocation(dropBlaster);
        }
    }
}
