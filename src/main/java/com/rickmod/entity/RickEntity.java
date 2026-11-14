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

public class RickEntity extends PathfinderMob {
    private static final String[] LINES = new String[] {
            "§a<Рик> §fВабба-лабба-даб-дабс! Не трогай квантовый стабилизатор!",
            "§a<Рик> §f*Отрыжка* Я занят, Морти, мы перенастраиваем Центральную Конечную Кривую.",
            "§a<Рик> §fДобро пожаловать в Цитадель. Постарайся ничего здесь не взорвать.",
            "§a<Рик> §fЗнаешь, сколько измерений мне пришлось перебрать, чтобы найти нормальный кофе?",
            "§a<Рик> §fСовет Риков снова заседает в центральной башне. Скукотища."
    };

    public RickEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.35D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, MortyEntity.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            this.playSound(SoundEvents.VILLAGER_AMBIENT, 1.0F, 0.85F);
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
