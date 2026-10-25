package net.rpgdifficulty.zone;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.rpgdifficulty.RpgDifficultyMain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DifficultyZonePersistentState extends SavedData {

    private static final String LEGACY_FILE_NAME = "rpgdifficulty_zones.dat";

    private static final Codec<DifficultyZonePersistentState> CODEC = CompoundTag.CODEC.xmap(DifficultyZonePersistentState::fromNbt, state -> state.save(new CompoundTag()));

    private static final SavedDataType<DifficultyZonePersistentState> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath("rpgdifficulty", "zones"), DifficultyZonePersistentState::new, CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    private static volatile List<DifficultyZone> cachedZones = List.of();

    private final List<DifficultyZone> zones = new ArrayList<>();

    private boolean legacyImported = false;

    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (DifficultyZone zone : zones) {
            list.add(zone.toNbt());
        }
        nbt.put("Zones", list);
        nbt.putBoolean("LegacyImported", legacyImported);
        return nbt;
    }

    public static DifficultyZonePersistentState fromNbt(CompoundTag nbt) {
        DifficultyZonePersistentState state = new DifficultyZonePersistentState();
        ListTag list = nbt.getListOrEmpty("Zones");
        for (int i = 0; i < list.size(); i++) {
            state.zones.add(DifficultyZone.fromNbt(list.getCompoundOrEmpty(i)));
        }
        state.legacyImported = nbt.getBooleanOr("LegacyImported", false);
        return state;
    }

    public List<DifficultyZone> getZones() {
        return zones;
    }

    public void addZone(DifficultyZone zone) {
        zones.add(zone);
        cachedZones = List.copyOf(zones);
        setDirty();
    }

    public boolean removeZone(UUID id) {
        boolean removed = zones.removeIf(zone -> zone.getId().equals(id));
        if (removed) {
            cachedZones = List.copyOf(zones);
            setDirty();
        }
        return removed;
    }

    public static Optional<DifficultyZone> findCachedZone(String dimensionKey, double x, double y, double z) {
        for (DifficultyZone zone : cachedZones) {
            if (zone.contains(dimensionKey, x, y, z)) {
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    public static void load(ServerLevel overworld) {
        cachedZones = List.copyOf(get(overworld.getServer()).zones);
    }

    public static void clearCache() {
        cachedZones = List.of();
    }

    public Optional<DifficultyZone> findZone(String dimensionKey, double x, double y, double z) {
        for (DifficultyZone zone : zones) {
            if (zone.contains(dimensionKey, x, y, z)) {
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    public static DifficultyZonePersistentState get(MinecraftServer server) {
        DifficultyZonePersistentState state = server.getDataStorage().computeIfAbsent(TYPE);
        if (!state.legacyImported) {
            importLegacyZones(server, state);
            state.legacyImported = true;
            state.setDirty();
        }
        return state;
    }

    private static void importLegacyZones(MinecraftServer server, DifficultyZonePersistentState state) {
        Path legacyFile = server.getWorldPath(LevelResource.DATA).resolve(LEGACY_FILE_NAME);
        if (!Files.exists(legacyFile)) {
            return;
        }
        try {
            CompoundTag legacyData = NbtIo.readCompressed(legacyFile, NbtAccounter.unlimitedHeap()).getCompoundOrEmpty("data");
            state.zones.addAll(fromNbt(legacyData).zones);
        } catch (IOException e) {
            RpgDifficultyMain.LOGGER.error("Failed to import legacy difficulty zones from {}", legacyFile, e);
        }
    }
}
