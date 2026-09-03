package com.donuttest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScanManager {
    public record Marker(BlockPos pos, String type, int color) {}

    private final List<Marker> markers = new ArrayList<>();
    private int cooldown;

    public List<Marker> markers() { return Collections.unmodifiableList(markers); }

    public long count(String type) {
        return markers.stream().filter(m -> m.type.equals(type)).count();
    }

    public void tick(Minecraft client) {
        if (cooldown-- > 0) return;
        cooldown = 40;
        scan(client);
    }

    private void scan(Minecraft client) {
        var world = client.level;
        var player = client.player;
        if (world == null || player == null) return;

        markers.clear();
        Map<Long, Integer> chunkScores = new HashMap<>();
        Map<Long, Integer> storageCounts = new HashMap<>();
        BlockPos center = player.blockPosition();
        int radius = Math.min(DonutTestClient.SETTINGS.scanRadius, 64);
        int minY = Math.max(world.getMinY(), center.getY() - 32);
        int maxY = Math.min(world.getMaxY(), center.getY() + 32);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                if (!world.hasChunkAt(cursor.set(x, center.getY(), z))) continue;
                for (int y = minY; y <= maxY; y++) {
                    cursor.set(x, y, z);
                    Block block = world.getBlockState(cursor).getBlock();
                    String type = classify(block);
                    if (type == null) continue;
                    long chunk = (((long)(x >> 4)) << 32) ^ ((z >> 4) & 0xffffffffL);
                    int weight = type.equals("Spawner") ? 8 : type.equals("Storage") ? 3 : 1;
                    chunkScores.merge(chunk, weight, Integer::sum);
                    if (type.equals("Storage")) storageCounts.merge(chunk, 1, Integer::sum);
                    if (isEnabled(type)) markers.add(new Marker(cursor.immutable(), type, color(type)));
                }
            }
        }

        chunkScores.forEach((packed, score) -> {
            int cx = (int)(packed >> 32);
            int cz = (int)(long)packed;
            int storage = storageCounts.getOrDefault(packed, 0);
            if (DonutTestClient.SETTINGS.stashFinder && storage >= DonutTestClient.SETTINGS.stashThreshold)
                markers.add(new Marker(new BlockPos((cx << 4) + 8, center.getY(), (cz << 4) + 8), "Stash", 0xffff55ff));
            if (DonutTestClient.SETTINGS.baseFinder && score >= 18)
                markers.add(new Marker(new BlockPos((cx << 4) + 8, center.getY(), (cz << 4) + 8), "Base", 0xffffaa00));
            if (DonutTestClient.SETTINGS.chunkOverlay && score >= 8)
                markers.add(new Marker(new BlockPos(cx << 4, center.getY(), cz << 4), "Chunk", 0xffffff55));
        });
    }

    private static boolean isEnabled(String type) {
        return switch (type) {
            case "Spawner" -> DonutTestClient.SETTINGS.spawnerEsp;
            case "Storage" -> DonutTestClient.SETTINGS.storageEsp;
            case "Redstone" -> DonutTestClient.SETTINGS.redstoneEsp;
            default -> false;
        };
    }

    private static String classify(Block b) {
        if (b == Blocks.SPAWNER) return "Spawner";
        if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.BARREL || b == Blocks.ENDER_CHEST ||
                b == Blocks.SHULKER_BOX || b == Blocks.WHITE_SHULKER_BOX || b == Blocks.ORANGE_SHULKER_BOX ||
                b == Blocks.MAGENTA_SHULKER_BOX || b == Blocks.LIGHT_BLUE_SHULKER_BOX || b == Blocks.YELLOW_SHULKER_BOX ||
                b == Blocks.LIME_SHULKER_BOX || b == Blocks.PINK_SHULKER_BOX || b == Blocks.GRAY_SHULKER_BOX ||
                b == Blocks.LIGHT_GRAY_SHULKER_BOX || b == Blocks.CYAN_SHULKER_BOX || b == Blocks.PURPLE_SHULKER_BOX ||
                b == Blocks.BLUE_SHULKER_BOX || b == Blocks.BROWN_SHULKER_BOX || b == Blocks.GREEN_SHULKER_BOX ||
                b == Blocks.RED_SHULKER_BOX || b == Blocks.BLACK_SHULKER_BOX) return "Storage";
        if (b == Blocks.OBSERVER || b == Blocks.HOPPER || b == Blocks.PISTON || b == Blocks.STICKY_PISTON ||
                b == Blocks.DISPENSER || b == Blocks.DROPPER || b == Blocks.REPEATER || b == Blocks.COMPARATOR ||
                b == Blocks.REDSTONE_WIRE) return "Redstone";
        return null;
    }

    private static int color(String type) {
        return switch (type) {
            case "Spawner" -> 0xffff5555;
            case "Storage" -> 0xff55ffff;
            default -> 0xffffaa00;
        };
    }
}
