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
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.EntityAccess;

public class MobStrengthener {

    // Todo: Instead of having multiple voids, reduce to one

    public static void changeAttributes(MobEntity mobEntity, ServerWorld world) {
        if (!RpgDifficultyMain.CONFIG.excluded_entity.contains(mobEntity.getType().toString().replace("entity.", ""))) {
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

    public static void changeOnlyHealthAttribute(MobEntity mobEntity, ServerWorld world) {
        if (!RpgDifficultyMain.CONFIG.excluded_entity.contains(mobEntity.getType().toString().replace("entity.", ""))) {
            double mobHealthFactor = 1.0F;
            ServerWorld serverWorld = (ServerWorld) world.toServerWorld();
            float worldSpawnDistance = MathHelper.sqrt(mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                    serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
            int worldTime = (int) world.toServerWorld().getTime();

            int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
            mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;

            int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
            mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;

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
            double mobDamageFactor = 1.0F;
            float worldSpawnDistance = MathHelper.sqrt(mobEntity.squaredDistanceTo(world.getSpawnPos().getX(),
                    world.getSpawnPos().getY(), world.getSpawnPos().getZ()));
            int worldTime = (int) world.getTime();

            int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
            mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;

            int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
            mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;

            double maxFactor = RpgDifficultyMain.CONFIG.maxFactorDamage;
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }
            if (entity instanceof PersistentProjectileEntity) {
                PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) entity;
                persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * mobDamageFactor);
            }

            boolean hasAttackDamageAttribute = mobEntity.getAttributes()
                    .hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (hasAttackDamageAttribute) {
                double mobDamage = mobEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                mobDamage *= mobDamageFactor;
                mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(mobDamage);
            }

        }
    }

    public static void changeBossAttributes(MobEntity mobEntity, ServerWorld world) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {
            double mobHealthFactor = 1.0F;
            double mobProtectionFactor = 1.0F;
            double mobDamageFactor = 1.0F;
            double dynamicFactor = 1.0D;

            ServerWorld serverWorld = (ServerWorld) world;
            float worldSpawnDistance = MathHelper.sqrt(mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                    serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
            int worldTime = (int) world.getTime();

            int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
            mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
            mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
            mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;

            int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
            mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
            mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
            mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;

            if (RpgDifficultyMain.CONFIG.dynamicBossModification) {
                List<PlayerEntity> list = Lists.newArrayList();
                Iterator<? extends PlayerEntity> iterator = world.getPlayers().iterator();

                while (iterator.hasNext()) {
                    PlayerEntity playerEntity = (PlayerEntity) iterator.next();
                    if (new Box(mobEntity.getBlockPos()).expand(128D).contains(playerEntity.getX(), playerEntity.getY(),
                            playerEntity.getZ())) {
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

            boolean hasAttackDamageAttribute = mobEntity.getAttributes()
                    .hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
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

    public static void changeEnderDragonAttribute(MobEntity mobEntity, ServerWorld world) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {
            double mobHealthFactor = 1.0F;
            double dynamicFactor = 1.0D;

            ServerWorld serverWorld = (ServerWorld) mobEntity.getEntityWorld();
            float worldSpawnDistance = MathHelper.sqrt(mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                    serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
            int worldTime = (int) mobEntity.getEntityWorld().getTime();

            int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
            mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;

            int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
            mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;

            if (RpgDifficultyMain.CONFIG.dynamicBossModification) {
                List<ServerPlayerEntity> list = serverWorld.getPlayers(
                        EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(0.0D, 128.0D, 0.0D, 1000.0D)));
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
