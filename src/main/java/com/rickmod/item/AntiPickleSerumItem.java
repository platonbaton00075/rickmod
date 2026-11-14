package com.rickmod.item;

import com.rickmod.pickle.PickleStateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AntiPickleSerumItem extends Item {
    public AntiPickleSerumItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (!PickleStateManager.isPickle(serverPlayer)) {
                serverPlayer.displayClientMessage(Component.literal("Ты и так в человеческом теле!").withStyle(ChatFormatting.YELLOW), true);
                return InteractionResultHolder.pass(stack);
            }
            PickleStateManager.setPickle(serverPlayer, false);
            level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.9F, 1.3F);
            serverPlayer.displayClientMessage(Component.literal("Человеческая форма восстановлена!").withStyle(ChatFormatting.AQUA), true);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
