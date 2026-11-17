package net.rpgdifficulty.fabric;

import net.rpgdifficulty.network.BigZombiePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.data.DifficultyLoader;
import net.rpgdifficulty.util.WorldPlayerTracker;
import net.rpgdifficulty.zone.DifficultyZoneCommand;
import net.rpgdifficulty.zone.ZoneSyncManager;

public class RpgDifficultyFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        RpgDifficultyMain.init();
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(DifficultyLoader.ID, new DifficultyLoader());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> DifficultyZoneCommand.register(dispatcher, registryAccess));
        PayloadTypeRegistry.clientboundPlay().register(ZoneSyncManager.ZoneSyncPayload.ID, ZoneSyncManager.ZoneSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BigZombiePayload.ID, BigZombiePayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> RpgDifficultyMain.onPlayerJoin(handler.getPlayer()));
        ServerLevelEvents.LOAD.register((server, world) -> RpgDifficultyMain.onLevelLoad(world));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> RpgDifficultyMain.onServerStopped());
        ServerTickEvents.END_LEVEL_TICK.register(WorldPlayerTracker::onLevelTick);
    }

}
