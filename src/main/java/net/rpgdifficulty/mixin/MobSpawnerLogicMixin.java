package net.rpgdifficulty.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.EntityAccess;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void updateMixin(CallbackInfo info, World world, BlockPos blockPos, boolean bl, int i,
            CompoundTag compoundTag, Optional<EntityType<?>> optional, ListTag listTag, int j, double g, double h,
            double k, ServerWorld serverWorld, Entity entity) {
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) entity;
            if (!RpgDifficultyMain.CONFIG.excluded_entity
                    .contains(mobEntity.getType().toString().replace("entity.", ""))) {
                // Factor
                double mobHealthFactor = 1.0F;
                double mobDamageFactor = 1.0F;
                double mobProtectionFactor = 1.0F;
                double mobSpeedFactor = 1.0F;

                // Distance and Time
                float worldSpawnDistance = MathHelper.sqrt(mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                        serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
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

                // Big Zombie
                if (RpgDifficultyMain.CONFIG.allowSpecialZombie && mobEntity instanceof ZombieEntity) {
                    if (world.random.nextFloat() < ((float) RpgDifficultyMain.CONFIG.speedZombieChance / 100F)) {
                        mobHealth -= RpgDifficultyMain.CONFIG.speedZombieMalusLifePoints;
                        mobSpeed *= RpgDifficultyMain.CONFIG.speedZombieSpeedFactor;
                    } else if (world.random.nextFloat() < ((float) RpgDifficultyMain.CONFIG.bigZombieChance / 100F)) {
                        mobSpeed *= RpgDifficultyMain.CONFIG.bigZombieSlownessFactor;
                        mobHealth += RpgDifficultyMain.CONFIG.bigZombieBonusLifePoints;
                        mobDamage += RpgDifficultyMain.CONFIG.bigZombieBonusDamage;
                        ((EntityAccess) mobEntity).setBig();
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
        }
        System.out.println(entity);
    }

}
