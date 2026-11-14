package com.rickmod.implant;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CyberImplantItem extends Item {
    private final ImplantType implantType;

    public CyberImplantItem(ImplantType implantType, Properties properties) {
        super(properties.stacksTo(1));
        this.implantType = implantType;
    }

    public ImplantType getImplantType() {
        return implantType;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.rickmod.implant.slot." + implantType.slot).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.rickmod.implant." + implantType.id).withStyle(ChatFormatting.GRAY));
        if (implantType.maxShield > 0) {
            tooltip.add(Component.literal("Щит: " + (int) implantType.maxShield + " ед. | Перезарядка: " + (implantType.rechargeTicks / 20) + " сек.").withStyle(ChatFormatting.GREEN));
        }
    }
}
