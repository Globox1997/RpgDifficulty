package net.rpgdifficulty;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.rpgdifficulty.config.RpgDifficultyConfig;

public class RpgDifficultyMain implements ModInitializer {

    public static RpgDifficultyConfig CONFIG = new RpgDifficultyConfig();
    public static final boolean isNameplateLoaded = FabricLoader.getInstance().isModLoaded("nameplate");

    @Override
    public void onInitialize() {
        AutoConfig.register(RpgDifficultyConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(RpgDifficultyConfig.class).getConfig();
    }

}