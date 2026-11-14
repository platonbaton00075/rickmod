package com.rickmod.init;

import com.rickmod.RickMod;
import com.rickmod.entity.MeeseeksEntity;
import com.rickmod.entity.MortyEntity;
import com.rickmod.entity.PortalEntity;
import com.rickmod.entity.RickEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RickMod.MOD_ID);

    public static final RegistryObject<EntityType<PortalEntity>> PORTAL = ENTITIES.register("portal",
            () -> EntityType.Builder.<PortalEntity>of(PortalEntity::new, MobCategory.MISC)
                    .sized(1.4F, 2.6F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("portal"));

    public static final RegistryObject<EntityType<RickEntity>> RICK_CITIZEN = ENTITIES.register("rick_citizen",
            () -> EntityType.Builder.of(RickEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("rick_citizen"));

    public static final RegistryObject<EntityType<MortyEntity>> MORTY_CITIZEN = ENTITIES.register("morty_citizen",
            () -> EntityType.Builder.of(MortyEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.75F)
                    .clientTrackingRange(8)
                    .build("morty_citizen"));

    public static final RegistryObject<EntityType<MeeseeksEntity>> MEESEEKS = ENTITIES.register("meeseeks",
            () -> EntityType.Builder.of(MeeseeksEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("meeseeks"));

    @SubscribeEvent
    public static void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(RICK_CITIZEN.get(), RickEntity.createAttributes().build());
        event.put(MORTY_CITIZEN.get(), MortyEntity.createAttributes().build());
        event.put(MEESEEKS.get(), MeeseeksEntity.createAttributes().build());
    }
}
