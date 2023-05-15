package net.rpgdifficulty.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.nameplate.access.MobEntityAccess;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.EntityAccess;
import net.rpgdifficulty.access.ZombieEntityAccess;
import net.rpgdifficulty.data.DifficultyLoader;

public class MobStrengthener {

    private final static Random random = new Random();

    // Use only on ServerWorld
    public static void changeAttributes(MobEntity mobEntity, World world) {
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(mobEntity.getType().toString().replace("entity.", "").replace(".", ":"))) {

            if (mobEntity.isBaby() && mobEntity instanceof PassiveEntity) {
                return;
            }

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(world.getRegistryKey().getValue().toString())) {
                map = DifficultyLoader.dimensionDifficulty.get(world.getRegistryKey().getValue().toString());
            }

            // Factor
            double mobHealthFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobProtectionFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            // Unused
            double mobSpeedFactor = 1.0D;

            // Distance, Time, Height
            int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : world.getSpawnPos().getX();
            int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : world.getSpawnPos().getZ();

            float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(spawnX, mobEntity.getY(), spawnZ));
            int worldTime = (int) world.getTime();
            int mobSpawnHeight = (int) mobEntity.getY();

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
            if ((map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance))
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobHealthFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                mobDamageFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                mobProtectionFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
            }
            // Time
            if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200)
                    worldTime = 0;
                else
                    worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
                int timeDivided = worldTime / ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) * 1200);
                if (RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    timeDivided = 0;
                }
                mobHealthFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                mobDamageFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                mobProtectionFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
            }
            // Height
            if ((map != null ? (int) map.get("heightDistance") : RpgDifficultyMain.CONFIG.heightDistance) != 0) {
                int spawnHeightDivided = (mobSpawnHeight - (map != null ? (int) map.get("startingHeight") : RpgDifficultyMain.CONFIG.startingHeight))
                        / (map != null ? (int) map.get("heightDistance") : RpgDifficultyMain.CONFIG.heightDistance);
                if ((map != null ? !(boolean) map.get("positiveHeightIncreasion") : !RpgDifficultyMain.CONFIG.positiveHeightIncreasion) && spawnHeightDivided > 0)
                    spawnHeightDivided = 0;
                if ((map != null ? !(boolean) map.get("negativeHeightIncreasion") : !RpgDifficultyMain.CONFIG.negativeHeightIncreasion) && spawnHeightDivided < 0)
                    spawnHeightDivided = 0;
                if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnHeightDivided = 0;
                }
                spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                mobHealthFactor += spawnHeightDivided * (map != null ? (double) map.get("heightFactor") : RpgDifficultyMain.CONFIG.heightFactor);
                mobDamageFactor += spawnHeightDivided * (map != null ? (double) map.get("heightFactor") : RpgDifficultyMain.CONFIG.heightFactor);
                mobProtectionFactor += spawnHeightDivided * (map != null ? (double) map.get("heightFactor") : RpgDifficultyMain.CONFIG.heightFactor);
            }

            // Cutoff
            double maxFactorHealth = map != null ? (double) map.get("maxFactorHealth") : RpgDifficultyMain.CONFIG.maxFactorHealth;
            double maxFactorDamage = map != null ? (double) map.get("maxFactorDamage") : RpgDifficultyMain.CONFIG.maxFactorDamage;
            double maxFactorProtection = map != null ? (double) map.get("maxFactorProtection") : RpgDifficultyMain.CONFIG.maxFactorProtection;
            double maxFactorSpeed = map != null ? (double) map.get("maxFactorSpeed") : RpgDifficultyMain.CONFIG.maxFactorSpeed;

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

            DefaultAttributeContainer mobEntityDefaultAttributes = DefaultAttributeRegistryAccessor.getRegistry().get(mobEntity.getType());

            // Test purpose
            if (RpgDifficultyMain.CONFIG.hudTesting) {
                System.out.println(Registry.ENTITY_TYPE.getId(mobEntity.getType()).toString() + "; HealthFactor: " + mobHealthFactor + "; DamageFactor: " + mobDamageFactor + "; Health: " + mobHealth
                        + ";  Old Health: " + mobEntity.getHealth() + "; Default HP: "
                        + (mobEntityDefaultAttributes != null ? mobEntityDefaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) : "-"));
            }

            // Check if mob already has increased strength
            if (mobEntityDefaultAttributes != null && mobHealth - mobEntityDefaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) * mobHealthFactor < 0.1D) {
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

    // Unused
    public static void changeOnlyHealthAttribute(MobEntity mobEntity, ServerWorld world) {
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(mobEntity.getType().toString().replace("entity.", ""))) {
            double mobHealthFactor = RpgDifficultyMain.CONFIG.startingFactor;

            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(world.getSpawnPos().getX(), world.getSpawnPos().getY(), world.getSpawnPos().getZ()));
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
                int worldTime = (int) world.getTime();
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
            // round factor
            mobHealthFactor = Math.round(mobHealthFactor * 100.0D) / 100.0D;

            double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            mobHealth *= mobHealthFactor;
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
            mobEntity.heal(mobEntity.getMaxHealth());
        }
    }

    public static void changeOnlyDamageAttribute(MobEntity mobEntity, ServerWorld world, Entity entity, boolean changeMobEntityValue) {
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(mobEntity.getType().toString().replace("entity.", ""))) {

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(world.getRegistryKey().getValue().toString()))
                map = DifficultyLoader.dimensionDifficulty.get(world.getRegistryKey().getValue().toString());

            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : world.getSpawnPos().getX();
            int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : world.getSpawnPos().getZ();

            float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(spawnX, mobEntity.getY(), spawnZ));
            int worldTime = (int) world.getTime();

            if ((map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance))
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobDamageFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
            }
            if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200)
                    worldTime = 0;
                else
                    worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
                int timeDivided = worldTime / ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) * 1200);
                if (RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    timeDivided = 0;
                }
                mobDamageFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
            }
            if ((map != null ? (int) map.get("heightDistance") : RpgDifficultyMain.CONFIG.heightDistance) != 0) {
                int spawnHeightDivided = ((int) mobEntity.getY() - (map != null ? (int) map.get("startingHeight") : RpgDifficultyMain.CONFIG.startingHeight))
                        / (map != null ? (int) map.get("heightDistance") : RpgDifficultyMain.CONFIG.heightDistance);
                if ((map != null ? !(boolean) map.get("positiveHeightIncreasion") : !RpgDifficultyMain.CONFIG.positiveHeightIncreasion) && spawnHeightDivided > 0)
                    spawnHeightDivided = 0;
                if ((map != null ? !(boolean) map.get("negativeHeightIncreasion") : !RpgDifficultyMain.CONFIG.negativeHeightIncreasion) && spawnHeightDivided < 0)
                    spawnHeightDivided = 0;
                if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && mobEntity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnHeightDivided = 0;
                }
                spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                mobDamageFactor += spawnHeightDivided * (map != null ? (double) map.get("heightFactor") : RpgDifficultyMain.CONFIG.heightFactor);
            }

            double maxFactor = (map != null ? (double) map.get("maxFactorDamage") : RpgDifficultyMain.CONFIG.maxFactorDamage);
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }

            // round factor
            mobDamageFactor = Math.round(mobDamageFactor * 100.0D) / 100.0D;

            if (entity instanceof PersistentProjectileEntity) {
                PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) entity;
                persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * mobDamageFactor);
            }
            if (changeMobEntityValue) {
                boolean hasAttackDamageAttribute = mobEntity.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                if (hasAttackDamageAttribute) {
                    double mobDamage = mobEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    mobDamage *= mobDamageFactor;
                    mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(mobDamage);
                }
            }
        }
    }

    // Untouched by height factor and dimension check
    public static void changeBossAttributes(MobEntity mobEntity, ServerWorld world) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(world.getRegistryKey().getValue().toString()))
                map = DifficultyLoader.dimensionDifficulty.get(world.getRegistryKey().getValue().toString());

            double mobHealthFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobProtectionFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double dynamicFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;

            int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : world.getSpawnPos().getX();
            int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : world.getSpawnPos().getZ();

            float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(spawnX, mobEntity.getY(), spawnZ));
            int worldTime = (int) world.getTime();

            if ((map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance))
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
            }
            if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200)
                    worldTime = 0;
                else
                    worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
                int timeDivided = worldTime / ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) * 1200);
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
            double maxFactorProtection = (map != null ? (double) map.get("maxFactorProtection") : RpgDifficultyMain.CONFIG.maxFactorProtection);
            double maxFactorDamage = map != null ? (double) map.get("maxFactorDamage") : RpgDifficultyMain.CONFIG.maxFactorDamage;
            if (mobHealthFactor > maxFactor) {
                mobHealthFactor = maxFactor;
            }
            if (mobProtectionFactor > maxFactorProtection) {
                mobProtectionFactor = maxFactorProtection;
            }
            if (mobDamageFactor > maxFactorDamage) {
                mobDamageFactor = maxFactorDamage;
            }

            // round factor
            mobHealthFactor = Math.round(mobHealthFactor * 100.0D) / 100.0D;
            mobProtectionFactor = Math.round(mobProtectionFactor * 100.0D) / 100.0D;
            mobDamageFactor = Math.round(mobDamageFactor * 100.0D) / 100.0D;

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
            mobEntity.heal(mobEntity.getMaxHealth());
            if (hasArmorAttribute) {
                mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(mobProtection);
            }
            if (hasAttackDamageAttribute) {
                mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(mobDamage);
            }
            setMobHealthMultiplier(mobEntity, (float) mobHealthFactor);
        }
    }

    // Untouched by height factor and dimension check
    public static void changeEnderDragonAttribute(MobEntity mobEntity, ServerWorld world) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {
            double mobHealthFactor = RpgDifficultyMain.CONFIG.startingFactor;
            double dynamicFactor = 1.0D;

            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                float worldSpawnDistance = MathHelper.sqrt((float) mobEntity.squaredDistanceTo(world.getSpawnPos().getX(), world.getSpawnPos().getY(), world.getSpawnPos().getZ()));
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
                List<ServerPlayerEntity> list = world.getPlayers(EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(0.0D, 128.0D, 0.0D, 1000.0D)));
                for (int i = 0; i < list.size(); ++i) {
                    dynamicFactor += RpgDifficultyMain.CONFIG.dynamicBossModificator;
                }
            }
            mobHealthFactor *= dynamicFactor;

            double maxFactor = RpgDifficultyMain.CONFIG.bossMaxFactor;
            if (mobHealthFactor > maxFactor) {
                mobHealthFactor = maxFactor;
            }
            // round factor
            mobHealthFactor = Math.round(mobHealthFactor * 100.0D) / 100.0D;

            double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            mobHealth *= mobHealthFactor;
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
            mobEntity.heal(mobEntity.getMaxHealth());

            setMobHealthMultiplier(mobEntity, (float) mobHealthFactor);
        }
    }

    public static double getDamageFactor(Entity entity) {
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(entity.getType().toString().replace("entity.", ""))) {

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(entity.world.getRegistryKey().getValue().toString()))
                map = DifficultyLoader.dimensionDifficulty.get(entity.world.getRegistryKey().getValue().toString());

            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : ((ServerWorld) entity.world).getSpawnPos().getX();
            int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : ((ServerWorld) entity.world).getSpawnPos().getZ();

            float worldSpawnDistance = MathHelper.sqrt((float) entity.squaredDistanceTo(spawnX, entity.getY(), spawnZ));
            int worldTime = (int) entity.world.getTime();

            if ((map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance))
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && entity.world.getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobDamageFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
            }
            if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200)
                    worldTime = 0;
                else
                    worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
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
        } else
            return original;
    }

    public static void dropMoreLoot(MobEntity mobEntity, LootTable lootTable, LootContext.Builder builder) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot) {
            float level = 0f;
            if (RpgDifficultyMain.isNameplateLoaded) {
                level = ((MobEntityAccess) (Object) mobEntity).getMobRpgLevel();
            } else {
                level = getMobHealthMultiplier(mobEntity);
            }
            if (level > 0.01f) {
                float dropChance = level * RpgDifficultyMain.CONFIG.moreLootChance;
                if (dropChance > RpgDifficultyMain.CONFIG.maxLootChance)
                    dropChance = RpgDifficultyMain.CONFIG.maxLootChance;

                if (random.nextFloat() <= dropChance) {
                    List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));
                    for (int i = 0; i < list.size(); i++) {
                        if (random.nextFloat() < RpgDifficultyMain.CONFIG.chanceForEachItem)
                            continue;
                        ItemStack stack = list.get(i);
                        stack.increment((int) (stack.getCount() * dropChance));
                        mobEntity.dropStack(stack);
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
