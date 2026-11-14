package com.rickmod.item;

import com.rickmod.RickMod;
import com.rickmod.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class SpaceArmorMaterial implements ArmorMaterial {
    public static final SpaceArmorMaterial INSTANCE = new SpaceArmorMaterial();
    private static final int[] DURABILITY = new int[]{420, 610, 570, 490};
    private static final int[] DEFENSE = new int[]{3, 8, 6, 3};

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.getSlot().getIndex()];
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 18;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.PORTAL_CORE.get());
    }

    @Override
    public String getName() {
        return RickMod.MOD_ID + ":space_suit";
    }

    @Override
    public float getToughness() {
        return 3.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1F;
    }
}
