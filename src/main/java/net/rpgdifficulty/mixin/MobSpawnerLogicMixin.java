package net.rpgdifficulty.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void updateMixin(CallbackInfo info, World world, BlockPos blockPos, boolean bl, int i,
            CompoundTag compoundTag, Optional<EntityType<?>> optional, ListTag listTag, int j, double g, double h,
            double k, ServerWorld serverWorld, Entity entity) {
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) entity;
            MobStrengthener.changeAttributes(mobEntity, serverWorld);
        }
    }

}
