package net.rpgdifficulty.fabric.client;

import net.rpgdifficulty.network.BigZombiePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.rpgdifficulty.RpgDifficultyClient;
import net.rpgdifficulty.zone.ClientZoneEvents;
import net.rpgdifficulty.zone.ClientZoneTracker;
import net.rpgdifficulty.zone.ZoneSyncManager;

@Environment(EnvType.CLIENT)
public class RpgDifficultyFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(RpgDifficultyClient.TESTING_HUD_ID, RpgDifficultyClient::renderTestingHud);

        ClientZoneTracker.setEnterHandler(zone -> ClientZoneEvents.ENTER.invoker().onEnter(zone));
        ClientPlayNetworking.registerGlobalReceiver(ZoneSyncManager.ZoneSyncPayload.ID, (payload, context) -> context.client().execute(() -> ClientZoneTracker.handlePayload(payload)));
        ClientPlayNetworking.registerGlobalReceiver(BigZombiePayload.ID, (payload, context) -> context.client().execute(() -> RpgDifficultyClient.handleBigZombie(payload)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientZoneTracker.reset());
        ClientTickEvents.END_CLIENT_TICK.register(ClientZoneTracker::onClientTick);
    }

}
