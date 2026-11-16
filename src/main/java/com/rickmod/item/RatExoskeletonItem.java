package com.rickmod.item;

import com.rickmod.pickle.PickleStateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RatExoskeletonItem extends ArmorItem {
    public RatExoskeletonItem(Properties properties) {
        super(ArmorMaterials.IRON, Type.CHESTPLATE, properties);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (armorType != EquipmentSlot.CHEST) return false;
        if (entity instanceof net.minecraft.world.entity.player.Player p) {
            return PickleStateManager.isPickle(p);
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Надевается только в форме Огурчика Рика").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Дает силу, скорость, защиту и руки").withStyle(ChatFormatting.GRAY));
    }
}
