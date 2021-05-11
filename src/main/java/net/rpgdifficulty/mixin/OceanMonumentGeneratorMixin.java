package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(OceanMonumentGenerator.Piece.class)
public class OceanMonumentGeneratorMixin {

    @Inject(method = "Lnet/minecraft/structure/OceanMonumentGenerator$Piece;method_14772(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;III)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ElderGuardianEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onTrackedDataSetMixin(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int i, int j,
            int k, CallbackInfoReturnable<Boolean> info, int l, int m, int n, ElderGuardianEntity elderGuardianEntity) {
        double mobHealthFactor = 1.0F;
        ServerWorld serverWorld = (ServerWorld) elderGuardianEntity.world;
        float worldSpawnDistance = MathHelper.sqrt(elderGuardianEntity.squaredDistanceTo(
                serverWorld.getSpawnPos().getX(), serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
        int worldTime = (int) elderGuardianEntity.world.getTime();

        int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
        mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;

        int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
        mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;

        double maxFactor = RpgDifficultyMain.CONFIG.maxFactorHealth;
        if (mobHealthFactor > maxFactor) {
            mobHealthFactor = maxFactor;
        }
        double mobHealth = elderGuardianEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        mobHealth *= mobHealthFactor;
        elderGuardianEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
        elderGuardianEntity.heal(elderGuardianEntity.getMaxHealth());
    }
}
