package net.rpgdifficulty.zone;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientZoneTracker {

    private static List<ZoneSyncManager.ZoneEntry> zones = new ArrayList<>();
    private static BlockPos lastCheckedPos = null;
    private static ZoneSyncManager.ZoneEntry currentZone = null;

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ZoneSyncManager.ZoneSyncPayload.ID, (payload, context) -> {
            zones = payload.zones();
            lastCheckedPos = null;
        });

        ClientTickEvents.END_CLIENT_TICK.register(ClientZoneTracker::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.player == null || zones.isEmpty()) {
            return;
        }

        BlockPos pos = client.player.getBlockPos();

        if (pos.equals(lastCheckedPos)) {
            return;
        }
        lastCheckedPos = pos;

        String dimension = client.player.getWorld().getRegistryKey().getValue().toString();
        double x = client.player.getX();
        double y = client.player.getY();
        double z = client.player.getZ();

        Optional<ZoneSyncManager.ZoneEntry> found = zones.stream().filter(zone -> zone.contains(dimension, x, y, z)).findFirst();

        ZoneSyncManager.ZoneEntry newZone = found.orElse(null);

        if (newZone != currentZone) {
            currentZone = newZone;
            if (newZone != null) {
                ClientZoneEvents.ENTER.invoker().onEnter(currentZone);
            }
        }
    }

    public static ZoneSyncManager.ZoneEntry getCurrentZone() {
        return currentZone;
    }
}
