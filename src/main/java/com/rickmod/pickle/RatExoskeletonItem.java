package com.rickmod.pickle;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RatExoskeletonItem extends ArmorItem {
    public static final ArmorMaterial RAT_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(Type type) { return 550; }
        @Override public int getDefenseForType(Type type) { return 8; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_LEATHER; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.LEATHER, Items.IRON_INGOT); }
        @Override public String getName() { return "rickmod:rat_exoskeleton"; }
        @Override public float getToughness() { return 2.0f; }
        @Override public float getKnockbackResistance() { return 0.2f; }
    };

    public RatExoskeletonItem(Properties props) {
        super(RAT_MATERIAL, Type.CHESTPLATE, props);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (entity instanceof Player player) {
            return armorType == EquipmentSlot.CHEST && PickleStateManager.isPickle(player);
        }
        return false;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (!level.isClientSide && player.getItemBySlot(EquipmentSlot.CHEST) == stack) {
            if (!PickleStateManager.isPickle(player)) {
                player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                if (!player.getInventory().add(stack)) player.drop(stack, false);
                return;
            }
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Боевой экзоскелет из крыс и мышей.").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Надевается ТОЛЬКО в форме Огурчика Рика!").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Дает Силу III, Скорость II, Прыгучесть II и броню.").withStyle(ChatFormatting.GRAY));
    }
}
