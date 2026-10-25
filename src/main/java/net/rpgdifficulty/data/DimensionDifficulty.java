package net.rpgdifficulty.data;

import com.google.gson.JsonObject;
import net.rpgdifficulty.config.RpgDifficultyConfig;
import org.jetbrains.annotations.Nullable;

public record DimensionDifficulty(@Nullable Integer distanceCoordinatesX, @Nullable Integer distanceCoordinatesZ, int increasingDistance, double distanceFactor, int increasingTime,
        double timeFactor, int heightDistance, double heightFactor, double maxFactorHealth, double maxFactorDamage, double maxFactorProtection, double maxFactorSpeed, double startingFactor,
        int startingDistance, int startingTime, int startingHeight, boolean positiveHeightIncreasion, boolean negativeHeightIncreasion) {

    public static DimensionDifficulty fromConfig(RpgDifficultyConfig config) {
        return new DimensionDifficulty(null, null, config.increasingDistance, config.distanceFactor, config.increasingTime, config.timeFactor, config.heightDistance, config.heightFactor,
                config.maxFactorHealth, config.maxFactorDamage, config.maxFactorProtection, config.maxFactorSpeed, config.startingFactor, config.startingDistance, config.startingTime,
                config.startingHeight, config.positiveHeightIncreasion, config.negativeHeightIncreasion);
    }

    public static DimensionDifficulty fromJson(JsonObject data, RpgDifficultyConfig config) {
        return new DimensionDifficulty(data.has("distanceCoordinatesX") ? data.get("distanceCoordinatesX").getAsInt() : null,
                data.has("distanceCoordinatesZ") ? data.get("distanceCoordinatesZ").getAsInt() : null,
                data.has("increasingDistance") ? data.get("increasingDistance").getAsInt() : config.increasingDistance,
                data.has("distanceFactor") ? data.get("distanceFactor").getAsDouble() : config.distanceFactor,
                data.has("increasingTime") ? data.get("increasingTime").getAsInt() : config.increasingTime,
                data.has("timeFactor") ? data.get("timeFactor").getAsDouble() : config.timeFactor,
                data.has("heightDistance") ? data.get("heightDistance").getAsInt() : config.heightDistance,
                data.has("heightFactor") ? data.get("heightFactor").getAsDouble() : config.heightFactor,
                data.has("maxFactorHealth") ? data.get("maxFactorHealth").getAsDouble() : config.maxFactorHealth,
                data.has("maxFactorDamage") ? data.get("maxFactorDamage").getAsDouble() : config.maxFactorDamage,
                data.has("maxFactorProtection") ? data.get("maxFactorProtection").getAsDouble() : config.maxFactorProtection,
                data.has("maxFactorSpeed") ? data.get("maxFactorSpeed").getAsDouble() : config.maxFactorSpeed,
                data.has("startingFactor") ? data.get("startingFactor").getAsDouble() : config.startingFactor,
                data.has("startingDistance") ? data.get("startingDistance").getAsInt() : config.startingDistance,
                data.has("startingTime") ? data.get("startingTime").getAsInt() : config.startingTime,
                data.has("startingHeight") ? data.get("startingHeight").getAsInt() : config.startingHeight,
                data.has("positiveHeightIncreasion") ? data.get("positiveHeightIncreasion").getAsBoolean() : config.positiveHeightIncreasion,
                data.has("negativeHeightIncreasion") ? data.get("negativeHeightIncreasion").getAsBoolean() : config.negativeHeightIncreasion);
    }
}
