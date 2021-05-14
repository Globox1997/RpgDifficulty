package net.rpgdifficulty.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(SpiderEntity.class)
public abstract class SpiderEntityMixin {

    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SkeletonEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
            @Nullable EntityData entityData, @Nullable CompoundTag entityTag, CallbackInfoReturnable<EntityData> info,
            SkeletonEntity skeletonEntity) {
        MobStrengthener.changeOnlyHealthAttribute(skeletonEntity, world.toServerWorld());
    }
}
