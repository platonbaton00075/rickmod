package com.rickmod.init;

import com.rickmod.RickMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RickMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> RICK_TAB = TABS.register("rick_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.rickmod"))
                    .icon(() -> new ItemStack(ModItems.RICKS_PORTAL_GUN.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.RICKS_PORTAL_GUN.get());
                        output.accept(ModItems.RICK_PRIMES_PORTAL_GUN.get());
                        output.accept(ModItems.EVIL_MORTY_GOLDEN_PORTAL_GUN.get());
                        output.accept(ModItems.PORTAL_CORE.get());
                        output.accept(ModItems.PORTAL_FLUID_FLASK.get());

                        output.accept(ModItems.MEESEEKS_BOX.get());
                        output.accept(ModItems.DEATH_CRYSTAL.get());
                        output.accept(ModItems.DEATH_CRYSTAL_HEADBAND.get());
                        output.accept(ModItems.PICKLE_SERUM.get());
                        output.accept(ModItems.ANTI_PICKLE_SERUM.get());
                        output.accept(ModItems.RAT_EXOSKELETON.get());
                        output.accept(ModItems.RAT_LASER.get());
                        output.accept(ModItems.PHOENIX_POD.get());

                        output.accept(ModItems.FEDERATION_BLASTER.get());
                        output.accept(ModItems.PLASMA_CELL.get());

                        output.accept(ModItems.SPACE_HELMET.get());
                        output.accept(ModItems.SPACE_CHESTPLATE.get());
                        output.accept(ModItems.SPACE_LEGGINGS.get());
                        output.accept(ModItems.SPACE_BOOTS.get());

                        output.accept(ModItems.C137_HELMET.get());
                        output.accept(ModItems.C137_CHESTPLATE.get());
                        output.accept(ModItems.C137_LEGGINGS.get());
                        output.accept(ModItems.C137_BOOTS.get());

                        output.accept(ModItems.EVIL_MORTY_HELMET.get());
                        output.accept(ModItems.EVIL_MORTY_CHESTPLATE.get());
                        output.accept(ModItems.EVIL_MORTY_LEGGINGS.get());
                        output.accept(ModItems.EVIL_MORTY_BOOTS.get());

                        output.accept(ModItems.PRIME_HELMET.get());
                        output.accept(ModItems.PRIME_CHESTPLATE.get());
                        output.accept(ModItems.PRIME_LEGGINGS.get());
                        output.accept(ModItems.PRIME_BOOTS.get());

                        output.accept(ModItems.RICK_SCANNER_EYE.get());
                        output.accept(ModItems.EVIL_MORTY_GOLD_EYE.get());
                        output.accept(ModItems.RICK_FORCE_FIELD.get());
                        output.accept(ModItems.EVIL_MORTY_FORCE_FIELD.get());
                        output.accept(ModItems.RICK_SYNTHETIC_LIVER.get());
                        output.accept(ModItems.PRIME_NANO_REGENERATOR.get());
                        output.accept(ModItems.RICK_PLASMA_ARM.get());
                        output.accept(ModItems.EVIL_MORTY_MAGNET_HAND.get());
                        output.accept(ModItems.PRIME_QUANTUM_BLADES.get());
                        output.accept(ModItems.RICK_SPRING_LEGS.get());
                        output.accept(ModItems.PRIME_PHASE_ACCELERATOR.get());

                        output.accept(ModItems.CITADEL_STEEL_BLOCK.get());
                        output.accept(ModItems.PORTAL_CRYSTAL_BLOCK.get());
                        output.accept(ModItems.BLENDER_GRATE_BLOCK.get());
                        output.accept(ModItems.CRONENBERG_SOIL.get());

                        output.accept(ModItems.MEGA_LOG.get());
                        output.accept(ModItems.MEGA_PLANKS.get());
                        output.accept(ModItems.MEGA_STAIRS.get());
                        output.accept(ModItems.MEGA_SLAB.get());
                        output.accept(ModItems.MEGA_FENCE.get());
                        output.accept(ModItems.MEGA_FENCE_GATE.get());
                        output.accept(ModItems.MEGA_LEAVES.get());

                        output.accept(ModItems.RICK_SPAWN_EGG.get());
                        output.accept(ModItems.MORTY_SPAWN_EGG.get());
                        output.accept(ModItems.MEESEEKS_SPAWN_EGG.get());
                        output.accept(ModItems.GROMFLOMITE_SPAWN_EGG.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
