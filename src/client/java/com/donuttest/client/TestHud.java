package com.donuttest.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.network.chat.Component;

public final class TestHud {
    private TestHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            var mc = DonutTestClient.minecraft();
            if (mc.options.hideGui) return;
            int red = 0xffef4444;
            graphics.drawString(mc.font, Component.literal("DONUT AC TEST CLIENT"), 8, 8, red, true);
            if (!DonutTestClient.AUTHORIZED) {
                graphics.drawString(mc.font, Component.literal("Locked: server authorization required"), 8, 20, 0xffaaaaaa, true);
                return;
            }
            graphics.drawString(mc.font, Component.literal("AUTHORIZED • Right Shift: modules"), 8, 20, 0xff55ff55, true);
            int y = 34;
            int shown = 0;
            for (var marker : DonutTestClient.SCANNER.markers()) {
                if (shown++ >= 10) break;
                var p = marker.pos();
                graphics.drawString(mc.font, Component.literal(marker.type() + "  " + p.getX() + " " + p.getY() + " " + p.getZ()), 8, y, marker.color(), true);
                y += 11;
            }
        });
    }
}
