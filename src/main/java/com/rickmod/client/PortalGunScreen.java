package com.rickmod.client;

import com.rickmod.RickMod;
import com.rickmod.item.PortalGunItem;
import com.rickmod.network.ModNetwork;
import com.rickmod.network.PacketDeleteWaypoint;
import com.rickmod.network.PacketSelectDestination;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PortalGunScreen extends Screen {
    private final InteractionHand hand;
    private final ItemStack stack;

    private static final String[][] PRESET_DIMS = new String[][]{
            {"minecraft:overworld", "Обычный мир (C-137)"},
            {"minecraft:the_nether", "Нижний мир (Nether)"},
            {"minecraft:the_end", "Край (The End)"},
            {"rickmod:citadel_of_ricks", "Цитадель Риков"},
            {"rickmod:blender_dimension", "Измерение Блендеров"},
            {"rickmod:cronenberg_wasteland", "Пустошь Кроненбергов"}
    };

    public PortalGunScreen(InteractionHand hand, ItemStack stack) {
        super(Component.literal("Навигатор Портальной Пушки"));
        this.hand = hand;
        this.stack = stack;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = 48;

        // Левая колонка: Измерения
        for (int i = 0; i < PRESET_DIMS.length; i++) {
            final String dimId = PRESET_DIMS[i][0];
            final String dimName = PRESET_DIMS[i][1];
            this.addRenderableWidget(Button.builder(Component.literal(dimName), btn -> {
                ModNetwork.CHANNEL.sendToServer(new PacketSelectDestination(hand, dimId, new BlockPos(0, 80, 0), dimName));
                this.onClose();
            }).bounds(centerX - 185, startY + i * 24, 175, 20).build());
        }

        // Правая колонка: Сохраненные точки (Shift + ПКМ)
        CompoundTag tag = stack.getOrCreateTag();
        ListTag waypoints = tag.getList("Waypoints", Tag.TAG_COMPOUND);
        int maxShow = Math.min(waypoints.size(), 6);
        for (int i = 0; i < maxShow; i++) {
            final int index = i;
            CompoundTag wp = waypoints.getCompound(i);
            String name = wp.getString("Name");
            String dim = wp.getString("Dim");
            BlockPos pos = new BlockPos(wp.getInt("X"), wp.getInt("Y"), wp.getInt("Z"));

            this.addRenderableWidget(Button.builder(Component.literal(name), btn -> {
                ModNetwork.CHANNEL.sendToServer(new PacketSelectDestination(hand, dim, pos, name));
                this.onClose();
            }).bounds(centerX + 5, startY + i * 24, 155, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("X"), btn -> {
                ModNetwork.CHANNEL.sendToServer(new PacketDeleteWaypoint(hand, index));
                this.onClose();
            }).bounds(centerX + 163, startY + i * 24, 22, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Закрыть"), btn -> this.onClose())
                .bounds(centerX - 50, this.height - 32, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        int centerX = this.width / 2;

        graphics.fill(centerX - 195, 20, centerX + 195, this.height - 10, 0xCC0A1F0A);
        graphics.renderOutline(centerX - 195, 20, 390, this.height - 30, 0xFF39FF14);

        graphics.drawCenteredString(this.font, "§a§lМЕЖПРОСТРАНСТВЕННЫЙ НАВИГАТОР", centerX, 28, 0x39FF14);
        graphics.drawString(this.font, "§7Измерения:", centerX - 185, 38, 0xAAAAAA);
        graphics.drawString(this.font, "§7Сохраненные точки (Shift+ПКМ):", centerX + 5, 38, 0xAAAAAA);

        if (stack.getItem() instanceof PortalGunItem gun) {
            String chargeText = gun.isInfinite()
                    ? "§aЗаряд: ∞ (Без лимита)"
                    : "§aЗаряд: " + gun.getCharge(stack) + " / " + gun.getMaxCharge();
            graphics.drawCenteredString(this.font, chargeText, centerX, this.height - 46, 0x39FF14);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
