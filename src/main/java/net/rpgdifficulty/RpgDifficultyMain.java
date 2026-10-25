package net.rpgdifficulty;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.config.RpgDifficultyConfig;
import net.rpgdifficulty.data.DifficultyLoader;
import net.rpgdifficulty.util.WorldPlayerTracker;
import net.rpgdifficulty.zone.DifficultyZoneCommand;
import net.rpgdifficulty.zone.DifficultyZonePersistentState;
import net.rpgdifficulty.zone.ZoneSyncManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RpgDifficultyMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("RpgDifficulty");

    public static RpgDifficultyConfig CONFIG = new RpgDifficultyConfig();

    public static final boolean isNameplateLoaded = FabricLoader.getInstance().isModLoaded("nameplate");

    public static final TagKey<EntityType<?>> BOSS_ENTITY_TYPES = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", "bosses"));

    @Override
    public void onInitialize() {
        AutoConfig.register(RpgDifficultyConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(RpgDifficultyConfig.class).getConfig();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new DifficultyLoader());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> DifficultyZoneCommand.register(dispatcher, registryAccess));
        PayloadTypeRegistry.clientboundPlay().register(ZoneSyncManager.ZoneSyncPayload.ID, ZoneSyncManager.ZoneSyncPayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ZoneSyncManager.syncToPlayer(handler.getPlayer()));
        ServerLevelEvents.LOAD.register((server, world) -> {
            if (world.dimension() == Level.OVERWORLD) {
                DifficultyZonePersistentState.load(world);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> DifficultyZonePersistentState.clearCache());
        WorldPlayerTracker.init();
    }

}