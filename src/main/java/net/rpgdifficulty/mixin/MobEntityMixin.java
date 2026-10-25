package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.access.EntityAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity implements EntityAccess {

    @Unique
    private float mobHealthMultiplier = 1.0f;

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "getBaseExperienceReward", at = @At("RETURN"), cancellable = true)
    private void getXpToDropMixin(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(MobStrengthener.getXpToDropAddition((Mob) (Object) this, (ServerLevel) this.level(), info.getReturnValue()));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(CompoundTag nbt, CallbackInfo info) {
        if (nbt.contains("MobHealthMultiplier")) {
            this.mobHealthMultiplier = nbt.getFloat("MobHealthMultiplier");
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(CompoundTag nbt, CallbackInfo info) {
        nbt.putFloat("MobHealthMultiplier", this.mobHealthMultiplier);
    }

    @Override
    public void setMobHealthMultiplier(float multiplier) {
        this.mobHealthMultiplier = multiplier;
    }

    @Override
    public float getMobHealthMultiplier() {
        return this.mobHealthMultiplier;
    }

}
