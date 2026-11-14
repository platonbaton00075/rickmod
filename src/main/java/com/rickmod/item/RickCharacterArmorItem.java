package com.rickmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RickCharacterArmorItem extends ArmorItem {
    private final String characterId;

    public RickCharacterArmorItem(ArmorMaterial material, Type type, String characterId, Properties properties) {
        super(material, type, properties);
        this.characterId = characterId;
    }

    public String getCharacterId() {
        return characterId;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (!level.isClientSide && player.tickCount % 40 == 0 && hasFullSet(player)) {
            if ("c137".equals(characterId)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 80, 1, true, false));
            } else if ("evil_morty".equals(characterId)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 1, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, false));
            } else if ("prime".equals(characterId)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, 1, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 1, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 1, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 80, 0, true, false));
            }
        }
    }

    public static boolean hasFullSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        if (head.getItem() instanceof RickCharacterArmorItem a &&
            chest.getItem() instanceof RickCharacterArmorItem b &&
            legs.getItem() instanceof RickCharacterArmorItem c &&
            feet.getItem() instanceof RickCharacterArmorItem d) {
            return a.characterId.equals(b.characterId) && a.characterId.equals(c.characterId) && a.characterId.equals(d.characterId);
        }
        return false;
    }
}
