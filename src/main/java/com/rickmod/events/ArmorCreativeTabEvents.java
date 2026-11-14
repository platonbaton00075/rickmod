package com.rickmod.events;

import com.rickmod.RickMod;
import com.rickmod.init.ModArmorItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArmorCreativeTabEvents {
    @SubscribeEvent
    public static void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            ModArmorItems.ARMOR_ITEMS.getEntries().forEach(reg -> event.accept(reg.get()));
        }
    }
}
