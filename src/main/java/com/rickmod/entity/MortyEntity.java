package com.rickmod.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MortyEntity extends PathfinderMob {
    private static final String[] LINES = new String[] {
            "§e<Морти> §fО боже, Рик, ты уверен, что эта штука не взорвется?!",
            "§e<Морти> §fЭй, привет! Ты тоже из измерения C-137 или из какого-то другого?",
            "§e<Морти> §fВ Цитадели столько разных Риков, что у меня уже голова кругом...",
            "§e<Морти> §fТолько не ходи в Измерение Блендеров без брони, серьезно!",
            "§e<Морти> §fРик сказал мне протереть все портальные кристаллы в этом секторе."
    };

    public MortyEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, RickEntity.class, 10.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            this.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.25F);
            String msg = LINES[this.random.nextInt(LINES.length)];
            player.sendSystemMessage(Component.literal(msg));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
