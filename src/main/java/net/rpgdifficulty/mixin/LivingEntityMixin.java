package net.rpgdifficulty.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    @Nullable
    protected PlayerEntity attackingPlayer;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "dropXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"))
    private void dropXpRedirect(ServerWorld serverWorld, Vec3d vec3d, int i) {
        double xpFactor = 1.0F;
        if (RpgDifficultyMain.CONFIG.extraXp) {
            float worldSpawnDistance = MathHelper.sqrt((float) this.squaredDistanceTo(serverWorld.getSpawnPos().getX(), serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
            int worldTime = (int) world.getTime();
            if (RpgDifficultyMain.CONFIG.increasingDistance != 0) {
                if ((int) worldSpawnDistance <= RpgDifficultyMain.CONFIG.startingDistance)
                    worldSpawnDistance = 0;
                else
                    worldSpawnDistance -= RpgDifficultyMain.CONFIG.startingDistance;
                int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && world.getRegistryKey() != World.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                xpFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;
            }
            if (RpgDifficultyMain.CONFIG.increasingTime != 0) {
                if (worldTime <= RpgDifficultyMain.CONFIG.startingTime * 1200)
                    worldTime = 0;
                else
                    worldTime -= RpgDifficultyMain.CONFIG.startingTime * 1200;
                int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
                xpFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;
            }
            double maxXPFactor = RpgDifficultyMain.CONFIG.maxXPFactor;
            if (xpFactor > maxXPFactor) {
                xpFactor = maxXPFactor;
            }
        }
        ExperienceOrbEntity.spawn((ServerWorld) this.world, this.getPos(), (int) xpFactor * this.getXpToDrop(this.attackingPlayer));
    }

    @Shadow
    protected int getXpToDrop(PlayerEntity player) {
        return 0;
    }

}
