package com.rickmod.client.gui;

import com.rickmod.item.PortalGunItem;
import com.rickmod.network.ModNetwork;
import com.rickmod.network.PacketSelectDimension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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

    public PortalGunScreen(InteractionHand hand, ItemStack stack) {
        super(Component.literal("МЕЖПРОСТРАНСТВЕННЫЙ НАВИГАТОР C-137"));
        this.hand = hand;
        this.stack = stack;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = 58;

        // Only Overworld + Rick & Morty Custom Dimensions (Nether & End removed)
        String[][] dims = {
            {"Земля (C-137)", "minecraft:overworld"},
            {"Цитадель Риков (Космос)", "rickmod:citadel_of_ricks"},
            {"Измерение Блендеров", "rickmod:blender_dimension"},
            {"Пустошь Кроненбергов", "rickmod:cronenberg_wasteland"}
        };

        for (int i = 0; i < dims.length; i++) {
            final String label = dims[i][0];
            final String dimId = dims[i][1];
            this.addRenderableWidget(Button.builder(Component.literal("◈ " + label), btn -> {
                ModNetwork.CHANNEL.sendToServer(new PacketSelectDimension(this.hand, dimId, false, 0, 0, 0, label, -1));
                this.onClose();
            }).bounds(centerX - 185, startY + i * 24, 175, 20).build());
        }

        List<CompoundTag> waypoints = getWaypoints();
        for (int i = 0; i < Math.min(waypoints.size(), 7); i++) {
            final int idx = i;
            CompoundTag wp = waypoints.get(i);
            String name = wp.getString("Name");
            String dim = wp.getString("Dim");
            double wx = wp.getDouble("X");
            double wy = wp.getDouble("Y");
            double wz = wp.getDouble("Z");

            this.addRenderableWidget(Button.builder(Component.literal("✦ " + name), btn -> {
                ModNetwork.CHANNEL.sendToServer(new PacketSelectDimension(this.hand, dim, true, wx, wy, wz, name, -1));
                this.onClose();
            }).bounds(centerX + 10, startY + i * 24, 150, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("✕"), btn -> {
                ModNetwork.CHANNEL.sendToServer(new PacketSelectDimension(this.hand, "", false, 0, 0, 0, "", idx));
                this.onClose();
            }).bounds(centerX + 164, startY + i * 24, 22, 20).build());
        }
    }

    private List<CompoundTag> getWaypoints() {
        List<CompoundTag> list = new ArrayList<>();
        if (stack.hasTag() && stack.getTag().contains("Waypoints", Tag.TAG_LIST)) {
            ListTag tagList = stack.getTag().getList("Waypoints", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                list.add(tagList.getCompound(i));
            }
        }
        return list;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(g);
        int cx = this.width / 2;

        // Holographic Sci-Fi Frame
        g.fill(cx - 202, 16, cx + 202, 238, 0xEE04140A);
        g.renderOutline(cx - 202, 16, 404, 222, 0xFF39FF14);
        g.renderOutline(cx - 199, 19, 398, 216, 0x6600FF88);

        // Header Bar
        g.fill(cx - 199, 19, cx + 199, 37, 0xAA0C331A);
        g.drawCenteredString(this.font, "§a§l[ ПОРТАЛЬНЫЙ ДИСПЛЕЙ РИКА // ВЫБОР ИЗМЕРЕНИЯ ]", cx, 24, 0x39FF14);

        g.drawString(this.font, "§2ИЗМЕРЕНИЯ МУЛЬТИВСЕЛЕННОЙ:", cx - 185, 44, 0x55FF55);
        g.drawString(this.font, "§2СОХРАНЕННЫЕ ТОЧКИ (Shift+ПКМ):", cx + 10, 44, 0x55FF55);

        // Charge Bar at Bottom
        g.fill(cx - 190, 212, cx + 190, 230, 0xAA082212);
        if (stack.getItem() instanceof PortalGunItem gun) {
            if (gun.isInfinite()) {
                g.fill(cx - 188, 214, cx + 188, 228, 0xCC00FF55);
                g.drawCenteredString(this.font, "§f§lЭНЕРГИЯ: ∞ БЕЗЛИМИТНЫЙ РЕАКТОР ПРАЙМА", cx, 217, 0xFFFFFF);
            } else {
                int cur = gun.getCharge(stack);
                int max = gun.getMaxCharge();
                int barW = (int) (376.0F * ((float) cur / Math.max(1, max)));
                g.fill(cx - 188, 214, cx - 188 + barW, 228, 0xCC39FF14);
                g.drawCenteredString(this.font, "§fЗАРЯД ЖИДКОСТИ: " + cur + " / " + max, cx, 217, 0xFFFFFF);
            }
        }

        super.render(g, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
