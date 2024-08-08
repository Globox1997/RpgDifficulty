package net.rpgdifficulty.mixin;

import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.rpgdifficulty.access.EntityAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements EntityAccess {

    private float mobHealthMultiplier = 1.0f;

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "getXpToDrop", at = @At(value = "RETURN", ordinal = 0))
    private int getXpToDropMixin(int original) {
        return MobStrengthener.getXpToDropAddition((MobEntity) (Object) this, (ServerWorld) this.getWorld(), original);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.mobHealthMultiplier = nbt.getFloat("MobHealthMultiplier");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
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
