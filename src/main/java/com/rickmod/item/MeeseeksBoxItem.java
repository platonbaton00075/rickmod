package com.rickmod.item;

import com.rickmod.entity.MeeseeksEntity;
import com.rickmod.init.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeeseeksBoxItem extends Item {
    public MeeseeksBoxItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            Vec3 look = player.getLookAngle();
            double spawnX = player.getX() + look.x * 2.0;
            double spawnY = player.getY() + 0.2;
            double spawnZ = player.getZ() + look.z * 2.0;

            MeeseeksEntity meeseeks = new MeeseeksEntity(ModEntities.MEESEEKS.get(), level);
            meeseeks.moveTo(spawnX, spawnY, spawnZ, player.getYRot() + 180.0F, 0.0F);
            meeseeks.setOwnerUUID(player.getUUID());
            serverLevel.addFreshEntity(meeseeks);

            serverLevel.sendParticles(ParticleTypes.CLOUD, spawnX, spawnY + 1.0, spawnZ, 25, 0.4, 0.6, 0.4, 0.05);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, spawnX, spawnY + 1.0, spawnZ, 15, 0.3, 0.5, 0.3, 0.03);
            level.playSound(null, spawnX, spawnY, spawnZ, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.3F);

            player.sendSystemMessage(Component.literal("[Мистер Мисикс] ")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                    .append(Component.literal("О-о-о, да! Я Мистер Мисикс, посмотрите на меня! Напиши в чат «Мисикс, <задача>»!")
                            .withStyle(ChatFormatting.WHITE)));

            player.getCooldowns().addCooldown(this, 60);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("ПКМ: Вызвать Мистера Мисикса").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("Команды в чат: «Мисикс, руби дерево / копай руду / убей врагов / ко мне / хватит»").withStyle(ChatFormatting.GRAY));
    }
}
