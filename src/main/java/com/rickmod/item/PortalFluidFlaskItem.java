package com.rickmod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PortalFluidFlaskItem extends Item {
    public PortalFluidFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack flask = player.getItemInHand(hand);
        ItemStack other = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (other.getItem() instanceof PortalGunItem) {
            int cur = PortalGunItem.getFluid(other);
            int max = PortalGunItem.getMaxFluid(other);
            if (max > 0 && cur < max) {
                if (!level.isClientSide) {
                    PortalGunItem.setFluid(other, Math.min(max, cur + 50));
                    if (!player.getAbilities().instabuild) {
                        flask.shrink(1);
                        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                        if (!player.getInventory().add(bottle)) player.drop(bottle, false);
                    }
                    level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0F, 1.2F);
                    player.displayClientMessage(Component.literal("Портальная пушка заправлена (+50)!").withStyle(ChatFormatting.GREEN), true);
                }
                return InteractionResultHolder.sidedSuccess(flask, level.isClientSide());
            }
        }
        return InteractionResultHolder.pass(flask);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Возьмите пушку в другую руку и нажмите ПКМ для заправки (+50)").withStyle(ChatFormatting.GREEN));
    }
}
