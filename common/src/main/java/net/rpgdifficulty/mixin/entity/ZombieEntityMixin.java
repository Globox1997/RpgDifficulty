package net.rpgdifficulty.mixin.entity;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;
import net.rpgdifficulty.network.BigZombiePayload;
import net.rpgdifficulty.platform.Services;

@Mixin(Zombie.class)
public abstract class ZombieEntityMixin extends Monster implements ZombieEntityAccess {

    @Unique
    private boolean rpgdifficulty$big = false;

    public ZombieEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromTagMixin(ValueInput input, CallbackInfo info) {
        if (input.getBooleanOr("Big", false)) {
            this.applyBig(true);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToTagMixin(ValueOutput output, CallbackInfo info) {
        output.putBoolean("Big", this.rpgdifficulty$big);
    }

    @Inject(method = "getDefaultDimensions", at = @At("RETURN"), cancellable = true)
    private void getBaseDimensionsMixin(Pose pose, CallbackInfoReturnable<EntityDimensions> info) {
        if (this.rpgdifficulty$big) {
            info.setReturnValue(info.getReturnValue().scale(RpgDifficultyMain.CONFIG.bigZombieSize));
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this.rpgdifficulty$big && Services.network().canSend(player, BigZombiePayload.ID)) {
            player.connection.send(new ClientboundCustomPayloadPacket(new BigZombiePayload(this.getId(), true)));
        }
    }

    @Override
    public void setBig() {
        this.applyBig(true);
        if (this.level() instanceof ServerLevel serverLevel && serverLevel.getServer().isSameThread()) {
            serverLevel.getChunkSource().sendToTrackingPlayersFiltered(this, new ClientboundCustomPayloadPacket(new BigZombiePayload(this.getId(), true)),
                    player -> Services.network().canSend(player, BigZombiePayload.ID));
        }
    }

    @Override
    public boolean isBig() {
        return this.rpgdifficulty$big;
    }

    @Override
    public void applyBig(boolean big) {
        this.rpgdifficulty$big = big;
        this.reapplyPosition();
        this.refreshDimensions();
    }
}
