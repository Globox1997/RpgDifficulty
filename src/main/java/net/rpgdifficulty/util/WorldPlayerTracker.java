package net.rpgdifficulty.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldPlayerTracker {

    private static final Map<RegistryKey<World>, List<ServerPlayerEntity>> PLAYERS = new ConcurrentHashMap<>();

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(world -> PLAYERS.put(world.getRegistryKey(), List.copyOf(world.getPlayers())));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PLAYERS.clear());
    }

    public static List<ServerPlayerEntity> getPlayers(ServerWorld world) {
        if (world.getServer().isOnThread()) {
            return world.getPlayers();
        }
        return PLAYERS.getOrDefault(world.getRegistryKey(), List.of());
    }

    @Nullable
    public static PlayerEntity getClosestPlayer(ServerWorld world, double x, double y, double z) {
        PlayerEntity closestPlayer = null;
        double closestDistance = 0.0D;
        for (PlayerEntity playerEntity : getPlayers(world)) {
            if (playerEntity.isSpectator()) {
                continue;
            }
            double distance = playerEntity.squaredDistanceTo(x, y, z);
            if (closestPlayer == null || distance < closestDistance) {
                closestPlayer = playerEntity;
                closestDistance = distance;
            }
        }
        return closestPlayer;
    }
}
