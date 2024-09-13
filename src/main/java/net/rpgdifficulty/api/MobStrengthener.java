package net.rpgdifficulty.api;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.rpgdifficulty.mixin.access.DefaultAttributeRegistryAccess;
import org.jetbrains.annotations.Nullable;

public class MobStrengthener {

    // Bosses are untouched by height and dimension check
    // If entity != null, must be PersistentProjectileEntity and will only set the damage
    public static void changeAttributes(MobEntity mobEntity, ServerWorld world, @Nullable PersistentProjectileEntity persistentProjectileEntity, boolean isBossMob) {
        if (isBossMob && !RpgDifficultyMain.CONFIG.affectBosses) {
            return;
        }
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(mobEntity.getType().toString().replace("entity.", "").replace(".", ":"))) {
            if (mobEntity.isBaby() && mobEntity instanceof PassiveEntity && !RpgDifficultyMain.CONFIG.affectAnimalBabies) {
                return;
            }

            Random random = world.getRandom();

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(world.getRegistryKey().getValue().toString())) {
                map = DifficultyLoader.dimensionDifficulty.get(world.getRegistryKey().getValue().toString());
            }

            // Factor
            double mobHealthFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobProtectionFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double dynamicFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            // Unused
            double mobSpeedFactor = 1.0D;

            // Distance, Time, Height
            int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : world.getSpawnPos().getX();
            int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : world.getSpawnPos().getZ();

            float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(spawnX, mobEntity.getY(), spawnZ));
            int worldTime = (int) world.getTime();
            int mobSpawnHeight = (int) mobEntity.getY();

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

            // Value Editing
            // Distance
            if ((map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance)) {
                    worldSpawnDistance = 0;
                } else {
                    worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                }
                int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                if (!isBossMob && RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.getWorld().getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                if (isBossMob) {
                    mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                    mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                    mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                } else {
                    mobHealthFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                    mobDamageFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                    mobProtectionFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                }

            }
            // Time
            if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200) {
                    worldTime = 0;
                } else {
                    worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
                }
                int timeDivided = worldTime / ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) * 1200);
                if (!isBossMob && RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.getWorld().getRegistryKey() != World.OVERWORLD) {
                    timeDivided = 0;
                }
                if (isBossMob) {
                    mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                    mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                    mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                } else {
                    mobHealthFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                    mobDamageFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                    mobProtectionFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                }
            }
            // Height
            if (!isBossMob) {
                if ((map != null ? (int) map.get("heightDistance") : RpgDifficultyMain.CONFIG.heightDistance) != 0) {
                    int spawnHeightDivided = (mobSpawnHeight - (map != null ? (int) map.get("startingHeight") : RpgDifficultyMain.CONFIG.startingHeight))
                            / (map != null ? (int) map.get("heightDistance") : RpgDifficultyMain.CONFIG.heightDistance);
                    if ((map != null ? !(boolean) map.get("positiveHeightIncreasion") : !RpgDifficultyMain.CONFIG.positiveHeightIncreasion) && spawnHeightDivided > 0) {
                        spawnHeightDivided = 0;
                    }
                    if ((map != null ? !(boolean) map.get("negativeHeightIncreasion") : !RpgDifficultyMain.CONFIG.negativeHeightIncreasion) && spawnHeightDivided < 0) {
                        spawnHeightDivided = 0;
                    }
                    if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.getWorld().getRegistryKey() != World.OVERWORLD) {
                        spawnHeightDivided = 0;
                    }
                    spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                    mobHealthFactor += spawnHeightDivided * (map != null ? (double) map.get("heightFactor") : RpgDifficultyMain.CONFIG.heightFactor);
                    mobDamageFactor += spawnHeightDivided * (map != null ? (double) map.get("heightFactor") : RpgDifficultyMain.CONFIG.heightFactor);
                    mobProtectionFactor += spawnHeightDivided * (map != null ? (double) map.get("heightFactor") : RpgDifficultyMain.CONFIG.heightFactor);
                }
            }

            // Dynamic Boss Modification
            if (isBossMob && RpgDifficultyMain.CONFIG.dynamicBossModification) {
                List<PlayerEntity> list = Lists.newArrayList();

                for (PlayerEntity player : world.getPlayers()) {
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
            double maxFactorHealth = map != null ? (double) map.get("maxFactorHealth") : RpgDifficultyMain.CONFIG.maxFactorHealth;
            double maxFactorDamage = map != null ? (double) map.get("maxFactorDamage") : RpgDifficultyMain.CONFIG.maxFactorDamage;
            double maxFactorProtection = map != null ? (double) map.get("maxFactorProtection") : RpgDifficultyMain.CONFIG.maxFactorProtection;
            double maxFactorSpeed = map != null ? (double) map.get("maxFactorSpeed") : RpgDifficultyMain.CONFIG.maxFactorSpeed;

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

            DefaultAttributeContainer mobEntityDefaultAttributes = DefaultAttributeRegistryAccess.getRegistry().get(mobEntity.getType());

            // Test purpose
            if (RpgDifficultyMain.CONFIG.hudTesting) {
                if (persistentProjectileEntity != null) {
                    System.out.println(Registries.ENTITY_TYPE.getId(persistentProjectileEntity.getType()).toString() + "; DamageFactor: " + mobDamageFactor);
                } else {
                    System.out.println(Registries.ENTITY_TYPE.getId(mobEntity.getType()).toString() + "; HealthFactor: " + mobHealthFactor + "; DamageFactor: " + mobDamageFactor + "; Health: "
                            + mobHealth + ";  Old Health: " + mobEntity.getHealth() + "; Default HP: "
                            + (mobEntityDefaultAttributes != null ? mobEntityDefaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) : "-"));
                }
            }

            // Check if mob already has increased strength
            if (mobEntityDefaultAttributes != null && mobHealth - mobEntityDefaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) * mobHealthFactor < 0.1D) {

                if (persistentProjectileEntity != null) {
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * mobDamageFactor);
                } else {
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
    }

    public static double getDamageFactor(Entity entity) {
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(entity.getType().toString().replace("entity.", "").replace(".", ":"))) {

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(entity.getWorld().getRegistryKey().getValue().toString()))
                map = DifficultyLoader.dimensionDifficulty.get(entity.getWorld().getRegistryKey().getValue().toString());

            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : ((ServerWorld) entity.getWorld()).getSpawnPos().getX();
            int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : ((ServerWorld) entity.getWorld()).getSpawnPos().getZ();

            float worldSpawnDistance = MathHelper.sqrt((float) entity.squaredDistanceTo(spawnX, entity.getY(), spawnZ));
            int worldTime = (int) entity.getWorld().getTime();

            if ((map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance)) {
                    worldSpawnDistance = 0;
                } else {
                    worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                }
                int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && entity.getWorld().getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobDamageFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
            }
            if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200) {
                    worldTime = 0;
                } else {
                    worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
                }
                int timeDivided = worldTime / ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) * 1200);
                mobDamageFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
            }

            double maxFactor = map != null ? (double) map.get("maxFactorDamage") : RpgDifficultyMain.CONFIG.maxFactorDamage;
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }
            mobDamageFactor *= RpgDifficultyMain.CONFIG.creeperExplosionFactor;
            if (mobDamageFactor < 1.0F)
                mobDamageFactor = 1.0F;
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
                        if (mobEntity.getWorld().getRandom().nextFloat() < RpgDifficultyMain.CONFIG.chanceForEachItem) {
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
