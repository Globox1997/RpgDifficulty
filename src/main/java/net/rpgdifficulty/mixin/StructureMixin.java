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
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.rpgdifficulty.RpgDifficultyMain;

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

                // Factor
                double mobHealthFactor = 1.0F;
                double mobDamageFactor = 1.0F;
                double mobProtectionFactor = 1.0F;
                double mobSpeedFactor = 1.0F;

                // Distance and Time
                float worldSpawnDistance = MathHelper.sqrt(mobEntity.squaredDistanceTo(world.getSpawnPos().getX(),
                        world.getSpawnPos().getY(), world.getSpawnPos().getZ()));
                int worldTime = (int) world.getTime();

                // Entity Values
                double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                // Check if hasAttributes necessary
                double mobDamage = 0.0F;
                double mobProtection = 0.0F;
                double mobSpeed = 0.0F;
                boolean hasAttackDamageAttribute = mobEntity.getAttributes()
                        .hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                boolean hasArmorAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR);
                boolean hasMovementSpeedAttribute = mobEntity.getAttributes()
                        .hasAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (hasAttackDamageAttribute) {
                    mobDamage = mobEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                }
                if (hasArmorAttribute) {
                    mobProtection = mobEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
                }
                if (hasMovementSpeedAttribute) {
                    mobSpeed = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                }

                // Value Editing
                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
                mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
                mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;

                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
                mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
                mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;

                // Cutoff
                double maxFactorHealth = RpgDifficultyMain.CONFIG.maxFactorHealth;
                double maxFactorDamage = RpgDifficultyMain.CONFIG.maxFactorDamage;
                double maxFactorProtection = RpgDifficultyMain.CONFIG.maxFactorProtection;
                double maxFactorSpeed = RpgDifficultyMain.CONFIG.maxFactorSpeed;

                if (mobHealthFactor > maxFactorHealth) {
                    mobHealthFactor = maxFactorHealth;
                }
                if (mobDamageFactor > maxFactorDamage) {
                    mobDamageFactor = maxFactorDamage;
                }
                if (mobProtectionFactor > maxFactorProtection) {
                    mobProtectionFactor = maxFactorProtection;
                }
                if (mobSpeedFactor > maxFactorSpeed) {
                    mobSpeedFactor = maxFactorSpeed;
                }

                // Setter
                mobHealth *= mobHealthFactor;
                mobDamage *= mobDamageFactor;
                mobProtection *= mobProtectionFactor;
                mobSpeed *= mobSpeedFactor;

                // Randomness
                if (RpgDifficultyMain.CONFIG.allowRandomValues) {
                    if (world.random.nextFloat() <= ((float) RpgDifficultyMain.CONFIG.randomChance / 100F)) {
                        float randomFactor = (float) RpgDifficultyMain.CONFIG.randomFactor / 100F;
                        mobHealth = mobHealth * (1 - randomFactor + (world.random.nextDouble() * randomFactor * 2F));
                        mobDamage = mobDamage * (1 - randomFactor + (world.random.nextDouble() * randomFactor * 2F));
                    }
                }

                // Set Values
                mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
                mobEntity.heal(mobEntity.getMaxHealth());
                if (hasAttackDamageAttribute) {
                    mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(mobDamage);
                }
                if (hasArmorAttribute) {
                    mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(mobProtection);
                }
                if (hasMovementSpeedAttribute) {
                    mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(mobSpeed);
                }

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