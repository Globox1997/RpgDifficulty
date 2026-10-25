package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;

@Mixin(Zombie.class)
public abstract class ZombieEntityMixin extends Monster implements ZombieEntityAccess {

    @Unique
    private static final EntityDataAccessor<Boolean> BIG_ZOMBIE = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);

    public ZombieEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void initDataTrackerMixin(SynchedEntityData.Builder builder, CallbackInfo info) {
        builder.define(BIG_ZOMBIE, false);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromTagMixin(CompoundTag nbt, CallbackInfo info) {
        this.entityData.set(BIG_ZOMBIE, nbt.getBoolean("Big"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToTagMixin(CompoundTag nbt, CallbackInfo info) {
        nbt.putBoolean("Big", this.entityData.get(BIG_ZOMBIE));
    }

    @Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At("HEAD"))
    private void onTrackedDataSetMixin(EntityDataAccessor<?> data, CallbackInfo info) {
        if (BIG_ZOMBIE.equals(data)) {
            this.refreshDimensions();
        }
    }

    @Inject(method = "getDefaultDimensions", at = @At("RETURN"), cancellable = true)
    private void getBaseDimensionsMixin(Pose pose, CallbackInfoReturnable<EntityDimensions> info) {
        if (this.entityData.get(BIG_ZOMBIE)) {
            info.setReturnValue(info.getReturnValue().scale(RpgDifficultyMain.CONFIG.bigZombieSize));
        }
    }

    @Override
    public void setBig() {
        this.entityData.set(BIG_ZOMBIE, true);
        this.reapplyPosition();
        this.refreshDimensions();
    }

    @Override
    public EntityDataAccessor<Boolean> getTrackedDataBoolean() {
        return BIG_ZOMBIE;
    }
}
