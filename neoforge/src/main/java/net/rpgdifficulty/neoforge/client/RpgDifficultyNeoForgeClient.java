package net.rpgdifficulty.neoforge.client;

import net.rpgdifficulty.network.BigZombiePayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.rpgdifficulty.RpgDifficultyClient;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.config.RpgDifficultyConfigScreen;
import net.rpgdifficulty.neoforge.event.ZoneEnterEvent;
import net.rpgdifficulty.zone.ClientZoneTracker;
import net.rpgdifficulty.zone.ZoneSyncManager;

@Mod(value = RpgDifficultyMain.MOD_ID, dist = Dist.CLIENT)
public class RpgDifficultyNeoForgeClient {

    public RpgDifficultyNeoForgeClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterGuiLayersEvent.class, event -> event.registerAboveAll(RpgDifficultyClient.TESTING_HUD_ID, RpgDifficultyClient::renderTestingHud));
        modBus.addListener(RegisterClientPayloadHandlersEvent.class, event -> {
            event.register(ZoneSyncManager.ZoneSyncPayload.ID, (payload, context) -> ClientZoneTracker.handlePayload(payload));
            event.register(BigZombiePayload.ID, (payload, context) -> RpgDifficultyClient.handleBigZombie(payload));
        });

        ClientZoneTracker.setEnterHandler(zone -> NeoForge.EVENT_BUS.post(new ZoneEnterEvent(zone)));
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> ClientZoneTracker.onClientTick(Minecraft.getInstance()));
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event -> ClientZoneTracker.reset());

        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> RpgDifficultyConfigScreen.create(parent));
    }

}
