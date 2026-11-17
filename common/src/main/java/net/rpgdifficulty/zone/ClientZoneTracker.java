package net.rpgdifficulty.zone;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ClientZoneTracker {

    private static List<ZoneSyncManager.ZoneEntry> zones = new ArrayList<>();
    private static BlockPos lastCheckedPos = null;
    private static ZoneSyncManager.ZoneEntry currentZone = null;

    private static Consumer<ZoneSyncManager.ZoneEntry> enterHandler = zone -> {
    };

    public static void setEnterHandler(Consumer<ZoneSyncManager.ZoneEntry> handler) {
        enterHandler = handler;
    }

    public static void handlePayload(ZoneSyncManager.ZoneSyncPayload payload) {
        zones = payload.zones();
        lastCheckedPos = null;
    }

    public static void reset() {
        zones = new ArrayList<>();
        lastCheckedPos = null;
        currentZone = null;
    }

    public static void onClientTick(Minecraft client) {
        if (client.player == null || zones.isEmpty()) {
            return;
        }

        BlockPos pos = client.player.blockPosition();

        if (pos.equals(lastCheckedPos)) {
            return;
        }
        lastCheckedPos = pos;

        String dimension = client.player.level().dimension().identifier().toString();
        double x = client.player.getX();
        double y = client.player.getY();
        double z = client.player.getZ();

        Optional<ZoneSyncManager.ZoneEntry> found = zones.stream().filter(zone -> zone.contains(dimension, x, y, z)).findFirst();

        ZoneSyncManager.ZoneEntry newZone = found.orElse(null);

        if (!Objects.equals(newZone, currentZone)) {
            currentZone = newZone;
            if (newZone != null) {
                enterHandler.accept(currentZone);
            }
        }
    }

    public static ZoneSyncManager.ZoneEntry getCurrentZone() {
        return currentZone;
    }
}
