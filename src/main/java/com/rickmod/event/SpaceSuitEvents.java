package com.rickmod.event;

import com.rickmod.RickMod;
import com.rickmod.item.RickCharacterArmorItem;
import com.rickmod.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID)
public class SpaceSuitEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.isCreative() || player.isSpectator()) return;

        ResourceLocation dim = player.level().dimension().location();
        boolean inCitadelSpace = dim.equals(new ResourceLocation(RickMod.MOD_ID, "citadel_of_ricks"));

        if (inCitadelSpace) {
            if (hasSpaceFlightGear(player)) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
                player.fallDistance = 0.0F;
                if (player.getAbilities().flying && !player.level().isClientSide && player.level().getGameTime() % 3 == 0) {
                    ((ServerLevel) player.level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            player.getX(), player.getY() + 0.2, player.getZ(), 2, 0.15, 0.05, 0.15, 0.02);
                }
            } else {
                if (player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
                if (!player.level().isClientSide && player.level().getGameTime() % 20 == 0) {
                    player.hurt(player.damageSources().freeze(), 2.0F);
                }
            }
        } else if (player.getAbilities().mayfly) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    public static boolean hasSpaceFlightGear(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

        // 1. RickMod Space Suit
        boolean rickSpaceSuit = head.is(ModItems.SPACE_HELMET.get())
                && chest.is(ModItems.SPACE_CHESTPLATE.get())
                && legs.is(ModItems.SPACE_LEGGINGS.get())
                && feet.is(ModItems.SPACE_BOOTS.get());
        if (rickSpaceSuit) return true;

        // 2. Full Rick Prime or Evil Morty Armor
        if (head.getItem() instanceof RickCharacterArmorItem && chest.getItem() instanceof RickCharacterArmorItem
                && legs.getItem() instanceof RickCharacterArmorItem && feet.getItem() instanceof RickCharacterArmorItem) {
            return true;
        }

        // 3. Create / Create SA Netherite Diving & Jetpack Kit
        String hId = String.valueOf(ForgeRegistries.ITEMS.getKey(head.getItem()));
        String cId = String.valueOf(ForgeRegistries.ITEMS.getKey(chest.getItem()));
        String lId = String.valueOf(ForgeRegistries.ITEMS.getKey(legs.getItem()));
        String fId = String.valueOf(ForgeRegistries.ITEMS.getKey(feet.getItem()));

        boolean netheriteDivingKit = hId.contains("netherite_diving_helmet")
                && (cId.contains("netherite_jetpack") || cId.contains("netherite_backtank"))
                && lId.contains("netherite_leggings")
                && fId.contains("netherite_diving_boots");
        return netheriteDivingKit;
    }
}
