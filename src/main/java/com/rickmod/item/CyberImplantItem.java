package com.rickmod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CyberImplantItem extends Item {
    private final String slotType;
    private final String abilityHint;

    public CyberImplantItem(Rarity rarity, String slotType, String abilityHint) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.slotType = slotType;
        this.abilityHint = abilityHint;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Слот Curios: " + slotType).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal(abilityHint).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Сохраняется при перерождении в Проекте «Феникс»").withStyle(ChatFormatting.DARK_GREEN));
    }
}
