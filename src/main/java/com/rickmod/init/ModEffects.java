package com.rickmod.init;

import com.rickmod.RickMod;
import com.rickmod.effect.FreshCloneEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RickMod.MOD_ID);

    public static final RegistryObject<MobEffect> FRESH_CLONE = EFFECTS.register("fresh_clone",
            FreshCloneEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
