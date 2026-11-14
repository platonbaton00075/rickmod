package com.rickmod.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MeeseeksEntity extends PathfinderMob {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(MeeseeksEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> TASK_MODE =
            SynchedEntityData.defineId(MeeseeksEntity.class, EntityDataSerializers.STRING);

    private final SimpleContainer lootInventory = new SimpleContainer(27);
    private int lifeTicks = 1200; // 60 секунд
    private int actionCooldown = 0;

    public MeeseeksEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("Мистер Мисикс").withStyle(ChatFormatting.AQUA));
        this.setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(TASK_MODE, "FOLLOW");
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, false,
                entity -> "FIGHT".equals(getTaskMode()) || "FOLLOW".equals(getTaskMode())));
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Nullable
    public Player getOwner() {
        UUID uuid = getOwnerUUID();
        return uuid != null ? this.level().getPlayerByUUID(uuid) : null;
    }

    public String getTaskMode() {
        return this.entityData.get(TASK_MODE);
    }

    public void setTaskMode(String mode) {
        this.entityData.set(TASK_MODE, mode);
    }

    public void handleRussianCommand(Player sender, String rawText) {
        String text = rawText.toLowerCase();
        if (text.contains("хватит") || text.contains("исчез") || text.contains("пока") || text.contains("стоп") || text.contains("уйди")) {
            sayTo(sender, "Задача выполнена! Прощайте!");
            despawnWithLoot();
        } else if (text.contains("дерев") || text.contains("брев") || text.contains("брёв") || text.contains("руби") || text.contains("лес")) {
            setTaskMode("CHOP_WOOD");
            sayTo(sender, "О-о-о, можно сделать! Пошёл рубить деревья!");
        } else if (text.contains("руд") || text.contains("копа") || text.contains("шахт") || text.contains("алмаз") || text.contains("желез") || text.contains("камен")) {
            setTaskMode("MINE_ORE");
            sayTo(sender, "О-о-о, да! Ищу и добываю руду!");
        } else if (text.contains("убей") || text.contains("атак") || text.contains("защи") || text.contains("монстр") || text.contains("зомби") || text.contains("бей")) {
            setTaskMode("FIGHT");
            sayTo(sender, "С удовольствием! Уничтожаю врагов вокруг!");
        } else if (text.contains("ко мне") || text.contains("сюда") || text.contains("следуй") || text.contains("рядом") || text.contains("отдай")) {
            setTaskMode("FOLLOW");
            giveCollectedLoot(sender);
            sayTo(sender, "Бегу к вам! Вот всё, что я собрал!");
        } else {
            sayTo(sender, "Не совсем понял, босс! Скажи: «руби дерево», «копай руду», «убей врагов», «ко мне» или «хватит»!");
        }
    }

    private void sayTo(Player player, String msg) {
        player.sendSystemMessage(Component.literal("[Мистер Мисикс] ")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                .append(Component.literal(msg).withStyle(ChatFormatting.WHITE)));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        lifeTicks--;
        if (lifeTicks <= 0) {
            Player owner = getOwner();
            if (owner != null) {
                sayTo(owner, "Время вышло! Существование — это боль! Прощайте!");
            }
            despawnWithLoot();
            return;
        }

        // Подбор предметов вокруг
        List<ItemEntity> items = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(2.5D));
        for (ItemEntity itemEntity : items) {
            if (itemEntity.isAlive() && !itemEntity.getItem().isEmpty()) {
                ItemStack remainder = lootInventory.addItem(itemEntity.getItem());
                if (remainder.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remainder);
                }
            }
        }

        if (actionCooldown > 0) {
            actionCooldown--;
            return;
        }

        String mode = getTaskMode();
        Player owner = getOwner();

        if ("FOLLOW".equals(mode) && owner != null) {
            double dist = this.distanceToSqr(owner);
            if (dist > 9.0D) {
                this.getNavigation().moveTo(owner, 1.3D);
            } else if (!lootInventory.isEmpty()) {
                giveCollectedLoot(owner);
            }
        } else if ("CHOP_WOOD".equals(mode)) {
            BlockPos targetLog = findNearestBlock(true, 16);
            if (targetLog != null) {
                if (this.blockPosition().distSqr(targetLog) > 6.0D) {
                    this.getNavigation().moveTo(targetLog.getX() + 0.5, targetLog.getY(), targetLog.getZ() + 0.5, 1.25D);
                } else {
                    breakAndCollect(targetLog);
                    actionCooldown = 10;
                }
            } else if (owner != null) {
                this.getNavigation().moveTo(owner, 1.2D);
            }
        } else if ("MINE_ORE".equals(mode)) {
            BlockPos targetOre = findNearestBlock(false, 16);
            if (targetOre != null) {
                if (this.blockPosition().distSqr(targetOre) > 6.0D) {
                    this.getNavigation().moveTo(targetOre.getX() + 0.5, targetOre.getY(), targetOre.getZ() + 0.5, 1.25D);
                } else {
                    breakAndCollect(targetOre);
                    actionCooldown = 12;
                }
            } else if (owner != null) {
                this.getNavigation().moveTo(owner, 1.2D);
            }
        }
    }

    @Nullable
    private BlockPos findNearestBlock(boolean logs, int radius) {
        BlockPos origin = this.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -6; dy <= 10; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState state = this.level().getBlockState(pos);
                    boolean match = logs ? state.is(BlockTags.LOGS) : state.is(Tags.Blocks.ORES);
                    if (match) {
                        double d = origin.distSqr(pos);
                        if (d < bestDist) {
                            bestDist = d;
                            best = pos;
                        }
                    }
                }
            }
        }
        return best;
    }

    private void breakAndCollect(BlockPos pos) {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockState state = serverLevel.getBlockState(pos);
            List<ItemStack> drops = net.minecraft.world.level.block.Block.getDrops(state, serverLevel, pos, null);
            for (ItemStack drop : drops) {
                ItemStack rem = lootInventory.addItem(drop);
                if (!rem.isEmpty()) {
                    popResource(serverLevel, pos, rem);
                }
            }
            serverLevel.destroyBlock(pos, false);
            this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
    }

    private static void popResource(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }

    public void giveCollectedLoot(Player player) {
        for (int i = 0; i < lootInventory.getContainerSize(); i++) {
            ItemStack stack = lootInventory.getItem(i);
            if (!stack.isEmpty()) {
                if (!player.getInventory().add(stack.copy())) {
                    player.drop(stack.copy(), false);
                }
                lootInventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    public void despawnWithLoot() {
        Player owner = getOwner();
        if (owner != null) {
            giveCollectedLoot(owner);
        } else if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < lootInventory.getContainerSize(); i++) {
                ItemStack stack = lootInventory.getItem(i);
                if (!stack.isEmpty()) {
                    popResource(serverLevel, this.blockPosition(), stack);
                }
            }
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 1.0, getZ(), 30, 0.4, 0.6, 0.4, 0.06);
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.NEUTRAL, 0.8F, 1.5F);
        }
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTicks", lifeTicks);
        tag.putString("TaskMode", getTaskMode());
        if (getOwnerUUID() != null) {
            tag.putUUID("OwnerUUID", getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("LifeTicks")) lifeTicks = tag.getInt("LifeTicks");
        if (tag.contains("TaskMode")) setTaskMode(tag.getString("TaskMode"));
        if (tag.hasUUID("OwnerUUID")) setOwnerUUID(tag.getUUID("OwnerUUID"));
    }
}
