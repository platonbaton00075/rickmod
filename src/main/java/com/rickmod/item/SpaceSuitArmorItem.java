package com.rickmod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpaceSuitArmorItem extends ArmorItem {
    public SpaceSuitArmorItem(Type type, Properties properties) {
        super(ArmorMaterials.DIAMOND, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Полный сет дает защиту и свободный полет в космосе Цитадели").withStyle(ChatFormatting.AQUA));
    }
}
