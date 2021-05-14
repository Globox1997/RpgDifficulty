package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void attackMixin(LivingEntity target, float pullProgress, CallbackInfo info, ItemStack itemStack,
            PersistentProjectileEntity persistentProjectileEntity, double d, double e, double f, double g) {
        if (!RpgDifficultyMain.CONFIG.excluded_entity
                .contains(((AbstractSkeletonEntity) (Object) this).getType().toString().replace("entity.", ""))) {
            double mobDamageFactor = 1.0F;
            ServerWorld serverWorld = (ServerWorld) target.world;
            float worldSpawnDistance = MathHelper.sqrt(target.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                    serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
            int worldTime = (int) target.world.getTime();

            int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
            mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;

            int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
            mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;

            double maxFactor = RpgDifficultyMain.CONFIG.maxFactorDamage;
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }

            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * mobDamageFactor);
        }
    }

}
