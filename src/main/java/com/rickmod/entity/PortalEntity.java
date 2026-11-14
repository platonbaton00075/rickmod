package com.rickmod.entity;

import com.rickmod.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class PortalEntity extends Entity {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(PortalEntity.class, EntityDataSerializers.INT);
    private int lifeTicks = 400; // 20 секунд
    private String targetDim = "rickmod:citadel_of_ricks";
    private BlockPos targetPos = null;

    public PortalEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public PortalEntity(Level level, double x, double y, double z, float yaw, int color, String targetDim) {
        this(ModEntities.PORTAL.get(), level);
        this.setPos(x, y, z);
        this.setYRot(yaw);
        this.entityData.set(COLOR, color);
        this.targetDim = targetDim;
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setTargetCoords(String dim, BlockPos pos) {
        this.targetDim = dim;
        this.targetPos = pos;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0x39FF14);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            int c = getColor();
            float r = ((c >> 16) & 0xFF) / 255f;
            float g = ((c >> 8) & 0xFF) / 255f;
            float b = (c & 0xFF) / 255f;
            for (int i = 0; i < 6; i++) {
                double angle = (this.tickCount * 0.25) + (i * Math.PI / 3.0);
                double rad = 0.75 + Math.sin(this.tickCount * 0.15) * 0.15;
                double px = this.getX() + Math.cos(angle) * Math.cos(Math.toRadians(this.getYRot())) * rad;
                double py = this.getY() + 1.2 + Math.sin(angle) * (rad * 1.35);
                double pz = this.getZ() + Math.cos(angle) * Math.sin(Math.toRadians(this.getYRot())) * rad;
                this.level().addParticle(new DustParticleOptions(new Vector3f(r, g, b), 1.3f), px, py, pz, 0, 0.01, 0);
            }
            return;
        }

        if (--this.lifeTicks <= 0) {
            this.discard();
            return;
        }

        AABB box = this.getBoundingBox().inflate(0.35);
        List<Entity> list = this.level().getEntities(this, box, e -> !(e instanceof PortalEntity) && e.canChangeDimensions());
        for (Entity entity : list) {
            if (entity.getPersistentData().getLong("RickPortalCooldown") > this.level().getGameTime()) continue;
            teleportEntity(entity);
        }
    }

    private void teleportEntity(Entity entity) {
        if (!(this.level() instanceof ServerLevel currentServerLevel)) return;
        ResourceLocation dimLoc = ResourceLocation.tryParse(this.targetDim);
        if (dimLoc == null) dimLoc = new ResourceLocation("minecraft", "overworld");

        ResourceKey<Level> destKey = ResourceKey.create(Registries.DIMENSION, dimLoc);
        ServerLevel destLevel = currentServerLevel.getServer().getLevel(destKey);
        if (destLevel == null) destLevel = currentServerLevel.getServer().overworld();

        BlockPos dest = this.targetPos != null ? this.targetPos : new BlockPos(0, 105, 0);
        if (this.targetPos == null && destLevel.dimension().location().toString().equals("minecraft:overworld")) {
            dest = destLevel.getSharedSpawnPos();
        }

        // Создаем безопасную площадку под ногами при прибытии
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos floor = dest.offset(dx, -1, dz);
                if (destLevel.isEmptyBlock(floor)) {
                    destLevel.setBlockAndUpdate(floor, Blocks.OBSIDIAN.defaultBlockState());
                }
            }
        }
        destLevel.setBlockAndUpdate(dest, Blocks.AIR.defaultBlockState());
        destLevel.setBlockAndUpdate(dest.above(), Blocks.AIR.defaultBlockState());

        entity.getPersistentData().putLong("RickPortalCooldown", destLevel.getGameTime() + 60);
        destLevel.playSound(null, dest, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (entity instanceof ServerPlayer player) {
            player.teleportTo(destLevel, dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5, player.getYRot(), player.getXRot());
        } else {
            if (entity.level() != destLevel) {
                Entity moved = entity.changeDimension(destLevel);
                if (moved != null) moved.teleportTo(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5);
            } else {
                entity.teleportTo(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5);
            }
        }

        // Ответный портал на точке выхода
        PortalEntity exitPortal = new PortalEntity(destLevel, dest.getX() + 0.5, dest.getY(), dest.getZ() + 2.5, entity.getYRot() + 180f, getColor(), currentServerLevel.dimension().location().toString());
        exitPortal.setTargetCoords(currentServerLevel.dimension().location().toString(), this.blockPosition());
        destLevel.addFreshEntity(exitPortal);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.lifeTicks = tag.getInt("Life");
        this.entityData.set(COLOR, tag.getInt("Color"));
        this.targetDim = tag.getString("TargetDim");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", this.lifeTicks);
        tag.putInt("Color", getColor());
        tag.putString("TargetDim", this.targetDim);
    }
}
