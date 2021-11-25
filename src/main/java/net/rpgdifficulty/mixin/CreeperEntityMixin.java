package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {

    public CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"), ordinal = 0)
    private float explodeMixin(float original) {
        if (this.world instanceof ServerWorld) {
            if (!RpgDifficultyMain.CONFIG.excluded_entity.contains(this.getType().toString().replace("entity.", ""))) {
                double mobDamageFactor = RpgDifficultyMain.CONFIG.startingFactor;
                float worldSpawnDistance = MathHelper.sqrt((float) this.squaredDistanceTo(((ServerWorld) this.world).getSpawnPos().getX(), ((ServerWorld) this.world).getSpawnPos().getY(),
                        ((ServerWorld) this.world).getSpawnPos().getZ()));
                int worldTime = (int) world.getTime();

                if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                    if ((int) worldSpawnDistance <= RpgDifficultyMain.CONFIG.startingDistance)
                        worldSpawnDistance = 0;
                    else
                        worldSpawnDistance -= RpgDifficultyMain.CONFIG.startingDistance;
                    int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                    if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && this.world.getRegistryKey() != World.OVERWORLD) {
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
                    mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
                }

                double maxFactor = RpgDifficultyMain.CONFIG.maxFactorDamage;
                if (mobDamageFactor > maxFactor) {
                    mobDamageFactor = maxFactor;
                }
                mobDamageFactor *= RpgDifficultyMain.CONFIG.creeperExplosionFactor;
                if (mobDamageFactor < 1.0F)
                    mobDamageFactor = 1.0F;
                return original * (float) mobDamageFactor;

            }
        }
        return original;
    }
}
