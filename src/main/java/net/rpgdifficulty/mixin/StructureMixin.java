package net.rpgdifficulty.mixin;

import java.util.Iterator;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(Structure.class)
public class StructureMixin {

    @Inject(method = "spawnEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/Structure;getEntity(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void spawnEntities(ServerWorldAccess serverWorldAccess, BlockPos pos, BlockMirror blockMirror,
            BlockRotation blockRotation, BlockPos pivot, @Nullable BlockBox area, boolean bl, CallbackInfo info,
            Iterator<Structure.StructureEntityInfo> var8, Structure.StructureEntityInfo structureEntityInfo,
            CompoundTag compoundTag, Vec3d vec3d, Vec3d vec3d2, ListTag listTag) {

        getEntity(serverWorldAccess, compoundTag).ifPresent((entity) -> {
            float f = entity.applyMirror(blockMirror);
            f += entity.yaw - entity.applyRotation(blockRotation);
            entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.pitch);
            if (bl && entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) entity;
                mobEntity.initialize(serverWorldAccess, serverWorldAccess.getLocalDifficulty(new BlockPos(vec3d2)),
                        SpawnReason.STRUCTURE, (EntityData) null, compoundTag);
                ServerWorld world = serverWorldAccess.toServerWorld();
                MobStrengthener.changeAttributes(mobEntity, world);
            }
            serverWorldAccess.spawnEntityAndPassengers(entity);
        });
        info.cancel();
    }

    @Shadow
    private static Optional<Entity> getEntity(ServerWorldAccess serverWorldAccess, CompoundTag compoundTag) {
        return null;
    }
}