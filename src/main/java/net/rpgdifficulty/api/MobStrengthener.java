package net.rpgdifficulty.api;

import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
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

    private static final RandomSource random = RandomSource.createThreadSafe();

    // Bosses are untouched by height and dimension check
    // If entity != null, must be PersistentProjectileEntity and will only set the damage
    public static void changeAttributes(Mob mobEntity, ServerLevel world, @Nullable AbstractArrow persistentProjectileEntity, boolean isBossMob) {
        if (isBossMob && !RpgDifficultyMain.CONFIG.affectBosses) {
            return;
        }
        if (!isExcluded(mobEntity.getType())) {
            if (mobEntity.isBaby() && mobEntity instanceof AgeableMob && !RpgDifficultyMain.CONFIG.affectAnimalBabies) {
                return;
            }

            DifficultyZone difficultyZone = DifficultyZonePersistentState.findCachedZone(world.dimension().location().toString(), mobEntity.getX(), mobEntity.getY(), mobEntity.getZ())
                    .orElse(null);

            DimensionDifficulty difficulty = DifficultyLoader.get(world.dimension().location().toString());

            // Factor
            double mobHealthFactor = difficulty.startingFactor();
            double mobDamageFactor = difficulty.startingFactor();
            double mobProtectionFactor = difficulty.startingFactor();
            double dynamicFactor = difficulty.startingFactor();
            // Unused
            double mobSpeedFactor = 1.0D;

            // Entity Values
            double mobHealth = mobEntity.getAttributeBaseValue(Attributes.MAX_HEALTH);
            // Check if hasAttributes necessary
            double mobDamage = 0.0F;
            double mobProtection = 0.0F;
            double mobSpeed = 0.0F;
            boolean hasAttackDamageAttribute = mobEntity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE);
            boolean hasArmorAttribute = mobEntity.getAttributes().hasAttribute(Attributes.ARMOR);
            boolean hasMovementSpeedAttribute = mobEntity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED);
            if (hasAttackDamageAttribute) {
                mobDamage = mobEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            }
            if (hasArmorAttribute) {
                mobProtection = mobEntity.getAttributeBaseValue(Attributes.ARMOR);
            }
            if (hasMovementSpeedAttribute) {
                mobSpeed = mobEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
            }

            if (difficultyZone != null) {
                mobHealthFactor = difficultyZone.getFactor();
                mobDamageFactor = difficultyZone.getFactor();
                mobProtectionFactor = difficultyZone.getFactor();
            } else {

                // Distance, Time, Height
                int spawnX = difficulty.distanceCoordinatesX() != null ? difficulty.distanceCoordinatesX() : world.getSharedSpawnPos().getX();
                int spawnZ = difficulty.distanceCoordinatesZ() != null ? difficulty.distanceCoordinatesZ() : world.getSharedSpawnPos().getZ();

                float worldSpawnDistance = Mth.sqrt((float) mobEntity.distanceToSqr(spawnX, mobEntity.getY(), spawnZ));
                int worldTime = (int) world.getGameTime();
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
                    if (!isBossMob && RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.level().dimension() != Level.OVERWORLD) {
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
                    if (!isBossMob && RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.level().dimension() != Level.OVERWORLD) {
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
                        if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.level().dimension() != Level.OVERWORLD) {
                            spawnHeightDivided = 0;
                        }
                        spawnHeightDivided = Mth.abs(spawnHeightDivided);
                        mobHealthFactor += spawnHeightDivided * difficulty.heightFactor();
                        mobDamageFactor += spawnHeightDivided * difficulty.heightFactor();
                        mobProtectionFactor += spawnHeightDivided * difficulty.heightFactor();
                    }
                }

                // Dynamic Boss Modification
                if (isBossMob && RpgDifficultyMain.CONFIG.dynamicBossModification) {
                    List<Player> list = Lists.newArrayList();

                    for (Player player : WorldPlayerTracker.getPlayers(world)) {
                        if (new AABB(mobEntity.blockPosition()).inflate(RpgDifficultyMain.CONFIG.bossDistance).contains(player.getX(), player.getY(), player.getZ())) {
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

            AttributeSupplier mobEntityDefaultAttributes = DefaultAttributeRegistryAccess.getRegistry().get(mobEntity.getType());
            boolean canStrengthen = mobEntityDefaultAttributes != null && mobHealth - mobEntityDefaultAttributes.getBaseValue(Attributes.MAX_HEALTH) * mobHealthFactor < 0.1D;

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
                if (RpgDifficultyMain.CONFIG.allowSpecialZombie && !mobEntity.isBaby() && mobEntity instanceof Zombie) {
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
                    RpgDifficultyMain.LOGGER.info(BuiltInRegistries.ENTITY_TYPE.getKey(persistentProjectileEntity.getType()).toString() + "; DamageFactor: " + mobDamageFactor);
                } else {
                    RpgDifficultyMain.LOGGER.info(BuiltInRegistries.ENTITY_TYPE.getKey(mobEntity.getType()).toString() + "; HealthFactor: " + mobHealthFactor + "; DamageFactor: " + mobDamageFactor + "; Health: "
                            + mobHealth + ";  Old Health: " + mobEntity.getHealth() + "; Default HP: "
                            + (mobEntityDefaultAttributes != null ? mobEntityDefaultAttributes.getBaseValue(Attributes.MAX_HEALTH) : "-"));
                }
            }

            if (persistentProjectileEntity != null) {
                persistentProjectileEntity.setBaseDamage(persistentProjectileEntity.getBaseDamage() * mobDamageFactor);
            } else
                // Check if mob already has increased strength
                if (canStrengthen) {
                    // Set Values
                    mobEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(mobHealth);
                    mobEntity.heal(mobEntity.getMaxHealth());
                    if (hasAttackDamageAttribute) {
                        mobEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(mobDamage);
                    }
                    if (hasArmorAttribute) {
                        mobEntity.getAttribute(Attributes.ARMOR).setBaseValue(mobProtection);
                    }
                    if (hasMovementSpeedAttribute) {
                        mobEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(mobSpeed);
                    }

                    setMobHealthMultiplier(mobEntity, (float) mobHealthFactor);
                }
        }
    }

    public static boolean isExcluded(EntityType<?> entityType) {
        return RpgDifficultyMain.CONFIG.excludedEntity.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
    }

    public static double getDamageFactor(Entity entity) {
        if (!isExcluded(entity.getType())) {

            DifficultyZone difficultyZone = DifficultyZonePersistentState.findCachedZone(entity.level().dimension().location().toString(), entity.getX(), entity.getY(), entity.getZ())
                    .orElse(null);

            if (difficultyZone != null) {
                return difficultyZone.getFactor();
            }

            DimensionDifficulty difficulty = DifficultyLoader.get(entity.level().dimension().location().toString());

            double mobDamageFactor = difficulty.startingFactor();
            int spawnX = difficulty.distanceCoordinatesX() != null ? difficulty.distanceCoordinatesX() : ((ServerLevel) entity.level()).getSharedSpawnPos().getX();
            int spawnZ = difficulty.distanceCoordinatesZ() != null ? difficulty.distanceCoordinatesZ() : ((ServerLevel) entity.level()).getSharedSpawnPos().getZ();

            float worldSpawnDistance = Mth.sqrt((float) entity.distanceToSqr(spawnX, entity.getY(), spawnZ));
            int worldTime = (int) entity.level().getGameTime();

            if (difficulty.increasingDistance() != 0) {
                if ((int) worldSpawnDistance <= difficulty.startingDistance()) {
                    worldSpawnDistance = 0;
                } else {
                    worldSpawnDistance -= difficulty.startingDistance();
                }
                int spawnDistanceDivided = (int) worldSpawnDistance / difficulty.increasingDistance();
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && entity.level().dimension() != Level.OVERWORLD) {
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
                if (RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && entity.level().dimension() != Level.OVERWORLD) {
                    timeDivided = 0;
                }
                mobDamageFactor += timeDivided * difficulty.timeFactor();
            }

            double maxFactor = difficulty.maxFactorDamage();
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }
            if (entity instanceof Creeper) {
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

    public static int getXpToDropAddition(Mob mobEntity, ServerLevel world, int original) {
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

    public static void dropMoreLoot(Mob mobEntity, LootTable lootTable, LootParams lootContextParameterSet) {
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

                if (mobEntity.level().getRandom().nextFloat() <= dropChance) {
                    List<ItemStack> list = lootTable.getRandomItems(lootContextParameterSet);
                    for (ItemStack itemStack : list) {
                        if (mobEntity.level().getRandom().nextFloat() >= RpgDifficultyMain.CONFIG.chanceForEachItem) {
                            continue;
                        }
                        itemStack.grow((int) (itemStack.getCount() * dropChance));
                        mobEntity.spawnAtLocation(itemStack);
                    }
                }
            }
        }

    }

    public static void setMobHealthMultiplier(Mob mobEntity, float multiplier) {
        ((EntityAccess) mobEntity).setMobHealthMultiplier(multiplier);
    }

    public static float getMobHealthMultiplier(Mob mobEntity) {
        return ((EntityAccess) mobEntity).getMobHealthMultiplier();
    }

}
