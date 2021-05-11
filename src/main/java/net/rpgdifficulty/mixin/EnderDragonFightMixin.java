package net.rpgdifficulty.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {

    @Inject(method = "createDragon", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createDragonMixin(CallbackInfoReturnable<EnderDragonEntity> info,
            EnderDragonEntity enderDragonEntity) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {
            double mobHealthFactor = 1.0F;
            double dynamicFactor = 1.0D;

            ServerWorld serverWorld = (ServerWorld) enderDragonEntity.getEntityWorld();
            if (!RpgDifficultyMain.CONFIG.disableBossTimeDistance) {
                float worldSpawnDistance = MathHelper
                        .sqrt(enderDragonEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                                serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
                int worldTime = (int) enderDragonEntity.getEntityWorld().getTime();

                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;

                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
            }

            if (RpgDifficultyMain.CONFIG.dynamicBossModification) {
                List<ServerPlayerEntity> list = serverWorld.getPlayers(
                        EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(0.0D, 128.0D, 0.0D, 1000.0D)));
                for (int i = 0; i < list.size(); ++i) {
                    dynamicFactor += RpgDifficultyMain.CONFIG.bossModificator;
                }
            }
            mobHealthFactor *= dynamicFactor;

            double maxFactor = RpgDifficultyMain.CONFIG.bossMaxFactor;
            if (mobHealthFactor > maxFactor) {
                mobHealthFactor = maxFactor;
            }
            double mobHealth = enderDragonEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            mobHealth *= mobHealthFactor;
            enderDragonEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
            enderDragonEntity.heal(enderDragonEntity.getMaxHealth());
        }
    }
}
