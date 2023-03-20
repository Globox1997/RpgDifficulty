package net.rpgdifficulty.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.rpgdifficulty.RpgDifficultyMain;

public class DifficultyLoader implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger("RpgDifficulty");

    public static HashMap<String, HashMap<String, Object>> dimensionDifficulty = new HashMap<String, HashMap<String, Object>>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("rpgdifficulty", "difficulty_loader");
    }

    @Override
    public void reload(ResourceManager manager) {

        dimensionDifficulty.clear();
        manager.findResources("difficulty", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                HashMap<String, Object> map = new HashMap<String, Object>();
                // coordinates
                if (data.has("distanceCoordinatesX"))
                    map.put("distanceCoordinatesX", data.get("distanceCoordinatesX").getAsInt());
                if (data.has("distanceCoordinatesY"))
                    map.put("distanceCoordinatesY", data.get("distanceCoordinatesY").getAsInt());
                if (data.has("distanceCoordinatesZ"))
                    map.put("distanceCoordinatesZ", data.get("distanceCoordinatesZ").getAsInt());
                // distance
                if (data.has("increasingDistance"))
                    map.put("increasingDistance", data.get("increasingDistance").getAsInt());
                else
                    map.put("increasingDistance", RpgDifficultyMain.CONFIG.increasingDistance);
                if (data.has("distanceFactor"))
                    map.put("distanceFactor", data.get("distanceFactor").getAsDouble());
                else
                    map.put("distanceFactor", RpgDifficultyMain.CONFIG.distanceFactor);
                // time
                if (data.has("increasingTime"))
                    map.put("increasingTime", data.get("increasingTime").getAsInt());
                else
                    map.put("increasingTime", RpgDifficultyMain.CONFIG.increasingTime);
                if (data.has("timeFactor"))
                    map.put("timeFactor", data.get("timeFactor").getAsDouble());
                else
                    map.put("timeFactor", RpgDifficultyMain.CONFIG.timeFactor);
                // height
                if (data.has("heightDistance"))
                    map.put("heightDistance", data.get("heightDistance").getAsInt());
                else
                    map.put("heightDistance", RpgDifficultyMain.CONFIG.heightDistance);
                if (data.has("heightFactor"))
                    map.put("heightFactor", data.get("heightFactor").getAsDouble());
                else
                    map.put("heightFactor", RpgDifficultyMain.CONFIG.heightFactor);
                // max
                if (data.has("maxFactorHealth"))
                    map.put("maxFactorHealth", data.get("maxFactorHealth").getAsDouble());
                else
                    map.put("maxFactorHealth", RpgDifficultyMain.CONFIG.maxFactorHealth);
                if (data.has("maxFactorDamage"))
                    map.put("maxFactorDamage", data.get("maxFactorDamage").getAsDouble());
                else
                    map.put("maxFactorDamage", RpgDifficultyMain.CONFIG.maxFactorDamage);
                if (data.has("maxFactorProtection"))
                    map.put("maxFactorProtection", data.get("maxFactorProtection").getAsDouble());
                else
                    map.put("maxFactorProtection", RpgDifficultyMain.CONFIG.maxFactorProtection);
                if (data.has("maxFactorSpeed"))
                    map.put("maxFactorSpeed", data.get("maxFactorSpeed").getAsDouble());
                else
                    map.put("maxFactorSpeed", RpgDifficultyMain.CONFIG.maxFactorSpeed);
                // starting
                if (data.has("startingFactor"))
                    map.put("startingFactor", data.get("startingFactor").getAsDouble());
                else
                    map.put("startingFactor", RpgDifficultyMain.CONFIG.startingFactor);
                if (data.has("startingDistance"))
                    map.put("startingDistance", data.get("startingDistance").getAsInt());
                else
                    map.put("startingDistance", RpgDifficultyMain.CONFIG.startingDistance);
                if (data.has("startingTime"))
                    map.put("startingTime", data.get("startingTime").getAsInt());
                else
                    map.put("startingTime", RpgDifficultyMain.CONFIG.startingTime);
                if (data.has("startingHeight"))
                    map.put("startingHeight", data.get("startingHeight").getAsInt());
                else
                    map.put("startingHeight", RpgDifficultyMain.CONFIG.startingHeight);
                // height check
                if (data.has("positiveHeightIncreasion"))
                    map.put("positiveHeightIncreasion", data.get("positiveHeightIncreasion").getAsBoolean());
                else
                    map.put("positiveHeightIncreasion", RpgDifficultyMain.CONFIG.positiveHeightIncreasion);
                if (data.has("negativeHeightIncreasion"))
                    map.put("negativeHeightIncreasion", data.get("negativeHeightIncreasion").getAsBoolean());
                else
                    map.put("negativeHeightIncreasion", RpgDifficultyMain.CONFIG.negativeHeightIncreasion);

                dimensionDifficulty.put(data.get("dimension").getAsString(), map);

            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}