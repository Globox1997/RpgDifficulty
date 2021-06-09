package net.rpgdifficulty.mixin;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import net.rpgdifficulty.access.EntityAccess;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity implements EntityAccess {
    private static final TrackedData<Boolean> BIG_ZOMBIE = DataTracker.registerData(ZombieEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTrackerMixin(CallbackInfo info) {
        this.dataTracker.startTracking(BIG_ZOMBIE, false);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromTagMixin(NbtCompound nbt, CallbackInfo info) {
        this.dataTracker.set(BIG_ZOMBIE, nbt.getBoolean("Big"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToTagMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("Big", this.dataTracker.get(BIG_ZOMBIE));
    }

    @Inject(method = "onTrackedDataSet", at = @At("HEAD"))
    private void onTrackedDataSetMixin(TrackedData<?> data, CallbackInfo info) {
        if (BIG_ZOMBIE.equals(data)) {
            this.calculateDimensions();
        }
    }

    @Override
    public void setBig() {
        this.dataTracker.set(BIG_ZOMBIE, true);
        this.refreshPosition();
        this.calculateDimensions();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.dataTracker.get(BIG_ZOMBIE) ? super.getDimensions(pose).scaled(1.3F) : super.getDimensions(pose);
    }
}
