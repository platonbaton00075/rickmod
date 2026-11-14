package com.rickmod.registry;

import com.rickmod.RickMod;
import com.rickmod.item.DeathCrystalHeadbandItem;
import com.rickmod.item.DeathCrystalItem;
import com.rickmod.item.MeeseeksBoxItem;
import com.rickmod.item.PortalGunItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RickMod.MOD_ID);

    public static final ArmorMaterial SPACE_SUIT_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) { return 550; }
        @Override public int getDefenseForType(ArmorItem.Type type) { return 3; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_IRON; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "rickmod:space_suit"; }
        @Override public float getToughness() { return 2.0f; }
        @Override public float getKnockbackResistance() { return 0.1f; }
    };

    // Portal Guns (Rick: 100, Evil Morty: 250, Rick Prime: Infinite -1)
    public static final RegistryObject<Item> RICKS_PORTAL_GUN = ITEMS.register("ricks_portal_gun",
            () -> new PortalGunItem(new Item.Properties().stacksTo(1), 100, 0x39FF14, false));

    public static final RegistryObject<Item> RICK_PRIMES_PORTAL_GUN = ITEMS.register("rick_primes_portal_gun",
            () -> new PortalGunItem(new Item.Properties().stacksTo(1), -1, 0x00FF44, false));

    public static final RegistryObject<Item> EVIL_MORTY_GOLDEN_PORTAL_GUN = ITEMS.register("evil_morty_golden_portal_gun",
            () -> new PortalGunItem(new Item.Properties().stacksTo(1), 250, 0xFFD700, true));

    // Core, Fluid & Gadgets
    public static final RegistryObject<Item> PORTAL_CORE = ITEMS.register("portal_core",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> PORTAL_FLUID_FLASK = ITEMS.register("portal_fluid_flask",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> MEESEEKS_BOX = ITEMS.register("meeseeks_box",
            () -> new MeeseeksBoxItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DEATH_CRYSTAL = ITEMS.register("death_crystal",
            () -> new DeathCrystalItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DEATH_CRYSTAL_HEADBAND = ITEMS.register("death_crystal_headband",
            () -> new DeathCrystalHeadbandItem(new Item.Properties().stacksTo(1)));

    // Space Suit
    public static final RegistryObject<Item> SPACE_HELMET = ITEMS.register("space_helmet",
            () -> new ArmorItem(SPACE_SUIT_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> SPACE_CHESTPLATE = ITEMS.register("space_chestplate",
            () -> new ArmorItem(SPACE_SUIT_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> SPACE_LEGGINGS = ITEMS.register("space_leggings",
            () -> new ArmorItem(SPACE_SUIT_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> SPACE_BOOTS = ITEMS.register("space_boots",
            () -> new ArmorItem(SPACE_SUIT_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Spawn Eggs
    public static final RegistryObject<Item> RICK_SPAWN_EGG = ITEMS.register("rick_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RICK_CITIZEN, 0xA8E6FF, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<Item> MORTY_SPAWN_EGG = ITEMS.register("morty_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MORTY_CITIZEN, 0xFFE64D, 0x3B6EA5, new Item.Properties()));
    public static final RegistryObject<Item> MEESEEKS_SPAWN_EGG = ITEMS.register("meeseeks_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MEESEEKS, 0x3CC8FF, 0xFF5522, new Item.Properties()));

    // Block Items
    public static final RegistryObject<Item> MEGA_LOG_ITEM = ITEMS.register("mega_log",
            () -> new BlockItem(ModBlocks.MEGA_LOG.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_PLANKS_ITEM = ITEMS.register("mega_planks",
            () -> new BlockItem(ModBlocks.MEGA_PLANKS.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_STAIRS_ITEM = ITEMS.register("mega_stairs",
            () -> new BlockItem(ModBlocks.MEGA_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_SLAB_ITEM = ITEMS.register("mega_slab",
            () -> new BlockItem(ModBlocks.MEGA_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_FENCE_ITEM = ITEMS.register("mega_fence",
            () -> new BlockItem(ModBlocks.MEGA_FENCE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_FENCE_GATE_ITEM = ITEMS.register("mega_fence_gate",
            () -> new BlockItem(ModBlocks.MEGA_FENCE_GATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MEGA_LEAVES_ITEM = ITEMS.register("mega_leaves",
            () -> new BlockItem(ModBlocks.MEGA_LEAVES.get(), new Item.Properties()));

    public static final RegistryObject<Item> CITADEL_STEEL_BLOCK_ITEM = ITEMS.register("citadel_steel_block",
            () -> new BlockItem(ModBlocks.CITADEL_STEEL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_CRYSTAL_BLOCK_ITEM = ITEMS.register("portal_crystal_block",
            () -> new BlockItem(ModBlocks.PORTAL_CRYSTAL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLENDER_GRATE_BLOCK_ITEM = ITEMS.register("blender_grate_block",
            () -> new BlockItem(ModBlocks.BLENDER_GRATE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRONENBERG_SOIL_ITEM = ITEMS.register("cronenberg_soil",
            () -> new BlockItem(ModBlocks.CRONENBERG_SOIL.get(), new Item.Properties()));
}
