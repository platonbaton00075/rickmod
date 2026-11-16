package com.rickmod.client;

import com.rickmod.item.PortalGunItem;
import com.rickmod.network.ModNetwork;
import com.rickmod.network.PacketSelectDimension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PortalGunScreen extends Screen {
    private final InteractionHand hand;
    private final ItemStack gunStack;

    public PortalGunScreen(InteractionHand hand, ItemStack gunStack) {
        super(Component.literal("Навигатор Портальной Пушки"));
        this.hand = hand;
        this.gunStack = gunStack;
    }

    public PortalGunScreen(ItemStack gunStack) {
        this(InteractionHand.MAIN_HAND, gunStack);
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = 45;

        addDimButton(cx - 110, y, "Земля C-137 (Overworld)", "minecraft:overworld");
        y += 24;
        addDimButton(cx - 110, y, "Гранд-Цитадель Риков", "rickmod:citadel_of_ricks");
        y += 24;
        addDimButton(cx - 110, y, "Измерение Блендеров", "rickmod:blender_dimension");
        y += 24;
        addDimButton(cx - 110, y, "Пустошь Кроненбергов", "rickmod:cronenberg_wasteland");
        y += 32;

        ListTag waypoints = PortalGunItem.getWaypoints(gunStack);
        int maxShow = Math.min(waypoints.size(), 5);
        for (int i = 0; i < maxShow; i++) {
            final int idx = i;
            CompoundTag wp = waypoints.getCompound(i);
            String name = wp.getString("Name");
            String dim = wp.getString("Dim");
            int wx = wp.getInt("X");
            int wy = wp.getInt("Y");
            int wz = wp.getInt("Z");

            this.addRenderableWidget(Button.builder(Component.literal("★ " + name), b -> {
                ModNetwork.CHANNEL.sendToServer(new PacketSelectDimension(this.hand, dim, true, wx, wy, wz, name, -1));
                this.onClose();
            }).bounds(cx - 110, y, 195, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("X"), b -> {
                ModNetwork.CHANNEL.sendToServer(new PacketSelectDimension(this.hand, "", false, 0, 0, 0, "", idx));
                this.onClose();
            }).bounds(cx + 88, y, 22, 20).build());

            y += 22;
        }
    }

    private void addDimButton(int x, int y, String label, String dimId) {
        this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketSelectDimension(this.hand, dimId, false, 0, 0, 0, label, -1));
            this.onClose();
        }).bounds(x, y, 220, 20).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx);
        int cx = this.width / 2;
        gfx.fill(cx - 125, 20, cx + 125, this.height - 20, 0xCC051A0A);
        gfx.renderOutline(cx - 125, 20, 250, this.height - 40, 0xFF39FF14);
        gfx.drawCenteredString(this.font, "МЕЖПРОСТРАНСТВЕННЫЙ НАВИГАТОР", cx, 28, 0x39FF14);
        super.render(gfx, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
