package com.rickmod.init;

import com.rickmod.RickMod;
import com.rickmod.item.AntiPickleSerumItem;
import com.rickmod.item.DeathCrystalHeadbandItem;
import com.rickmod.item.DeathCrystalItem;
import com.rickmod.item.FederationBlasterItem;
import com.rickmod.item.ImplantItem;
import com.rickmod.item.MeeseeksBoxItem;
import com.rickmod.item.ModArmorMaterials;
import com.rickmod.item.PickleSerumItem;
import com.rickmod.item.PlasmaCellItem;
import com.rickmod.item.PortalFluidFlaskItem;
import com.rickmod.item.PortalGunItem;
import com.rickmod.item.RatExoskeletonItem;
import com.rickmod.item.RatLaserItem;
import com.rickmod.item.RickCharacterArmorItem;
import com.rickmod.item.SpaceSuitArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RickMod.MOD_ID);

    // 3 Portal Guns
    public static final RegistryObject<Item> RICKS_PORTAL_GUN = ITEMS.register("ricks_portal_gun",
            () -> new PortalGunItem(PortalGunItem.GunType.RICK, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> RICK_PRIMES_PORTAL_GUN = ITEMS.register("rick_primes_portal_gun",
            () -> new PortalGunItem(PortalGunItem.GunType.PRIME, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> EVIL_MORTY_GOLDEN_PORTAL_GUN = ITEMS.register("evil_morty_golden_portal_gun",
            () -> new PortalGunItem(PortalGunItem.GunType.EVIL_MORTY, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // Core Components
    public static final RegistryObject<Item> PORTAL_CORE = ITEMS.register("portal_core",
            () -> new Item(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> PORTAL_FLUID_FLASK = ITEMS.register("portal_fluid_flask",
            () -> new PortalFluidFlaskItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

    // Gadgets & Serums
    public static final RegistryObject<Item> MEESEEKS_BOX = ITEMS.register("meeseeks_box",
            () -> new MeeseeksBoxItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> DEATH_CRYSTAL = ITEMS.register("death_crystal",
            () -> new DeathCrystalItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> DEATH_CRYSTAL_HEADBAND = ITEMS.register("death_crystal_headband",
            () -> new DeathCrystalHeadbandItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> PICKLE_SERUM = ITEMS.register("pickle_serum",
            () -> new PickleSerumItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> ANTI_PICKLE_SERUM = ITEMS.register("anti_pickle_serum",
            () -> new AntiPickleSerumItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> RAT_EXOSKELETON = ITEMS.register("rat_exoskeleton",
            () -> new RatExoskeletonItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> RAT_LASER = ITEMS.register("rat_laser",
            () -> new RatLaserItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    // Federation Weapons & Ammo
    public static final RegistryObject<Item> FEDERATION_BLASTER = ITEMS.register("federation_blaster",
            () -> new FederationBlasterItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> PLASMA_CELL = ITEMS.register("plasma_cell",
            () -> new PlasmaCellItem(new Item.Properties().stacksTo(64)));

    // Space Suit
    public static final RegistryObject<Item> SPACE_HELMET = ITEMS.register("space_helmet",
            () -> new SpaceSuitArmorItem(ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> SPACE_CHESTPLATE = ITEMS.register("space_chestplate",
            () -> new SpaceSuitArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> SPACE_LEGGINGS = ITEMS.register("space_leggings",
            () -> new SpaceSuitArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> SPACE_BOOTS = ITEMS.register("space_boots",
            () -> new SpaceSuitArmorItem(ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.RARE)));

    // Character 3D Armor Sets (C-137, Evil Morty, Prime)
    public static final RegistryObject<Item> C137_HELMET = ITEMS.register("c137_helmet",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.HELMET, "c137", new Item.Properties()));
    public static final RegistryObject<Item> C137_CHESTPLATE = ITEMS.register("c137_chestplate",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.CHESTPLATE, "c137", new Item.Properties()));
    public static final RegistryObject<Item> C137_LEGGINGS = ITEMS.register("c137_leggings",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.LEGGINGS, "c137", new Item.Properties()));
    public static final RegistryObject<Item> C137_BOOTS = ITEMS.register("c137_boots",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.BOOTS, "c137", new Item.Properties()));

    public static final RegistryObject<Item> EVIL_MORTY_HELMET = ITEMS.register("evil_morty_helmet",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.HELMET, "evil_morty", new Item.Properties()));
    public static final RegistryObject<Item> EVIL_MORTY_CHESTPLATE = ITEMS.register("evil_morty_chestplate",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.CHESTPLATE, "evil_morty", new Item.Properties()));
    public static final RegistryObject<Item> EVIL_MORTY_LEGGINGS = ITEMS.register("evil_morty_leggings",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.LEGGINGS, "evil_morty", new Item.Properties()));
    public static final RegistryObject<Item> EVIL_MORTY_BOOTS = ITEMS.register("evil_morty_boots",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.BOOTS, "evil_morty", new Item.Properties()));

    public static final RegistryObject<Item> PRIME_HELMET = ITEMS.register("prime_helmet",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.HELMET, "prime", new Item.Properties()));
    public static final RegistryObject<Item> PRIME_CHESTPLATE = ITEMS.register("prime_chestplate",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.CHESTPLATE, "prime", new Item.Properties()));
    public static final RegistryObject<Item> PRIME_LEGGINGS = ITEMS.register("prime_leggings",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.LEGGINGS, "prime", new Item.Properties()));
    public static final RegistryObject<Item> PRIME_BOOTS = ITEMS.register("prime_boots",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.BOOTS, "prime", new Item.Properties()));

    // 11 Curios Implants
    public static final RegistryObject<Item> RICK_SCANNER_EYE = ITEMS.register("rick_scanner_eye",
            () -> new ImplantItem("head", "item.rickmod.rick_scanner_eye.desc", new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> EVIL_MORTY_GOLD_EYE = ITEMS.register("evil_morty_gold_eye",
            () -> new ImplantItem("head", "item.rickmod.evil_morty_gold_eye.desc", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> RICK_FORCE_FIELD = ITEMS.register("rick_force_field",
            () -> new ImplantItem("body", "item.rickmod.rick_force_field.desc", new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> EVIL_MORTY_FORCE_FIELD = ITEMS.register("evil_morty_force_field",
            () -> new ImplantItem("body", "item.rickmod.evil_morty_force_field.desc", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> RICK_SYNTHETIC_LIVER = ITEMS.register("rick_synthetic_liver",
            () -> new ImplantItem("body", "item.rickmod.rick_synthetic_liver.desc", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> PRIME_NANO_REGENERATOR = ITEMS.register("prime_nano_regenerator",
            () -> new ImplantItem("body", "item.rickmod.prime_nano_regenerator.desc", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> RICK_PLASMA_ARM = ITEMS.register("rick_plasma_arm",
            () -> new ImplantItem("hands", "item.rickmod.rick_plasma_arm.desc", new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> EVIL_MORTY_MAGNET_HAND = ITEMS.register("evil_morty_magnet_hand",
            () -> new ImplantItem("hands", "item.rickmod.evil_morty_magnet_hand.desc", new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> PRIME_QUANTUM_BLADES = ITEMS.register("prime_quantum_blades",
            () -> new ImplantItem("hands", "item.rickmod.prime_quantum_blades.desc", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> RICK_SPRING_LEGS = ITEMS.register("rick_spring_legs",
            () -> new ImplantItem("belt", "item.rickmod.rick_spring_legs.desc", new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> PRIME_PHASE_ACCELERATOR = ITEMS.register("prime_phase_accelerator",
            () -> new ImplantItem("feet", "item.rickmod.prime_phase_accelerator.desc", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // Spawn Eggs
    public static final RegistryObject<Item> RICK_SPAWN_EGG = ITEMS.register("rick_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RICK_CITIZEN, 0x73D2DE, 0xF5F5F5, new Item.Properties()));
    public static final RegistryObject<Item> MORTY_SPAWN_EGG = ITEMS.register("morty_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MORTY_CITIZEN, 0xF4D03F, 0x2E86C1, new Item.Properties()));
    public static final RegistryObject<Item> MEESEEKS_SPAWN_EGG = ITEMS.register("meeseeks_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MEESEEKS, 0x3CC8FF, 0xFF5522, new Item.Properties()));
    public static final RegistryObject<Item> GROMFLOMITE_SPAWN_EGG = ITEMS.register("gromflomite_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GROMFLOMITE_SOLDIER, 0x4A6B3A, 0xB82929, new Item.Properties()));

    // Block Items
    public static final RegistryObject<Item> MEGA_LOG = ITEMS.register("mega_log",
            () -> new BlockItem(ModBlocks.MEGA_LOG.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_PLANKS = ITEMS.register("mega_planks",
            () -> new BlockItem(ModBlocks.MEGA_PLANKS.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_STAIRS = ITEMS.register("mega_stairs",
            () -> new BlockItem(ModBlocks.MEGA_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_SLAB = ITEMS.register("mega_slab",
            () -> new BlockItem(ModBlocks.MEGA_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_FENCE = ITEMS.register("mega_fence",
            () -> new BlockItem(ModBlocks.MEGA_FENCE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_FENCE_GATE = ITEMS.register("mega_fence_gate",
            () -> new BlockItem(ModBlocks.MEGA_FENCE_GATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_LEAVES = ITEMS.register("mega_leaves",
            () -> new BlockItem(ModBlocks.MEGA_LEAVES.get(), new Item.Properties()));

    public static final RegistryObject<Item> CITADEL_STEEL_BLOCK = ITEMS.register("citadel_steel_block",
            () -> new BlockItem(ModBlocks.CITADEL_STEEL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_CRYSTAL_BLOCK = ITEMS.register("portal_crystal_block",
            () -> new BlockItem(ModBlocks.PORTAL_CRYSTAL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLENDER_GRATE_BLOCK = ITEMS.register("blender_grate_block",
            () -> new BlockItem(ModBlocks.BLENDER_GRATE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRONENBERG_SOIL = ITEMS.register("cronenberg_soil",
            () -> new BlockItem(ModBlocks.CRONENBERG_SOIL.get(), new Item.Properties()));
    public static final RegistryObject<Item> PHOENIX_POD = ITEMS.register("phoenix_pod",
            () -> new BlockItem(ModBlocks.PHOENIX_POD.get(), new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
