package com.donuttest.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class TestClientScreen extends Screen {
    public TestClientScreen() { super(Component.literal("Donut AC Test Client")); }

    @Override
    protected void init() {
        int x = width / 2 - 155;
        int y = height / 2 - 80;
        addToggle(x, y, "Player Tracers", () -> DonutTestClient.SETTINGS.playerTracers, v -> DonutTestClient.SETTINGS.playerTracers = v);
        addToggle(x, y + 24, "Spawner ESP", () -> DonutTestClient.SETTINGS.spawnerEsp, v -> DonutTestClient.SETTINGS.spawnerEsp = v);
        addToggle(x, y + 48, "Storage ESP", () -> DonutTestClient.SETTINGS.storageEsp, v -> DonutTestClient.SETTINGS.storageEsp = v);
        addToggle(x, y + 72, "Redstone/Build ESP", () -> DonutTestClient.SETTINGS.redstoneEsp, v -> DonutTestClient.SETTINGS.redstoneEsp = v);
        addToggle(x + 160, y, "Stash Finder", () -> DonutTestClient.SETTINGS.stashFinder, v -> DonutTestClient.SETTINGS.stashFinder = v);
        addToggle(x + 160, y + 24, "Base Finder", () -> DonutTestClient.SETTINGS.baseFinder, v -> DonutTestClient.SETTINGS.baseFinder = v);
        addToggle(x + 160, y + 48, "Chunk Finder", () -> DonutTestClient.SETTINGS.chunkOverlay, v -> DonutTestClient.SETTINGS.chunkOverlay = v);
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(width / 2 - 75, y + 112, 150, 20).build());
    }

    private void addToggle(int x, int y, String name, BooleanSupplier get, Consumer<Boolean> set) {
        addRenderableWidget(Button.builder(label(name, get.getAsBoolean()), b -> {
            boolean value = !get.getAsBoolean();
            set.accept(value);
            b.setMessage(label(name, value));
        }).bounds(x, y, 150, 20).build());
    }

    private static Component label(String name, boolean enabled) {
        return Component.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 108, 0xffef4444);
        graphics.drawCenteredString(font, Component.literal("Authorized server diagnostics"), width / 2, height / 2 - 94, 0xffaaaaaa);
        super.render(graphics, mouseX, mouseY, delta);
    }
}
