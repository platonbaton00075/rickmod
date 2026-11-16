package com.rickmod.init;

import com.rickmod.RickMod;
import com.rickmod.item.RickCharacterArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModArmorItems {
    public static final DeferredRegister<Item> ARMOR_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RickMod.MODID);

    // Rick C-137 Set
    public static final RegistryObject<Item> C137_HELMET = ARMOR_ITEMS.register("c137_helmet",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.HELMET, "c137", new Item.Properties()));
    public static final RegistryObject<Item> C137_CHESTPLATE = ARMOR_ITEMS.register("c137_chestplate",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.CHESTPLATE, "c137", new Item.Properties()));
    public static final RegistryObject<Item> C137_LEGGINGS = ARMOR_ITEMS.register("c137_leggings",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.LEGGINGS, "c137", new Item.Properties()));
    public static final RegistryObject<Item> C137_BOOTS = ARMOR_ITEMS.register("c137_boots",
            () -> new RickCharacterArmorItem(ModArmorMaterials.C137, ArmorItem.Type.BOOTS, "c137", new Item.Properties()));

    // Evil Morty Set
    public static final RegistryObject<Item> EVIL_MORTY_HELMET = ARMOR_ITEMS.register("evil_morty_helmet",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.HELMET, "evil_morty", new Item.Properties()));
    public static final RegistryObject<Item> EVIL_MORTY_CHESTPLATE = ARMOR_ITEMS.register("evil_morty_chestplate",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.CHESTPLATE, "evil_morty", new Item.Properties()));
    public static final RegistryObject<Item> EVIL_MORTY_LEGGINGS = ARMOR_ITEMS.register("evil_morty_leggings",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.LEGGINGS, "evil_morty", new Item.Properties()));
    public static final RegistryObject<Item> EVIL_MORTY_BOOTS = ARMOR_ITEMS.register("evil_morty_boots",
            () -> new RickCharacterArmorItem(ModArmorMaterials.EVIL_MORTY, ArmorItem.Type.BOOTS, "evil_morty", new Item.Properties()));

    // Rick Prime Set
    public static final RegistryObject<Item> PRIME_HELMET = ARMOR_ITEMS.register("prime_helmet",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.HELMET, "prime", new Item.Properties()));
    public static final RegistryObject<Item> PRIME_CHESTPLATE = ARMOR_ITEMS.register("prime_chestplate",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.CHESTPLATE, "prime", new Item.Properties()));
    public static final RegistryObject<Item> PRIME_LEGGINGS = ARMOR_ITEMS.register("prime_leggings",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.LEGGINGS, "prime", new Item.Properties()));
    public static final RegistryObject<Item> PRIME_BOOTS = ARMOR_ITEMS.register("prime_boots",
            () -> new RickCharacterArmorItem(ModArmorMaterials.PRIME, ArmorItem.Type.BOOTS, "prime", new Item.Properties()));

    public static void register(IEventBus bus) {
        ARMOR_ITEMS.register(bus);
    }
}
