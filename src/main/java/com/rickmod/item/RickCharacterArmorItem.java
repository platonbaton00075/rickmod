package com.rickmod.item;

import com.rickmod.client.model.CharacterArmorModel;
import com.rickmod.init.ModArmorMaterials;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RickCharacterArmorItem extends ArmorItem {

    private final String setType;

    public RickCharacterArmorItem(ArmorMaterial material, Type type, String setType, Properties props) {
        super(material, type, props);
        this.setType = setType;
    }

    public String getSetType() {
        return setType;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide) return;
        if (!hasFullSet(player)) return;

        switch (setType) {
            case "c137" -> {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 40, 1, false, false, true));
            }
            case "evil_morty" -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
            }
            case "prime" -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false, true));
            }
        }
    }

    public boolean hasFullSet(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (!(armor.getItem() instanceof RickCharacterArmorItem item) || !item.setType.equals(this.setType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        switch (setType) {
            case "c137" -> tooltip.add(Component.literal("§b[Сет Рика C-137] §7Полный комплект: Скорость I и Удача II"));
            case "evil_morty" -> tooltip.add(Component.literal("§6[Сет Злого Морти] §7Полный комплект: Защита II, Сила I и Полёт в космосе"));
            case "prime" -> tooltip.add(Component.literal("§a[Сет Рика Прайма] §7Полный комплект: Сила II, Регенерация II, Скорость II и Полёт в космосе"));
        }
        tooltip.add(Component.literal("§8Можно зачаровать выбрасыванием с книгой на землю"));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HumanoidModel<?> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (model == null) {
                    model = switch (setType) {
                        case "evil_morty" -> new CharacterArmorModel(CharacterArmorModel.createEvilMortyLayer().bakeRoot());
                        case "prime" -> new CharacterArmorModel(CharacterArmorModel.createPrimeLayer().bakeRoot());
                        default -> new CharacterArmorModel(CharacterArmorModel.createC137Layer().bakeRoot());
                    };
                }
                model.head.visible = slot == EquipmentSlot.HEAD;
                model.hat.visible = slot == EquipmentSlot.HEAD;
                model.body.visible = slot == EquipmentSlot.CHEST;
                model.rightArm.visible = slot == EquipmentSlot.CHEST;
                model.leftArm.visible = slot == EquipmentSlot.CHEST;
                model.rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
                model.leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
                return model;
            }
        });
    }
}
