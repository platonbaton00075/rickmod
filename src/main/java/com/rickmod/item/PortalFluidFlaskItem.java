package com.rickmod.item;

import com.rickmod.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PortalFluidFlaskItem extends Item {
    public PortalFluidFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack flask = player.getItemInHand(hand);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack other = player.getItemInHand(otherHand);

        if (other.getItem() instanceof PortalGunItem gun) {
            if (gun.isInfinite()) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal("Эта пушка не требует заправки!").withStyle(ChatFormatting.GREEN), true);
                }
                return InteractionResultHolder.pass(flask);
            }
            int cur = PortalGunItem.getCharges(other);
            int max = gun.getMaxCharges();
            if (cur < max) {
                if (!level.isClientSide) {
                    PortalGunItem.setCharges(other, Math.min(max, cur + 50));
                    if (!player.getAbilities().instabuild) {
                        flask.shrink(1);
                    }
                    level.playSound(null, player.blockPosition(), ModSounds.PORTAL_OPEN.get(), SoundSource.PLAYERS, 0.8F, 1.4F);
                    player.displayClientMessage(Component.literal("Портальная пушка заправлена!").withStyle(ChatFormatting.GREEN), true);
                }
                return InteractionResultHolder.sidedSuccess(flask, level.isClientSide);
            }
        }
        return InteractionResultHolder.pass(flask);
    }
}
