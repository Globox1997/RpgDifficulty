package net.rpgdifficulty.zone;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.ArrayList;
import java.util.List;

public class ZoneSyncManager {

    public record ZoneSyncPayload(List<ZoneEntry> zones) implements CustomPayload {

        public static final CustomPayload.Id<ZoneSyncPayload> ID =
                new CustomPayload.Id<>(Identifier.of("rpgdifficulty", "zone_sync"));

        public static final PacketCodec<PacketByteBuf, ZoneSyncPayload> CODEC = PacketCodec.of(
                (payload, buf) -> {
                    buf.writeVarInt(payload.zones.size());
                    for (ZoneEntry entry : payload.zones) {
                        entry.write(buf);
                    }
                },
                buf -> {
                    int size = buf.readVarInt();
                    List<ZoneEntry> zones = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        zones.add(ZoneEntry.read(buf));
                    }
                    return new ZoneSyncPayload(zones);
                }
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Schlanke, netzwerktaugliche Repräsentation einer Zone. */
    public record ZoneEntry(String dimension, DifficultyZone.Shape shape, double factor, String name,
                            int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double radius) {

        static ZoneEntry from(DifficultyZone zone) {
            if (zone.getShape() == DifficultyZone.Shape.BOX) {
                return new ZoneEntry(zone.getDimension(), zone.getShape(), zone.getFactor(), zone.getName(),
                        zone.getBoxMin().getX(), zone.getBoxMin().getY(), zone.getBoxMin().getZ(),
                        zone.getBoxMax().getX(), zone.getBoxMax().getY(), zone.getBoxMax().getZ(), 0);
            } else {
                return new ZoneEntry(zone.getDimension(), zone.getShape(), zone.getFactor(), zone.getName(),
                        zone.getCenter().getX(), zone.getCenter().getY(), zone.getCenter().getZ(),
                        0, 0, 0, zone.getRadius());
            }
        }

        void write(PacketByteBuf buf) {
            buf.writeString(dimension);
            buf.writeEnumConstant(shape);
            buf.writeDouble(factor);
            buf.writeBoolean(name != null);
            if (name != null) buf.writeString(name);
            buf.writeInt(minX);
            buf.writeInt(minY);
            buf.writeInt(minZ);
            buf.writeInt(maxX);
            buf.writeInt(maxY);
            buf.writeInt(maxZ);
            buf.writeDouble(radius);
        }

        static ZoneEntry read(PacketByteBuf buf) {
            String dimension = buf.readString();
            DifficultyZone.Shape shape = buf.readEnumConstant(DifficultyZone.Shape.class);
            double factor = buf.readDouble();
            String name = buf.readBoolean() ? buf.readString() : null;
            int minX = buf.readInt();
            int minY = buf.readInt();
            int minZ = buf.readInt();
            int maxX = buf.readInt();
            int maxY = buf.readInt();
            int maxZ = buf.readInt();
            double radius = buf.readDouble();
            return new ZoneEntry(dimension, shape, factor, name, minX, minY, minZ, maxX, maxY, maxZ, radius);
        }

        public boolean contains(String dimensionKey, double x, double y, double z) {
            if (!dimension.equals(dimensionKey)) return false;
            if (shape == DifficultyZone.Shape.BOX) {
                return x >= minX && x <= maxX + 1 && y >= minY && y <= maxY + 1 && z >= minZ && z <= maxZ + 1;
            } else {
                double dx = x - (minX + 0.5);
                double dy = y - (minY + 0.5);
                double dz = z - (minZ + 0.5);
                return dx * dx + dy * dy + dz * dz <= radius * radius;
            }
        }

        public String getDisplayName() {
            return (name != null && !name.isBlank()) ? name : "Zone";
        }
    }

    public static void syncToAll(MinecraftServer server) {
        List<DifficultyZone> zones = DifficultyZonePersistentState.get(server).getZones();
        ZoneSyncPayload payload = new ZoneSyncPayload(zones.stream().map(ZoneEntry::from).toList());
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void syncToPlayer(ServerPlayerEntity player) {
        List<DifficultyZone> zones = DifficultyZonePersistentState.get(player.getServer()).getZones();
        ZoneSyncPayload payload = new ZoneSyncPayload(zones.stream().map(ZoneEntry::from).toList());
        ServerPlayNetworking.send(player, payload);
    }
}
