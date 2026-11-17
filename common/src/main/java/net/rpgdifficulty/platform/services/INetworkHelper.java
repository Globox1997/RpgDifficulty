package net.rpgdifficulty.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper {

    boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type);

    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

}
