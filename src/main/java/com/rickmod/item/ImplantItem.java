package com.rickmod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImplantItem extends Item {
    private final String slotType;
    private final String descKey;

    public ImplantItem(String slotType, String descKey, Properties properties) {
        super(properties);
        this.slotType = slotType;
        this.descKey = descKey;
    }

    public String getSlotType() {
        return slotType;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Кибер-имплант (Слот Curios: " + slotType + ")").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
    }
}
