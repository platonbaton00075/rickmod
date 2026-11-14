package com.rickmod.entity;

import com.rickmod.init.ModEntities;
import com.rickmod.init.ModSounds;
import com.rickmod.item.PortalGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.ITeleporter;

import java.util.List;
import java.util.function.Function;

public class PortalEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_PORTAL_TYPE =
            SynchedEntityData.defineId(PortalEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LIFE_LEFT =
            SynchedEntityData.defineId(PortalEntity.class, EntityDataSerializers.INT);

    private ResourceKey<Level> destDimension = Level.OVERWORLD;
    private BlockPos destPos = new BlockPos(0, 82, 0);
    private boolean isReturnPortal = false;

    public PortalEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_PORTAL_TYPE, 0);
        this.entityData.define(DATA_LIFE_LEFT, 400);
    }

    public void setGunType(PortalGunItem.GunType type) {
        int id = switch (type) {
            case PRIME -> 1;
            case EVIL_MORTY -> 2;
            default -> 0;
        };
        this.entityData.set(DATA_PORTAL_TYPE, id);
    }

    public PortalGunItem.GunType getGunType() {
        return switch (this.entityData.get(DATA_PORTAL_TYPE)) {
            case 1 -> PortalGunItem.GunType.PRIME;
            case 2 -> PortalGunItem.GunType.EVIL_MORTY;
            default -> PortalGunItem.GunType.RICK;
        };
    }

    public int getPortalType() {
        return this.entityData.get(DATA_PORTAL_TYPE);
    }

    public int getLifeLeft() {
        return this.entityData.get(DATA_LIFE_LEFT);
    }

    public void setDestination(ResourceKey<Level> dim, BlockPos pos) {
        this.destDimension = dim;
        this.destPos = pos;
    }

    public void setReturnPortal(boolean ret) {
        this.isReturnPortal = ret;
    }

    @Override
    public void tick() {
        super.tick();
        int life = this.entityData.get(DATA_LIFE_LEFT);
        if (life <= 0) {
            this.discard();
            return;
        }
        this.entityData.set(DATA_LIFE_LEFT, life - 1);

        if (this.level().isClientSide) {
            if (this.random.nextInt(2) == 0) {
                double ox = (this.random.nextDouble() - 0.5) * 1.4;
                double oy = this.random.nextDouble() * 2.2;
                double oz = (this.random.nextDouble() - 0.5) * 1.4;
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() + ox, this.getY() + oy, this.getZ() + oz, 0, 0.03, 0);
            }
            return;
        }

        if (life == 399) {
            this.level().playSound(null, this.blockPosition(), ModSounds.PORTAL_OPEN.get(), SoundSource.BLOCKS, 0.9F, 1.0F);
        }

        AABB box = this.getBoundingBox().inflate(0.35);
        List<Entity> entities = this.level().getEntities(this, box, e -> !(e instanceof PortalEntity) && e.getPortalCooldown() <= 0);
        for (Entity target : entities) {
            teleportEntity(target);
        }
    }

    private void teleportEntity(Entity target) {
        if (!(this.level() instanceof ServerLevel currentServerLevel)) return;
        ServerLevel targetLevel = currentServerLevel.getServer().getLevel(this.destDimension);
        if (targetLevel == null) {
            targetLevel = currentServerLevel.getServer().overworld();
        }

        BlockPos safePos = ensureSafeLanding(targetLevel, this.destPos);
        ResourceKey<Level> backDim = currentServerLevel.dimension();
        BlockPos backPos = this.blockPosition();
        PortalGunItem.GunType gunType = this.getGunType();
        boolean wasReturn = this.isReturnPortal;

        target.setPortalCooldown(60);

        if (targetLevel == currentServerLevel) {
            target.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
            spawnReturnPortal(targetLevel, safePos, backDim, backPos, gunType, wasReturn);
        } else {
            final ServerLevel destLvl = targetLevel;
            Entity moved = target.changeDimension(destLvl, new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                    Entity e = repositionEntity.apply(false);
                    if (e != null) {
                        e.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
                    }
                    return e;
                }
            });
            if (moved != null) {
                moved.setPortalCooldown(60);
            }
            spawnReturnPortal(destLvl, safePos, backDim, backPos, gunType, wasReturn);
        }

        targetLevel.playSound(null, safePos, ModSounds.PORTAL_TRAVEL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void spawnReturnPortal(ServerLevel level, BlockPos landing, ResourceKey<Level> backDim, BlockPos backPos, PortalGunItem.GunType type, boolean wasReturn) {
        if (wasReturn) return;
        PortalEntity ret = new PortalEntity(ModEntities.PORTAL.get(), level);
        ret.setPos(landing.getX() + 2.5, landing.getY(), landing.getZ() + 0.5);
        ret.setGunType(type);
        ret.setDestination(backDim, backPos);
        ret.setReturnPortal(true);
        level.addFreshEntity(ret);
    }

    private BlockPos ensureSafeLanding(ServerLevel level, BlockPos desired) {
        BlockPos pos = new BlockPos(desired.getX(), Math.max(level.getMinBuildHeight() + 5, Math.min(level.getMaxBuildHeight() - 5, desired.getY())), desired.getZ());
        if (level.getBlockState(pos.below()).isAir()) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    level.setBlockAndUpdate(pos.below().offset(dx, 0, dz), Blocks.OBSIDIAN.defaultBlockState());
                }
            }
        }
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
        return pos;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(DATA_PORTAL_TYPE, tag.getInt("PortalType"));
        this.entityData.set(DATA_LIFE_LEFT, tag.getInt("LifeLeft"));
        String dimStr = tag.getString("DestDim");
        if (!dimStr.isEmpty()) {
            this.destDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimStr));
        }
        this.destPos = new BlockPos(tag.getInt("DestX"), tag.getInt("DestY"), tag.getInt("DestZ"));
        this.isReturnPortal = tag.getBoolean("IsReturn");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("PortalType", this.entityData.get(DATA_PORTAL_TYPE));
        tag.putInt("LifeLeft", this.entityData.get(DATA_LIFE_LEFT));
        tag.putString("DestDim", this.destDimension.location().toString());
        tag.putInt("DestX", this.destPos.getX());
        tag.putInt("DestY", this.destPos.getY());
        tag.putInt("DestZ", this.destPos.getZ());
        tag.putBoolean("IsReturn", this.isReturnPortal);
    }
}
