package com.rickmod.pickle;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PickleSerumItem extends Item {
    private final boolean isAntidote;

    public PickleSerumItem(boolean isAntidote, Properties props) {
        super(props.stacksTo(16));
        this.isAntidote = isAntidote;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !level.isClientSide) {
            if (!isAntidote) {
                PickleStateManager.setPickle(player, true);
                level.playSound(null, player.blockPosition(), SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0f, 1.4f);
                player.displayClientMessage(Component.literal("Я ОГУРЧИК РИК! (Нужен антидот, чтобы вернуться)").withStyle(ChatFormatting.GREEN), true);
            } else {
                PickleStateManager.setPickle(player, false);
                level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.displayClientMessage(Component.literal("Вы вернули себе человеческий облик!").withStyle(ChatFormatting.AQUA), true);
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 24;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!isAntidote) {
            tooltip.add(Component.literal("Превращает вас в Огурчика Рика (рост 0.85 блока).").withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.literal("Возврат только второй сывороткой-антидотом!").withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.literal("Возвращает нормальное человеческое тело.").withStyle(ChatFormatting.AQUA));
        }
    }
}
