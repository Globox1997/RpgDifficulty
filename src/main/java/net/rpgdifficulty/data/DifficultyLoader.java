package net.rpgdifficulty.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.rpgdifficulty.RpgDifficultyMain;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DifficultyLoader implements SimpleSynchronousResourceReloadListener {

    private static volatile Map<String, DimensionDifficulty> dimensionDifficulty = Map.of();

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath("rpgdifficulty", "difficulty_loader");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Map<String, DimensionDifficulty> loaded = new HashMap<>();
        manager.listResources("difficulty", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try (InputStream stream = resourceRef.open()) {
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                loaded.put(data.get("dimension").getAsString(), DimensionDifficulty.fromJson(data, RpgDifficultyMain.CONFIG));
            } catch (Exception e) {
                RpgDifficultyMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
        dimensionDifficulty = Map.copyOf(loaded);
    }

    public static DimensionDifficulty get(String dimension) {
        DimensionDifficulty difficulty = dimensionDifficulty.get(dimension);
        return difficulty != null ? difficulty : DimensionDifficulty.fromConfig(RpgDifficultyMain.CONFIG);
    }

}
