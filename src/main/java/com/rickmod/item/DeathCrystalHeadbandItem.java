package com.rickmod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class DeathCrystalHeadbandItem extends ArmorItem {

    private static final ArmorMaterial HEADBAND_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(Type type) { return 480; }
        @Override public int getDefenseForType(Type type) { return 3; }
        @Override public int getEnchantmentValue() { return 18; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_GOLD; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "rickmod:death_crystal_band"; }
        @Override public float getToughness() { return 1.0f; }
        @Override public float getKnockbackResistance() { return 0.0f; }
    };

    public DeathCrystalHeadbandItem(Properties props) {
        super(HEADBAND_MATERIAL, Type.HELMET, props);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide && level.getGameTime() % 20 == 0) {
            AABB area = player.getBoundingBox().inflate(18.0);
            List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e instanceof Enemy && e.isAlive());
            for (LivingEntity enemy : enemies) {
                enemy.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
            }
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "rickmod:textures/models/armor/death_crystal_band_layer_1.png";
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Обруч с вживленным Кристаллом Смерти (как у Морти)").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("• Освобождает руки, сохраняя предвидение и спасение от смерти").withStyle(ChatFormatting.GRAY));
    }
}
