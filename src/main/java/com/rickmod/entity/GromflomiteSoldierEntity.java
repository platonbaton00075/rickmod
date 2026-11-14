package com.rickmod.entity;

import com.rickmod.init.ModItems;
import com.rickmod.init.ModSounds;
import com.rickmod.item.FederationBlasterItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class GromflomiteSoldierEntity extends Monster implements RangedAttackMob {

    public GromflomiteSoldierEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.1D, 35, 20.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, LivingEntity.class, 12.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                target -> !(target instanceof GromflomiteSoldierEntity)));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FEDERATION_BLASTER.get()));
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.5) - this.getY(0.6);
        double dz = target.getZ() - this.getZ();
        SmallFireball bolt = new SmallFireball(this.level(), this, dx, dy, dz);
        bolt.setPos(this.getX(), this.getY(0.6), this.getZ());
        this.level().addFreshEntity(bolt);
        this.level().playSound(null, this.blockPosition(), ModSounds.BLASTER_FIRE.get(), SoundSource.HOSTILE, 0.9F, 1.0F);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        int cells = 1 + this.random.nextInt(3 + looting);
        this.spawnAtLocation(new ItemStack(ModItems.PLASMA_CELL.get(), cells));
        if (this.random.nextFloat() < 0.25F) {
            ItemStack dropBlaster = new ItemStack(ModItems.FEDERATION_BLASTER.get());
            FederationBlasterItem.setAmmo(dropBlaster, 5 + this.random.nextInt(15));
            this.spawnAtLocation(dropBlaster);
        }
    }
}
