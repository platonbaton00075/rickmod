package com.rickmod.init;

import com.rickmod.RickMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RickMod.MOD_ID);

    public static final RegistryObject<SoundEvent> PORTAL_GUN_SHOOT = registerSound("portal_gun_shoot");
    public static final RegistryObject<SoundEvent> PORTAL_OPEN = registerSound("portal_open");
    public static final RegistryObject<SoundEvent> PORTAL_TRAVEL = registerSound("portal_travel");
    public static final RegistryObject<SoundEvent> MEESEEKS_BOX_PRESS = registerSound("meeseeks_box_press");
    public static final RegistryObject<SoundEvent> MEESEEKS_POOF = registerSound("meeseeks_poof");
    public static final RegistryObject<SoundEvent> BLASTER_FIRE = registerSound("blaster_fire");
    public static final RegistryObject<SoundEvent> DEATH_CRYSTAL_PULSE = registerSound("death_crystal_pulse");
    public static final RegistryObject<SoundEvent> PHOENIX_REVIVE = registerSound("phoenix_revive");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(RickMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}
