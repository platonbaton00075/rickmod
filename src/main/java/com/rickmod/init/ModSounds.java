package com.rickmod.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "rickmod");

    public static final RegistryObject<SoundEvent> PORTAL_GUN_SHOOT = register("portal_gun_shoot");
    public static final RegistryObject<SoundEvent> PORTAL_OPEN = register("portal_open");
    public static final RegistryObject<SoundEvent> PORTAL_TRAVEL = register("portal_travel");
    public static final RegistryObject<SoundEvent> MEESEEKS_BOX_PRESS = register("meeseeks_box_press");
    public static final RegistryObject<SoundEvent> MEESEEKS_POOF = register("meeseeks_poof");
    public static final RegistryObject<SoundEvent> BLASTER_FIRE = register("blaster_fire");
    public static final RegistryObject<SoundEvent> DEATH_CRYSTAL_PULSE = register("death_crystal_pulse");
    public static final RegistryObject<SoundEvent> PHOENIX_REVIVE = register("phoenix_revive");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("rickmod", name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
