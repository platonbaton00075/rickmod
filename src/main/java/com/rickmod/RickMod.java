package com.rickmod;

import com.rickmod.compat.CreateCompat;
import com.rickmod.entity.GromflomiteSoldierEntity;
import com.rickmod.entity.MeeseeksEntity;
import com.rickmod.entity.MortyEntity;
import com.rickmod.entity.RickEntity;
import com.rickmod.init.ModBlockEntities;
import com.rickmod.init.ModBlocks;
import com.rickmod.init.ModCreativeTabs;
import com.rickmod.init.ModEffects;
import com.rickmod.init.ModEntities;
import com.rickmod.init.ModItems;
import com.rickmod.init.ModSounds;
import com.rickmod.network.ModNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RickMod.MOD_ID)
public class RickMod {
    public static final String MOD_ID = "rickmod";
    public static final String MODID = "rickmod";

    public RickMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        if (ModList.get().isLoaded("create")) {
            CreateCompat.register(modEventBus);
        }

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerAttributes);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.RICK_CITIZEN.get(), RickEntity.createAttributes().build());
        event.put(ModEntities.MORTY_CITIZEN.get(), MortyEntity.createAttributes().build());
        event.put(ModEntities.MEESEEKS.get(), MeeseeksEntity.createAttributes().build());
        event.put(ModEntities.GROMFLOMITE_SOLDIER.get(), GromflomiteSoldierEntity.createAttributes().build());
    }
}
