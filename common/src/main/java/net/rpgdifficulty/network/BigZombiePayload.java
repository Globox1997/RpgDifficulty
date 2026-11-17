package net.rpgdifficulty.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.rpgdifficulty.RpgDifficultyMain;

public record BigZombiePayload(int entityId, boolean big) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BigZombiePayload> ID = new CustomPacketPayload.Type<>(RpgDifficultyMain.identifierOf("big_zombie"));

    public static final StreamCodec<ByteBuf, BigZombiePayload> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, BigZombiePayload::entityId, ByteBufCodecs.BOOL, BigZombiePayload::big,
            BigZombiePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
