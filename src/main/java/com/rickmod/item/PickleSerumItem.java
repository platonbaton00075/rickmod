package com.rickmod.item;

import com.rickmod.pickle.PickleStateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PickleSerumItem extends Item {
    public PickleSerumItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 24;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PickleStateManager.setPickleState(serverPlayer, true);
            level.playSound(null, serverPlayer.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.3F);
            serverPlayer.sendSystemMessage(Component.literal("Я ОГУРЧИК РИК!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        }
        if (!(entity instanceof Player p && p.getAbilities().instabuild)) {
            stack.shrink(1);
            if (entity instanceof Player p) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!p.getInventory().add(bottle)) p.drop(bottle, false);
            }
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Превращает в Огурчика Рика (рост 0.85 блока)").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Возврат в человека только Антидотом").withStyle(ChatFormatting.GRAY));
    }
}
