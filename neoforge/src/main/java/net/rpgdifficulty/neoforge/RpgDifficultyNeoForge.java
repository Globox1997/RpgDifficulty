package net.rpgdifficulty.neoforge;

import net.rpgdifficulty.network.BigZombiePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.data.DifficultyLoader;
import net.rpgdifficulty.util.WorldPlayerTracker;
import net.rpgdifficulty.zone.DifficultyZoneCommand;
import net.rpgdifficulty.zone.ZoneSyncManager;

@Mod(RpgDifficultyMain.MOD_ID)
public class RpgDifficultyNeoForge {

    public RpgDifficultyNeoForge(IEventBus modBus) {
        RpgDifficultyMain.init();
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> event.registrar("1").optional().playToClient(ZoneSyncManager.ZoneSyncPayload.ID, ZoneSyncManager.ZoneSyncPayload.CODEC)
                .playToClient(BigZombiePayload.ID, BigZombiePayload.CODEC));

        NeoForge.EVENT_BUS.addListener(AddServerReloadListenersEvent.class, event -> event.addListener(DifficultyLoader.ID, new DifficultyLoader()));
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> DifficultyZoneCommand.register(event.getDispatcher(), event.getBuildContext()));
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                RpgDifficultyMain.onPlayerJoin(serverPlayer);
            }
        });
        NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, event -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                RpgDifficultyMain.onLevelLoad(serverLevel);
            }
        });
        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, event -> RpgDifficultyMain.onServerStopped());
        NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, event -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                WorldPlayerTracker.onLevelTick(serverLevel);
            }
        });
    }

}
