package net.rpgdifficulty.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

    }

    @Nullable
    @Inject(method = "initialize", at = @At("HEAD"))
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt,
            CallbackInfoReturnable<EntityData> info) {
        MobStrengthener.changeAttributes((MobEntity) (Object) this, world.toServerWorld());
    }

    @ModifyVariable(method = "getXpToDrop", at = @At(value = "RETURN", ordinal = 0), print = true)
    private int getXpToDropMixin(int original) {
        if (RpgDifficultyMain.CONFIG.extraXp) {
            double xpFactor = 1.0F;
            float worldSpawnDistance = MathHelper.sqrt(
                    (float) this.squaredDistanceTo(((ServerWorld) this.world).getSpawnPos().getX(), ((ServerWorld) this.world).getSpawnPos().getY(), ((ServerWorld) this.world).getSpawnPos().getZ()));
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

            if (RpgDifficultyMain.CONFIG.heightDistance != 0) {
                int spawnHeightDivided = ((int) this.getY() - RpgDifficultyMain.CONFIG.startingHeight) / RpgDifficultyMain.CONFIG.heightDistance;
                if (!RpgDifficultyMain.CONFIG.positiveHeightIncreasion && spawnHeightDivided > 0)
                    spawnHeightDivided = 0;
                if (!RpgDifficultyMain.CONFIG.negativeHeightIncreasion && spawnHeightDivided < 0)
                    spawnHeightDivided = 0;
                if (RpgDifficultyMain.CONFIG.excludeHeightInOtherDimension && this.world.getRegistryKey() != World.OVERWORLD) {
                    spawnHeightDivided = 0;
                }
                spawnHeightDivided = MathHelper.abs(spawnHeightDivided);
                xpFactor += spawnHeightDivided * RpgDifficultyMain.CONFIG.heightFactor;
            }

            double maxXPFactor = RpgDifficultyMain.CONFIG.maxXPFactor;
            if (xpFactor > maxXPFactor) {
                xpFactor = maxXPFactor;
            }
            return (int) (original * xpFactor);
        } else
            return original;
    }

}
