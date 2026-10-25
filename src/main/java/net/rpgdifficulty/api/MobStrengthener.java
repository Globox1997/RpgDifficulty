package net.rpgdifficulty.api;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.nameplate.access.MobEntityAccess;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.EntityAccess;
import net.rpgdifficulty.access.ZombieEntityAccess;
import net.rpgdifficulty.data.DifficultyLoader;
import net.rpgdifficulty.data.DimensionDifficulty;
import net.rpgdifficulty.mixin.access.DefaultAttributeRegistryAccess;
import net.rpgdifficulty.util.WorldPlayerTracker;
import net.rpgdifficulty.zone.DifficultyZone;
import net.rpgdifficulty.zone.DifficultyZonePersistentState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobStrengthener {

    private static final Random random = Random.createThreadSafe();

    // Bosses are untouched by height and dimension check
    // If entity != null, must be PersistentProjectileEntity and will only set the damage
    public static void changeAttributes(MobEntity mobEntity, ServerWorld world, @Nullable PersistentProjectileEntity persistentProjectileEntity, boolean isBossMob) {
        if (isBossMob && !RpgDifficultyMain.CONFIG.affectBosses) {
            return;
        }
        if (!isExcluded(mobEntity.getType())) {
            if (mobEntity.isBaby() && mobEntity instanceof PassiveEntity && !RpgDifficultyMain.CONFIG.affectAnimalBabies) {
                return;
            }

            DifficultyZone difficultyZone = DifficultyZonePersistentState.findCachedZone(world.getRegistryKey().getValue().toString(), mobEntity.getX(), mobEntity.getY(), mobEntity.getZ())
                    .orElse(null);

            DimensionDifficulty difficulty = DifficultyLoader.get(world.getRegistryKey().getValue().toString());

            // Factor
            double mobHealthFactor = difficulty.startingFactor();
            double mobDamageFactor = difficulty.startingFactor();
            double mobProtectionFactor = difficulty.startingFactor();
            double dynamicFactor = difficulty.startingFactor();
            // Unused
            double mobSpeedFactor = 1.0D;

            // Entity Values
            double mobHealth = mobEntity.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH);
            // Check if hasAttributes necessary
            double mobDamage = 0.0F;
            double mobProtection = 0.0F;
            double mobSpeed = 0.0F;
            boolean hasAttackDamageAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            boolean hasArmorAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR);
            boolean hasMovementSpeedAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (hasAttackDamageAttribute) {
                mobDamage = mobEntity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            }
            if (hasArmorAttribute) {
                mobProtection = mobEntity.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR);
            }
            if (hasMovementSpeedAttribute) {
                mobSpeed = mobEntity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            }

            if (difficultyZone != null) {
                mobHealthFactor = difficultyZone.getFactor();
                mobDamageFactor = difficultyZone.getFactor();
                mobProtectionFactor = difficultyZone.getFactor();
            } else {

                // Distance, Time, Height
                int spawnX = difficulty.distanceCoordinatesX() != null ? difficulty.distanceCoordinatesX() : world.getSpawnPos().getX();
                int spawnZ = difficulty.distanceCoordinatesZ() != null ? difficulty.distanceCoordinatesZ() : world.getSpawnPos().getZ();

                float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(spawnX, mobEntity.getY(), spawnZ));
                int worldTime = (int) world.getTime();
                int mobSpawnHeight = (int) mobEntity.getY();

                // Value Editing
                // Distance
                if (difficulty.increasingDistance() != 0) {
                    if ((int) worldSpawnDistance <= difficulty.startingDistance()) {
                        worldSpawnDistance = 0;
                    } else {
                        worldSpawnDistance -= difficulty.startingDistance();
                    }
                    int spawnDistanceDivided = (int) worldSpawnDistance / difficulty.increasingDistance();
                    if (!isBossMob && RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.getWorld().getRegistryKey() != World.OVERWORLD) {
                        spawnDistanceDivided = 0;
                    }
                    if (isBossMob) {
                        mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                        mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                        mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                    } else {
                        mobHealthFactor += spawnDistanceDivided * difficulty.distanceFactor();
                        mobDamageFactor += spawnDistanceDivided * difficulty.distanceFactor();
                        mobProtectionFactor += spawnDistanceDivided * difficulty.distanceFactor();
                    }

                }
                // Time
                if (difficulty.increasingTime() != 0) {
                    if (worldTime <= difficulty.startingTime() * 1200) {
                        worldTime = 0;
                    } else {
                        worldTime -= difficulty.startingTime() * 1200;
                    }
                    int timeDivided = worldTime / (difficulty.increasingTime() * 1200);
                    if (!isBossMob && RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.getWorld().getRegistryKey() != World.OVERWORLD) {
                        timeDivided = 0;
                    }
                    if (isBossMob) {
                        mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                        mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                        mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                    } else {
                        mobHealthFactor += timeDivided * difficulty.timeFactor();
                        mobDamageFactor += timeDivided * difficulty.timeFactor();
                        mobProtectionFactor += timeDivided * difficulty.timeFactor();
                    }
                }
                // Height
                if (!isBossMob) {
                    if (difficulty.heightDistance() != 0) {
                        int spawnHeightDivided = (mobSpawnHeight - difficulty.startingHeight())
                                / difficulty.heightDistance();
                        if (!difficulty.positiveHeightIncreasion() && spawnHeightDivided > 0) {
                            spawnHeightDivided = 0;
                        }
                        if (!difficulty.negativeHeightIncreasion() && spawnHeightDivided < 0) {
                            spawnHeightDivided = 0;
                        }
                        if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.getWorld().getRegistryKey() != World.OVERWORLD) {
                            spawnHeightDivided = 0;
                        }
                        spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                        mobHealthFactor += spawnHeightDivided * difficulty.heightFactor();
                        mobDamageFactor += spawnHeightDivided * difficulty.heightFactor();
                        mobProtectionFactor += spawnHeightDivided * difficulty.heightFactor();
                    }
                }

                // Dynamic Boss Modification
                if (isBossMob && RpgDifficultyMain.CONFIG.dynamicBossModification) {
                    List<PlayerEntity> list = Lists.newArrayList();

                    for (PlayerEntity player : WorldPlayerTracker.getPlayers(world)) {
                        if (new Box(mobEntity.getBlockPos()).expand(RpgDifficultyMain.CONFIG.bossDistance).contains(player.getX(), player.getY(), player.getZ())) {
                            if (!player.isSpectator() && player.isAlive()) {
                                list.add(player);
                            }
                        }
                    }
                    for (int i = 0; i < list.size(); ++i) {
                        dynamicFactor += RpgDifficultyMain.CONFIG.dynamicBossModificator;
                    }
                    mobHealthFactor *= dynamicFactor;
                }

                // Cutoff
                double maxFactorHealth = difficulty.maxFactorHealth();
                double maxFactorDamage = difficulty.maxFactorDamage();
                double maxFactorProtection = difficulty.maxFactorProtection();
                double maxFactorSpeed = difficulty.maxFactorSpeed();

                if (isBossMob) {
                    maxFactorHealth = RpgDifficultyMain.CONFIG.bossMaxFactor;
                }

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
            }

            // round factor
            mobHealthFactor = Math.round(mobHealthFactor * 100.0D) / 100.0D;
            mobProtectionFactor = Math.round(mobProtectionFactor * 100.0D) / 100.0D;
            mobDamageFactor = Math.round(mobDamageFactor * 100.0D) / 100.0D;
            mobSpeedFactor = Math.round(mobSpeedFactor * 1000.0D) / 1000.0D;

            // Setter
            mobHealth *= mobHealthFactor;
            mobDamage *= mobDamageFactor;
            mobProtection *= mobProtectionFactor;
            mobSpeed *= mobSpeedFactor;

            DefaultAttributeContainer mobEntityDefaultAttributes = DefaultAttributeRegistryAccess.getRegistry().get(mobEntity.getType());
            boolean canStrengthen = mobEntityDefaultAttributes != null && mobHealth - mobEntityDefaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) * mobHealthFactor < 0.1D;

            if (persistentProjectileEntity == null && canStrengthen) {
                // Randomness
                if (RpgDifficultyMain.CONFIG.allowRandomValues) {
                    if (random.nextFloat() <= ((float) RpgDifficultyMain.CONFIG.randomChance / 100F)) {
                        float randomFactor = (float) RpgDifficultyMain.CONFIG.randomFactor / 100F;
                        mobHealth = mobHealth * (1 - randomFactor + (random.nextDouble() * randomFactor * 2F));
                        mobDamage = mobDamage * (1 - randomFactor + (random.nextDouble() * randomFactor * 2F));

                        // round value
                        mobHealth = Math.round(mobHealth * 100.0D) / 100.0D;
                        mobDamage = Math.round(mobDamage * 100.0D) / 100.0D;
                    }
                }

                // Big Zombie
                if (RpgDifficultyMain.CONFIG.allowSpecialZombie && !mobEntity.isBaby() && mobEntity instanceof ZombieEntity) {
                    if (random.nextFloat() < ((float) RpgDifficultyMain.CONFIG.speedZombieChance / 100F)) {
                        mobHealth -= RpgDifficultyMain.CONFIG.speedZombieMalusLifePoints;
                        mobSpeed *= RpgDifficultyMain.CONFIG.speedZombieSpeedFactor;
                    } else if (random.nextFloat() < ((float) RpgDifficultyMain.CONFIG.bigZombieChance / 100F)) {
                        mobSpeed *= RpgDifficultyMain.CONFIG.bigZombieSlownessFactor;
                        mobHealth += RpgDifficultyMain.CONFIG.bigZombieBonusLifePoints;
                        mobDamage += RpgDifficultyMain.CONFIG.bigZombieBonusDamage;
                        ((ZombieEntityAccess) mobEntity).setBig();
                    }
                    // round value
                    mobHealth = Math.round(mobHealth * 100.0D) / 100.0D;
                    mobDamage = Math.round(mobDamage * 100.0D) / 100.0D;
                    mobSpeed = Math.round(mobSpeed * 1000.0D) / 1000.0D;
                }
            }

            // Test purpose
            if (RpgDifficultyMain.CONFIG.hudTesting) {
                if (persistentProjectileEntity != null) {
                    RpgDifficultyMain.LOGGER.info(Registries.ENTITY_TYPE.getId(persistentProjectileEntity.getType()).toString() + "; DamageFactor: " + mobDamageFactor);
                } else {
                    RpgDifficultyMain.LOGGER.info(Registries.ENTITY_TYPE.getId(mobEntity.getType()).toString() + "; HealthFactor: " + mobHealthFactor + "; DamageFactor: " + mobDamageFactor + "; Health: "
                            + mobHealth + ";  Old Health: " + mobEntity.getHealth() + "; Default HP: "
                            + (mobEntityDefaultAttributes != null ? mobEntityDefaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) : "-"));
                }
            }

            if (persistentProjectileEntity != null) {
                persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * mobDamageFactor);
            } else
                // Check if mob already has increased strength
                if (canStrengthen) {
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

                    setMobHealthMultiplier(mobEntity, (float) mobHealthFactor);
                }
        }
    }

    public static boolean isExcluded(EntityType<?> entityType) {
        return RpgDifficultyMain.CONFIG.excludedEntity.contains(Registries.ENTITY_TYPE.getId(entityType).toString());
    }

    public static double getDamageFactor(Entity entity) {
        if (!isExcluded(entity.getType())) {

            DifficultyZone difficultyZone = DifficultyZonePersistentState.findCachedZone(entity.getWorld().getRegistryKey().getValue().toString(), entity.getX(), entity.getY(), entity.getZ())
                    .orElse(null);

            if (difficultyZone != null) {
                return difficultyZone.getFactor();
            }

            DimensionDifficulty difficulty = DifficultyLoader.get(entity.getWorld().getRegistryKey().getValue().toString());

            double mobDamageFactor = difficulty.startingFactor();
            int spawnX = difficulty.distanceCoordinatesX() != null ? difficulty.distanceCoordinatesX() : ((ServerWorld) entity.getWorld()).getSpawnPos().getX();
            int spawnZ = difficulty.distanceCoordinatesZ() != null ? difficulty.distanceCoordinatesZ() : ((ServerWorld) entity.getWorld()).getSpawnPos().getZ();

            float worldSpawnDistance = MathHelper.sqrt((float) entity.squaredDistanceTo(spawnX, entity.getY(), spawnZ));
            int worldTime = (int) entity.getWorld().getTime();

            if (difficulty.increasingDistance() != 0) {
                if ((int) worldSpawnDistance <= difficulty.startingDistance()) {
                    worldSpawnDistance = 0;
                } else {
                    worldSpawnDistance -= difficulty.startingDistance();
                }
                int spawnDistanceDivided = (int) worldSpawnDistance / difficulty.increasingDistance();
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && entity.getWorld().getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobDamageFactor += spawnDistanceDivided * difficulty.distanceFactor();
            }
            if (difficulty.increasingTime() != 0) {
                if (worldTime <= difficulty.startingTime() * 1200) {
                    worldTime = 0;
                } else {
                    worldTime -= difficulty.startingTime() * 1200;
                }
                int timeDivided = worldTime / (difficulty.increasingTime() * 1200);
                if (RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && entity.getWorld().getRegistryKey() != World.OVERWORLD) {
                    timeDivided = 0;
                }
                mobDamageFactor += timeDivided * difficulty.timeFactor();
            }

            double maxFactor = difficulty.maxFactorDamage();
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }
            if (entity instanceof CreeperEntity) {
                mobDamageFactor *= RpgDifficultyMain.CONFIG.creeperExplosionFactor;
            }
            if (mobDamageFactor < 1.0F) {
                mobDamageFactor = 1.0F;
            }
            // round factor
            mobDamageFactor = Math.round(mobDamageFactor * 100.0D) / 100.0D;

            return mobDamageFactor;
        }
        return 1.0D;
    }

    public static int getXpToDropAddition(MobEntity mobEntity, ServerWorld world, int original) {
        if (RpgDifficultyMain.CONFIG.extraXp) {
            float xpFactor = getMobHealthMultiplier(mobEntity);

            float maxXPFactor = RpgDifficultyMain.CONFIG.maxXPFactor;
            if (xpFactor > maxXPFactor) {
                xpFactor = maxXPFactor;
            }
            return (int) (original * xpFactor);
        } else {
            return original;
        }
    }

    public static void dropMoreLoot(MobEntity mobEntity, LootTable lootTable, LootContextParameterSet lootContextParameterSet) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot) {
            float level = 0f;
            if (RpgDifficultyMain.isNameplateLoaded) {
                level = ((MobEntityAccess) mobEntity).getMobRpgLevel();
            } else {
                level = getMobHealthMultiplier(mobEntity);
            }
            if (level > 0.01f) {
                float dropChance = level * RpgDifficultyMain.CONFIG.moreLootChance;
                if (dropChance > RpgDifficultyMain.CONFIG.maxLootChance)
                    dropChance = RpgDifficultyMain.CONFIG.maxLootChance;

                if (mobEntity.getWorld().getRandom().nextFloat() <= dropChance) {
                    List<ItemStack> list = lootTable.generateLoot(lootContextParameterSet);
                    for (ItemStack itemStack : list) {
                        if (mobEntity.getWorld().getRandom().nextFloat() >= RpgDifficultyMain.CONFIG.chanceForEachItem) {
                            continue;
                        }
                        itemStack.increment((int) (itemStack.getCount() * dropChance));
                        mobEntity.dropStack(itemStack);
                    }
                }
            }
        }

    }

    public static void setMobHealthMultiplier(MobEntity mobEntity, float multiplier) {
        ((EntityAccess) mobEntity).setMobHealthMultiplier(multiplier);
    }

    public static float getMobHealthMultiplier(MobEntity mobEntity) {
        return ((EntityAccess) mobEntity).getMobHealthMultiplier();
    }

}
