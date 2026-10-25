package net.rpgdifficulty.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldPlayerTracker {

    private static final Map<ResourceKey<Level>, List<ServerPlayer>> PLAYERS = new ConcurrentHashMap<>();

    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(world -> PLAYERS.put(world.dimension(), List.copyOf(world.players())));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PLAYERS.clear());
    }

    public static List<ServerPlayer> getPlayers(ServerLevel world) {
        if (world.getServer().isSameThread()) {
            return world.players();
        }
        return PLAYERS.getOrDefault(world.dimension(), List.of());
    }

    @Nullable
    public static Player getClosestPlayer(ServerLevel world, double x, double y, double z) {
        Player closestPlayer = null;
        double closestDistance = 0.0D;
        for (Player playerEntity : getPlayers(world)) {
            if (playerEntity.isSpectator()) {
                continue;
            }
            double distance = playerEntity.distanceToSqr(x, y, z);
            if (closestPlayer == null || distance < closestDistance) {
                closestPlayer = playerEntity;
                closestDistance = distance;
            }
        }
        return closestPlayer;
    }
}
