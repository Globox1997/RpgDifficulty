package net.rpgdifficulty.mixin.compat;

import net.levelz.access.LevelManagerAccess;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;
import net.rpgdifficulty.api.MobStrengthener;
import net.rpgdifficulty.mixin.access.DefaultAttributeRegistryAccess;
import net.rpgdifficulty.util.WorldPlayerTracker;
import net.rpgdifficulty.zone.DifficultyZonePersistentState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobStrengthener.class)
public class LevelZCompatMixin {

    @Unique
    private static final Random random = Random.createThreadSafe();

    @Inject(method = "changeAttributes", at = @At(value = "HEAD"), cancellable = true)
    private static void changeAttributesMixin(MobEntity mobEntity, ServerWorld world, @Nullable PersistentProjectileEntity persistentProjectileEntity, boolean isBossMob, CallbackInfo info) {
        if (RpgDifficultyMain.CONFIG.levelFactor > 0.001D && !MobStrengthener.isExcluded(mobEntity.getType())) {

            if (mobEntity.isBaby() && mobEntity instanceof PassiveEntity && !RpgDifficultyMain.CONFIG.affectAnimalBabies) {
                return;
            }
            if (isBossMob && !RpgDifficultyMain.CONFIG.affectBosses) {
                return;
            }
            if (DifficultyZonePersistentState.findCachedZone(world.getRegistryKey().getValue().toString(), mobEntity.getX(), mobEntity.getY(), mobEntity.getZ()).isPresent()) {
                return;
            }

            double x = mobEntity.getX();
            double y = mobEntity.getY();
            double z = mobEntity.getZ();

            int playerCount = 0;
            int totalPlayerLevel = 0;
            for (PlayerEntity playerEntity : WorldPlayerTracker.getPlayers(world)) {
                if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(playerEntity)) {
                    continue;
                }
                if (playerEntity.getWorld().getDimension().equals(mobEntity.getWorld().getDimension())
                        && Math.sqrt(playerEntity.squaredDistanceTo(x, y, z)) <= RpgDifficultyMain.CONFIG.playerRadius) {
                    playerCount++;
                    totalPlayerLevel += ((LevelManagerAccess) playerEntity).getLevelManager().getOverallLevel();
                }
            }
            if (playerCount == 0) {
                PlayerEntity playerEntity = WorldPlayerTracker.getClosestPlayer(world, x, y, z);
                if (playerEntity != null) {
                    playerCount++;
                    totalPlayerLevel += ((LevelManagerAccess) playerEntity).getLevelManager().getOverallLevel();
                }
            }
            if (playerCount > 0) {
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

                // Factor
                double mobHealthFactor = RpgDifficultyMain.CONFIG.startingFactor;
                double mobDamageFactor = RpgDifficultyMain.CONFIG.startingFactor;
                double mobProtectionFactor = RpgDifficultyMain.CONFIG.startingFactor;
                // Cutoff
                double maxFactorHealth = RpgDifficultyMain.CONFIG.maxFactorHealth;
                double maxFactorDamage = RpgDifficultyMain.CONFIG.maxFactorDamage;
                double maxFactorProtection = RpgDifficultyMain.CONFIG.maxFactorProtection;

                // Calculate
                mobHealthFactor += (double) totalPlayerLevel / (double) playerCount * RpgDifficultyMain.CONFIG.levelFactor;
                mobDamageFactor += (double) totalPlayerLevel / (double) playerCount * RpgDifficultyMain.CONFIG.levelFactor;
                mobProtectionFactor += (double) totalPlayerLevel / (double) playerCount * RpgDifficultyMain.CONFIG.levelFactor;

                if (mobHealthFactor > maxFactorHealth) {
                    mobHealthFactor = maxFactorHealth;
                }
                if (mobDamageFactor > maxFactorDamage) {
                    mobDamageFactor = maxFactorDamage;
                }
                if (mobProtectionFactor > maxFactorProtection) {
                    mobProtectionFactor = maxFactorProtection;
                }

                // round factor
                mobHealthFactor = Math.round(mobHealthFactor * 100.0D) / 100.0D;
                mobProtectionFactor = Math.round(mobProtectionFactor * 100.0D) / 100.0D;
                mobDamageFactor = Math.round(mobDamageFactor * 100.0D) / 100.0D;

                // Setter
                mobHealth *= mobHealthFactor;
                mobDamage *= mobDamageFactor;
                mobProtection *= mobProtectionFactor;

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

                if (persistentProjectileEntity != null) {
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * mobDamageFactor);
                    info.cancel();
                } else {
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
                        MobStrengthener.setMobHealthMultiplier(mobEntity, (float) mobHealthFactor);
                    }
                    info.cancel();
                }
            }
        }
    }

}
