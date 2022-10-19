package net.rpgdifficulty;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.rpgdifficulty.config.RpgDifficultyConfig;
import net.rpgdifficulty.data.DifficultyLoader;

public class RpgDifficultyMain implements ModInitializer {

    public static RpgDifficultyConfig CONFIG = new RpgDifficultyConfig();

    public static final boolean isNameplateLoaded = FabricLoader.getInstance().isModLoaded("nameplate");

    public static final TagKey<EntityType<?>> BOSS_ENTITY_TYPES = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("c", "bosses"));

    @Override
    public void onInitialize() {
        AutoConfig.register(RpgDifficultyConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(RpgDifficultyConfig.class).getConfig();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DifficultyLoader());
    }

}