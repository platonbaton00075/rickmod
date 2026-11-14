package com.rickmod.init;

import com.rickmod.RickMod;
import com.rickmod.block.entity.PhoenixPodBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RickMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<PhoenixPodBlockEntity>> PHOENIX_POD_BE =
            BLOCK_ENTITIES.register("phoenix_pod_be",
                    () -> BlockEntityType.Builder.of(PhoenixPodBlockEntity::new, ModBlocks.PHOENIX_POD.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
