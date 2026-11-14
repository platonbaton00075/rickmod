package com.rickmod.registry;

import com.rickmod.RickMod;
import com.rickmod.entity.MortyEntity;
import com.rickmod.entity.PortalEntity;
import com.rickmod.entity.RickEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = RickMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RickMod.MOD_ID);

    public static final RegistryObject<EntityType<PortalEntity>> PORTAL =
            ENTITIES.register("portal", () -> EntityType.Builder.<PortalEntity>of(PortalEntity::new, MobCategory.MISC)
                    .sized(1.6F, 2.8F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("portal"));

    public static final RegistryObject<EntityType<RickEntity>> RICK_CITIZEN =
            ENTITIES.register("rick_citizen", () -> EntityType.Builder.of(RickEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("rick_citizen"));

    public static final RegistryObject<EntityType<MortyEntity>> MORTY_CITIZEN =
            ENTITIES.register("morty_citizen", () -> EntityType.Builder.of(MortyEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.75F)
                    .clientTrackingRange(10)
                    .build("morty_citizen"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(RICK_CITIZEN.get(), RickEntity.createAttributes().build());
        event.put(MORTY_CITIZEN.get(), MortyEntity.createAttributes().build());
    }
}
