package com.donuttest.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.donuttest.TestAuthorizationPayload;
import org.lwjgl.glfw.GLFW;

public final class DonutTestClient implements ClientModInitializer {
    public static final ModuleSettings SETTINGS = new ModuleSettings();
    public static final ScanManager SCANNER = new ScanManager();
    private static KeyMapping menuKey;
    public static volatile boolean AUTHORIZED = false;

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(TestAuthorizationPayload.TYPE, TestAuthorizationPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(TestAuthorizationPayload.TYPE,
                (payload, context) -> AUTHORIZED = payload.enabled());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> AUTHORIZED = false);
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.donut_ac_test.menu",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.consumeClick()) {
                if (AUTHORIZED) client.setScreen(new TestClientScreen());
            }
            if (AUTHORIZED && client.level != null && client.player != null) SCANNER.tick(client);
        });
        TestHud.register();
    }

    public static Minecraft minecraft() {
        return Minecraft.getInstance();
    }
}
