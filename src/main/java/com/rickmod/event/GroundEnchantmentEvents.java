package com.rickmod.event;

import com.rickmod.RickMod;
import com.rickmod.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID)
public class GroundEnchantmentEvents {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;
        if (event.level.getGameTime() % 10 != 0) return;

        ServerLevel level = (ServerLevel) event.level;
        for (var player : level.players()) {
            AABB area = player.getBoundingBox().inflate(6.0);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area, e -> e.isAlive() && !e.getItem().isEmpty());

            for (ItemEntity bookEntity : items) {
                ItemStack bookStack = bookEntity.getItem();
                if (!bookStack.is(Items.ENCHANTED_BOOK)) continue;

                Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(bookStack));
                if (bookEnchants.isEmpty()) continue;

                for (ItemEntity targetEntity : items) {
                    if (targetEntity == bookEntity || !targetEntity.isAlive()) continue;
                    ItemStack targetStack = targetEntity.getItem();
                    if (targetStack.is(Items.ENCHANTED_BOOK) || targetStack.getCount() != 1) continue;
                    if (!targetStack.isDamageableItem() && !targetStack.isEnchantable() && !targetStack.isEnchanted()) continue;
                    if (bookEntity.distanceToSqr(targetEntity) > 1.5) continue;

                    Map<Enchantment, Integer> currentEnchants = new HashMap<>(EnchantmentHelper.getEnchantments(targetStack));
                    boolean changed = false;

                    for (Map.Entry<Enchantment, Integer> entry : bookEnchants.entrySet()) {
                        Enchantment ench = entry.getKey();
                        int bookLvl = entry.getValue();
                        int curLvl = currentEnchants.getOrDefault(ench, 0);

                        if (curLvl == 0) {
                            currentEnchants.put(ench, bookLvl);
                            changed = true;
                        } else if (curLvl == bookLvl && curLvl < ench.getMaxLevel()) {
                            currentEnchants.put(ench, curLvl + 1);
                            changed = true;
                        } else if (bookLvl > curLvl) {
                            currentEnchants.put(ench, bookLvl);
                            changed = true;
                        }
                    }

                    if (changed) {
                        EnchantmentHelper.setEnchantments(currentEnchants, targetStack);
                        targetEntity.setItem(targetStack);

                        if (bookStack.getCount() > 1) {
                            bookStack.shrink(1);
                            bookEntity.setItem(bookStack);
                        } else {
                            bookEntity.discard();
                        }

                        level.playSound(null, targetEntity.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8F, 1.2F);
                        level.sendParticles(ParticleTypes.ENCHANT, targetEntity.getX(), targetEntity.getY() + 0.4, targetEntity.getZ(), 25, 0.3, 0.3, 0.3, 0.1);
                        break;
                    }
                }
            }

            // Repair with Experience Bottle if item has Mending
            for (ItemEntity xpEntity : items) {
                if (!xpEntity.isAlive() || !xpEntity.getItem().is(Items.EXPERIENCE_BOTTLE)) continue;
                for (ItemEntity gearEntity : items) {
                    if (gearEntity == xpEntity || !gearEntity.isAlive()) continue;
                    ItemStack gear = gearEntity.getItem();
                    if (!gear.isDamaged() || gear.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.MENDING) <= 0) continue;
                    if (xpEntity.distanceToSqr(gearEntity) > 1.5) continue;

                    gear.setDamageValue(Math.max(0, gear.getDamageValue() - 150));
                    gearEntity.setItem(gear);
                    ItemStack xpStack = xpEntity.getItem();
                    if (xpStack.getCount() > 1) {
                        xpStack.shrink(1);
                        xpEntity.setItem(xpStack);
                    } else {
                        xpEntity.discard();
                    }
                    level.playSound(null, gearEntity.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.9F, 1.1F);
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER, gearEntity.getX(), gearEntity.getY() + 0.3, gearEntity.getZ(), 12, 0.25, 0.25, 0.25, 0.05);
                    break;
                }
            }
        }
    }
}
