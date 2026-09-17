package net.rpgdifficulty.zone;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DifficultyZonePersistentState extends PersistentState {

    private final List<DifficultyZone> zones = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for (DifficultyZone zone : zones) {
            list.add(zone.toNbt());
        }
        nbt.put("Zones", list);
        return nbt;
    }

    public static PersistentState.Type<DifficultyZonePersistentState> getPersistentStateType() {
        return new PersistentState.Type<>(DifficultyZonePersistentState::new, (nbt, registryLookup) -> fromNbt(nbt), null);
    }

    public static DifficultyZonePersistentState fromNbt(NbtCompound nbt) {
        DifficultyZonePersistentState state = new DifficultyZonePersistentState();
        NbtList list = nbt.getList("Zones", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            state.zones.add(DifficultyZone.fromNbt(list.getCompound(i)));
        }
        return state;
    }

    public List<DifficultyZone> getZones() {
        return zones;
    }

    public void addZone(DifficultyZone zone) {
        zones.add(zone);
        markDirty();
    }

    public boolean removeZone(UUID id) {
        boolean removed = zones.removeIf(zone -> zone.getId().equals(id));
        if (removed) {
            markDirty();
        }
        return removed;
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
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        return persistentStateManager.getOrCreate(getPersistentStateType(), "rpgdifficulty_zones");
    }
}
