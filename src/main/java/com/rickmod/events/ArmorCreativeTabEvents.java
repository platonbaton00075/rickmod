package com.rickmod.events;

import com.rickmod.RickMod;
import com.rickmod.init.ModArmorItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RickMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArmorCreativeTabEvents {
    @SubscribeEvent
    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            ModArmorItems.ARMOR_ITEMS.getEntries().forEach(entry -> event.accept(entry.get()));
        }
    }
}
