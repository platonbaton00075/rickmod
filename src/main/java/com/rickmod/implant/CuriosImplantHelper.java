package com.rickmod.implant;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CuriosImplantHelper {

    public static boolean hasImplant(Player player, ImplantType type) {
        return !getImplantStack(player, type).isEmpty();
    }

    public static ItemStack getImplantStack(Player player, ImplantType type) {
        for (ItemStack stack : getAllEquippedImplants(player)) {
            if (stack.getItem() instanceof CyberImplantItem implant && implant.getImplantType() == type) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> getAllEquippedImplants(Player player) {
        List<ItemStack> result = new ArrayList<>();
        if (player.getOffhandItem().getItem() instanceof CyberImplantItem) {
            result.add(player.getOffhandItem());
        }
        if (!ModList.get().isLoaded("curios")) {
            return result;
        }
        try {
            Class<?> curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            Method getCuriosHelper = curiosApiClass.getMethod("getCuriosHelper");
            Object helper = getCuriosHelper.invoke(null);
            Method findCurios = helper.getClass().getMethod("findCurios", net.minecraft.world.entity.LivingEntity.class, java.util.function.Predicate.class);
            java.util.function.Predicate<ItemStack> pred = s -> s.getItem() instanceof CyberImplantItem;
            List<?> found = (List<?>) findCurios.invoke(helper, player, pred);
            for (Object slotResult : found) {
                Method stackMethod = slotResult.getClass().getMethod("stack");
                ItemStack st = (ItemStack) stackMethod.invoke(slotResult);
                if (!st.isEmpty()) {
                    result.add(st);
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }
}
