package com.rickmod.init;

import com.rickmod.RickMod;
import com.rickmod.entity.GromflomiteSoldierEntity;
import com.rickmod.entity.MeeseeksEntity;
import com.rickmod.entity.MortyEntity;
import com.rickmod.entity.PortalEntity;
import com.rickmod.entity.RickEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RickMod.MOD_ID);

    public static final RegistryObject<EntityType<PortalEntity>> PORTAL = ENTITIES.register("portal",
            () -> EntityType.Builder.<PortalEntity>of(PortalEntity::new, MobCategory.MISC)
                    .sized(1.6F, 2.6F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("portal"));

    public static final RegistryObject<EntityType<RickEntity>> RICK_CITIZEN = ENTITIES.register("rick_citizen",
            () -> EntityType.Builder.of(RickEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .build("rick_citizen"));

    public static final RegistryObject<EntityType<MortyEntity>> MORTY_CITIZEN = ENTITIES.register("morty_citizen",
            () -> EntityType.Builder.of(MortyEntity::new, MobCategory.CREATURE)
                    .sized(0.55F, 1.7F)
                    .build("morty_citizen"));

    public static final RegistryObject<EntityType<MeeseeksEntity>> MEESEEKS = ENTITIES.register("meeseeks",
            () -> EntityType.Builder.of(MeeseeksEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.9F)
                    .build("meeseeks"));

    public static final RegistryObject<EntityType<GromflomiteSoldierEntity>> GROMFLOMITE_SOLDIER = ENTITIES.register("gromflomite_soldier",
            () -> EntityType.Builder.of(GromflomiteSoldierEntity::new, MobCategory.MONSTER)
                    .sized(0.65F, 2.0F)
                    .build("gromflomite_soldier"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
