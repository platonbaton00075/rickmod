package com.rickmod.events;

/**
 * Intentionally empty.
 * The old handler iterated ModArmorItems.ARMOR_ITEMS, a DeferredRegister that is never registered
 * on the mod event bus, so calling get() on its entries threw while the Combat creative tab was built.
 * All armor pieces are already listed in the Rick &amp; Morty creative tab (ModCreativeTabs).
 */
public class ArmorCreativeTabEvents {
}
