package net.rpgdifficulty;

import net.rpgdifficulty.api.MobStrengthener;
import net.minecraft.world.InteractionResult;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.config.RpgDifficultyConfig;
import net.rpgdifficulty.platform.Services;
import net.rpgdifficulty.util.WorldPlayerTracker;
import net.rpgdifficulty.zone.DifficultyZonePersistentState;
import net.rpgdifficulty.zone.ZoneSyncManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RpgDifficultyMain {

    public static final String MOD_ID = "rpgdifficulty";

    public static final Logger LOGGER = LogManager.getLogger("RpgDifficulty");

    public static RpgDifficultyConfig CONFIG = new RpgDifficultyConfig();

    public static final boolean isNameplateLoaded = Services.PLATFORM.isModLoaded("nameplate");

    public static final TagKey<EntityType<?>> BOSS_ENTITY_TYPES = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", "bosses"));

    public static void init() {
        AutoConfig.register(RpgDifficultyConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(RpgDifficultyConfig.class).getConfig();
        AutoConfig.getConfigHolder(RpgDifficultyConfig.class).registerSaveListener((holder, config) -> {
            MobStrengthener.clearExcludedCache();
            return InteractionResult.PASS;
        });
    }

    public static Identifier identifierOf(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void onPlayerJoin(ServerPlayer player) {
        ZoneSyncManager.syncToPlayer(player);
    }

    public static void onLevelLoad(ServerLevel level) {
        if (level.dimension() == Level.OVERWORLD) {
            DifficultyZonePersistentState.load(level);
        }
    }

    public static void onServerStopped() {
        DifficultyZonePersistentState.clearCache();
        WorldPlayerTracker.clear();
    }

}
