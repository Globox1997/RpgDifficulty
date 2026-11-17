package net.rpgdifficulty.config;

import me.shedaniel.autoconfig.AutoConfigClient;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.rpgdifficulty.access.ScreenAccess;

public class RpgDifficultyConfigScreen {

    public static Screen create(Screen parent) {
        ConfigScreenProvider<RpgDifficultyConfig> supplier = (ConfigScreenProvider<RpgDifficultyConfig>) AutoConfigClient.getConfigScreen(RpgDifficultyConfig.class, parent);
        supplier.setBuildFunction(builder -> {
            builder.setAfterInitConsumer(screen -> {
                Minecraft minecraftClient = Minecraft.getInstance();
                if (minecraftClient.level != null) {
                    ((ScreenAccess) screen).addAnotherDrawable(new ConfigClock(minecraftClient, screen.width - 35, 14));
                }
            });
            return builder.build();
        });
        return supplier.get();
    }
}
