package net.rpgdifficulty.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.rpgdifficulty.access.ScreenAccess;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    @SuppressWarnings("deprecation")
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigScreenProvider<RpgDifficultyConfig> supplier = (ConfigScreenProvider<RpgDifficultyConfig>) AutoConfig.getConfigScreen(RpgDifficultyConfig.class, parent);
            supplier.setBuildFunction(builder -> {
                builder.setAfterInitConsumer(screen -> {
                    MinecraftClient minecraftClient = MinecraftClient.getInstance();
                    if (minecraftClient.world != null)
                        ((ScreenAccess) screen).addAnotherDrawable(new ConfigClock(minecraftClient, screen.width - 35, 14));
                    // Cloth api
                    // ((ScreenHooks) screen).cloth$addDrawableChild(new ButtonWidget(screen.width - 104, 4, 100, 20, new TranslatableText("text.moderate-loading-screen.testButton"), button -> {
                    // Execute something
                    // }));
                });
                return builder.build();
            });
            return supplier.get();
        };
    }
}
