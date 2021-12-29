package net.rpgdifficulty.api;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.EntityAccess;

public class MobStrengthener {

    // Todo: Instead of having multiple voids, reduce to one

    public static void changeAttributes(MobEntity mobEntity, ServerWorld world) {
        if (!RpgDifficultyMain.CONFIG.excluded_entity.contains(mobEntity.getType().toString().replace("entity.", ""))) {
            // Factor
            double mobHealthFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double mobDamageFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double mobProtectionFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double mobSpeedFactor = RpgDifficultyMain.CONFIG.startingFactor;

            // Distance, Time, Height
            float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(world.getSpawnPos().getX(), world.getSpawnPos().getY(), world.getSpawnPos().getZ()));
            int worldTime = (int) world.getTime();
            int worldSpawnHeight = (int) mobEntity.getY();

            // Entity Values
            double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            // Check if hasAttributes necessary
            double mobDamage = 0.0F;
            double mobProtection = 0.0F;
            double mobSpeed = 0.0F;
            boolean hasAttackDamageAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            boolean hasArmorAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR);
            boolean hasMovementSpeedAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
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
            // Distance
            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                if ((int) worldSpawnDistance <= RpgDifficultyMain.CONFIG.startingDistance)
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= RpgDifficultyMain.CONFIG.startingDistance;
                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
                mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
                mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
            }
            // Time
            if (RpgDifficultyMain.CONFIG.increasingTime != 0) {
                if (worldTime <= RpgDifficultyMain.CONFIG.startingTime * 1200)
                    worldTime = 0;
                else
                    worldTime -= RpgDifficultyMain.CONFIG.startingTime * 1200;
                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                if (RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    timeDivided = 0;
                }
                mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
                mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
                mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
            }
            // Height
            if (RpgDifficultyMain.CONFIG.heightDistance != 0) {
                int spawnHeightDivided = (worldSpawnHeight - RpgDifficultyMain.CONFIG.startingHeight) / RpgDifficultyMain.CONFIG.heightDistance;
                if (!RpgDifficultyMain.CONFIG.positiveHeightIncreasion && spawnHeightDivided > 0)
                    spawnHeightDivided = 0;
                if (!RpgDifficultyMain.CONFIG.negativeHeightIncreasion && spawnHeightDivided < 0)
                    spawnHeightDivided = 0;
                if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnHeightDivided = 0;
                }
                spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                mobHealthFactor += spawnHeightDivided * RpgDifficultyMain.CONFIG.heightFactor;
                mobDamageFactor += spawnHeightDivided * RpgDifficultyMain.CONFIG.heightFactor;
                mobProtectionFactor += spawnHeightDivided * RpgDifficultyMain.CONFIG.heightFactor;
            }

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

            // Test purpose
            if (RpgDifficultyMain.CONFIG.hudTesting) {
                System.out.println(Registry.ENTITY_TYPE.getId(mobEntity.getType()).toString() + "; HealthFactor: " + mobHealthFactor + "; DamageFactor: " + mobDamageFactor);
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

    // Unused
    public static void changeOnlyHealthAttribute(MobEntity mobEntity, ServerWorld world) {
        if (!RpgDifficultyMain.CONFIG.excluded_entity.contains(mobEntity.getType().toString().replace("entity.", ""))) {
            double mobHealthFactor = RpgDifficultyMain.CONFIG.startingFactor;
            ServerWorld serverWorld = (ServerWorld) world.toServerWorld();

            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(), serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
                if ((int) worldSpawnDistance <= RpgDifficultyMain.CONFIG.startingDistance)
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= RpgDifficultyMain.CONFIG.startingDistance;
                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
            }
            if (RpgDifficultyMain.CONFIG.increasingTime != 0) {
                int worldTime = (int) world.toServerWorld().getTime();
                if (worldTime <= RpgDifficultyMain.CONFIG.startingTime * 1200)
                    worldTime = 0;
                else
                    worldTime -= RpgDifficultyMain.CONFIG.startingTime * 1200;
                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                if (RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    timeDivided = 0;
                }
                mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
            }
            if (RpgDifficultyMain.CONFIG.heightDistance != 0) {
                int spawnHeightDivided = ((int) mobEntity.getY() - RpgDifficultyMain.CONFIG.startingHeight) / RpgDifficultyMain.CONFIG.heightDistance;
                if (!RpgDifficultyMain.CONFIG.positiveHeightIncreasion && spawnHeightDivided > 0)
                    spawnHeightDivided = 0;
                if (!RpgDifficultyMain.CONFIG.negativeHeightIncreasion && spawnHeightDivided < 0)
                    spawnHeightDivided = 0;
                if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnHeightDivided = 0;
                }
                spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                mobHealthFactor += spawnHeightDivided * RpgDifficultyMain.CONFIG.heightFactor;
            }

            double maxFactor = RpgDifficultyMain.CONFIG.maxFactorHealth;
            if (mobHealthFactor > maxFactor) {
                mobHealthFactor = maxFactor;
            }

            double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            mobHealth *= mobHealthFactor;
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
            mobEntity.heal(mobEntity.getMaxHealth());
        }
    }

    public static void changeOnlyDamageAttribute(MobEntity mobEntity, ServerWorld world, Entity entity) {
        if (!RpgDifficultyMain.CONFIG.excluded_entity.contains(mobEntity.getType().toString().replace("entity.", ""))) {
            double mobDamageFactor = RpgDifficultyMain.CONFIG.startingFactor;
            float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(world.getSpawnPos().getX(), world.getSpawnPos().getY(), world.getSpawnPos().getZ()));
            int worldTime = (int) world.getTime();

            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                if ((int) worldSpawnDistance <= RpgDifficultyMain.CONFIG.startingDistance)
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= RpgDifficultyMain.CONFIG.startingDistance;
                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
            }
            if (RpgDifficultyMain.CONFIG.increasingTime != 0) {
                if (worldTime <= RpgDifficultyMain.CONFIG.startingTime * 1200)
                    worldTime = 0;
                else
                    worldTime -= RpgDifficultyMain.CONFIG.startingTime * 1200;
                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                if (RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    timeDivided = 0;
                }
                mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
            }
            if (RpgDifficultyMain.CONFIG.heightDistance != 0) {
                int spawnHeightDivided = ((int) mobEntity.getY() - RpgDifficultyMain.CONFIG.startingHeight) / RpgDifficultyMain.CONFIG.heightDistance;
                if (!RpgDifficultyMain.CONFIG.positiveHeightIncreasion && spawnHeightDivided > 0)
                    spawnHeightDivided = 0;
                if (!RpgDifficultyMain.CONFIG.negativeHeightIncreasion && spawnHeightDivided < 0)
                    spawnHeightDivided = 0;
                if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnHeightDivided = 0;
                }
                spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                mobDamageFactor += spawnHeightDivided * RpgDifficultyMain.CONFIG.heightFactor;
            }

            double maxFactor = RpgDifficultyMain.CONFIG.maxFactorDamage;
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }
            if (entity instanceof PersistentProjectileEntity) {
                PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) entity;
                persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * mobDamageFactor);
            }

            boolean hasAttackDamageAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (hasAttackDamageAttribute) {
                double mobDamage = mobEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                mobDamage *= mobDamageFactor;
                mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(mobDamage);
            }

        }
    }

    // Untouched by height factor and dimension check
    public static void changeBossAttributes(MobEntity mobEntity, ServerWorld world) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {
            double mobHealthFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double mobProtectionFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double mobDamageFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double dynamicFactor = RpgDifficultyMain.CONFIG.startingFactor;

            ServerWorld serverWorld = (ServerWorld) world;
            float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(), serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
            int worldTime = (int) world.getTime();

            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                if ((int) worldSpawnDistance <= RpgDifficultyMain.CONFIG.startingDistance)
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= RpgDifficultyMain.CONFIG.startingDistance;
                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
            }
            if (RpgDifficultyMain.CONFIG.increasingTime != 0) {
                if (worldTime <= RpgDifficultyMain.CONFIG.startingTime * 1200)
                    worldTime = 0;
                else
                    worldTime -= RpgDifficultyMain.CONFIG.startingTime * 1200;
                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
            }

            if (RpgDifficultyMain.CONFIG.dynamicBossModification) {
                List<PlayerEntity> list = Lists.newArrayList();
                Iterator<? extends PlayerEntity> iterator = world.getPlayers().iterator();

                while (iterator.hasNext()) {
                    PlayerEntity playerEntity = (PlayerEntity) iterator.next();
                    if (new Box(mobEntity.getBlockPos()).expand(128D).contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ())) {
                        if (!playerEntity.isSpectator() && playerEntity.isAlive()) {
                            list.add(playerEntity);
                        }
                    }
                }
                for (int i = 0; i < list.size(); ++i) {
                    dynamicFactor += RpgDifficultyMain.CONFIG.dynamicBossModificator;
                }

            }
            mobHealthFactor *= dynamicFactor;

            double maxFactor = RpgDifficultyMain.CONFIG.bossMaxFactor;
            double maxFactorProtection = RpgDifficultyMain.CONFIG.maxFactorProtection;
            double maxFactorDamage = RpgDifficultyMain.CONFIG.maxFactorDamage;
            if (mobHealthFactor > maxFactor) {
                mobHealthFactor = maxFactor;
            }
            if (mobProtectionFactor > maxFactorProtection) {
                mobProtectionFactor = maxFactorProtection;
            }
            if (maxFactorDamage > maxFactorProtection) {
                maxFactorDamage = maxFactorProtection;
            }

            boolean hasAttackDamageAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            boolean hasArmorAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR);

            double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            double mobDamage = 0.0D;
            double mobProtection = 0.0D;

            if (hasAttackDamageAttribute) {
                mobDamage = mobEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            }
            if (hasArmorAttribute) {
                mobProtection = mobEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            }
            mobHealth *= mobHealthFactor;
            mobProtection *= mobProtectionFactor;
            mobDamage *= mobDamageFactor;
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
            if (hasArmorAttribute) {
                mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(mobProtection);
            }
            if (hasAttackDamageAttribute) {
                mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(mobDamage);
            }
        }
    }

    // Untouched by height factor and dimension check
    public static void changeEnderDragonAttribute(MobEntity mobEntity, ServerWorld world) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {
            double mobHealthFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double dynamicFactor = 1.0D;

            ServerWorld serverWorld = (ServerWorld) mobEntity.getEntityWorld();

            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(), serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
                if ((int) worldSpawnDistance <= RpgDifficultyMain.CONFIG.startingDistance)
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= RpgDifficultyMain.CONFIG.startingDistance;
                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
            }

            if (RpgDifficultyMain.CONFIG.increasingTime != 0) {
                int worldTime = (int) mobEntity.getEntityWorld().getTime();
                if (worldTime <= RpgDifficultyMain.CONFIG.startingTime * 1200)
                    worldTime = 0;
                else
                    worldTime -= RpgDifficultyMain.CONFIG.startingTime * 1200;
                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
            }

            if (RpgDifficultyMain.CONFIG.dynamicBossModification) {
                List<ServerPlayerEntity> list = serverWorld.getPlayers(EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(0.0D, 128.0D, 0.0D, 1000.0D)));
                for (int i = 0; i < list.size(); ++i) {
                    dynamicFactor += RpgDifficultyMain.CONFIG.dynamicBossModificator;
                }
            }
            mobHealthFactor *= dynamicFactor;

            double maxFactor = RpgDifficultyMain.CONFIG.bossMaxFactor;
            if (mobHealthFactor > maxFactor) {
                mobHealthFactor = maxFactor;
            }
            double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            mobHealth *= mobHealthFactor;
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
            mobEntity.heal(mobEntity.getMaxHealth());
        }
    }

}
