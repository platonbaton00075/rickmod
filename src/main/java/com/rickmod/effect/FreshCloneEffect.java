package com.rickmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FreshCloneEffect extends MobEffect {
    public FreshCloneEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FFCC);
        this.addAttributeModifier(Attributes.MAX_HEALTH, "91AEAA56-376B-4498-935B-2F7F68070635", 16.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            entity.removeEffect(MobEffects.CONFUSION);
            entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            entity.removeEffect(MobEffects.WEAKNESS);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
