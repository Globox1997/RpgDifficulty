package net.rpgdifficulty.mixin;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.access.EntityAccess;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity implements EntityAccess {

    @Unique
    private float mobHealthMultiplier = 1.0f;

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(ValueInput input, CallbackInfo info) {
        this.mobHealthMultiplier = input.getFloatOr("MobHealthMultiplier", 1.0f);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(ValueOutput output, CallbackInfo info) {
        output.putFloat("MobHealthMultiplier", this.mobHealthMultiplier);
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
