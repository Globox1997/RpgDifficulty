package net.rpgdifficulty.mixin;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(WitherSkullBlock.class)
public class WitherSkullBlockMixin {

    @Inject(method = "Lnet/minecraft/block/WitherSkullBlock;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SkullBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/WitherEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onPlacedMixin(World world, BlockPos pos, SkullBlockEntity blockEntity, CallbackInfo info,
            BlockPattern blockPattern, BlockPattern.Result result, WitherEntity witherEntity) {
        if (RpgDifficultyMain.CONFIG.affectBosses) {
            double mobHealthFactor = 1.0F;
            double mobProtectionFactor = 1.0F;
            double dynamicFactor = 1.0D;

            ServerWorld serverWorld = (ServerWorld) world;
            float worldSpawnDistance = MathHelper.sqrt(witherEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                    serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
            int worldTime = (int) world.getTime();

            int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
            mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
            mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;

            int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
            mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
            mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;

            if (RpgDifficultyMain.CONFIG.dynamicBossModification) {
                List<PlayerEntity> list = Lists.newArrayList();
                Iterator<? extends PlayerEntity> iterator = world.getPlayers().iterator();

                while (iterator.hasNext()) {
                    PlayerEntity playerEntity = (PlayerEntity) iterator.next();
                    if (new Box(blockEntity.getPos()).expand(128D).contains(playerEntity.getX(), playerEntity.getY(),
                            playerEntity.getZ())) {
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
            double maxFactorProtection = RpgDifficultyMain.CONFIG.maxFactorProtection;
            if (mobHealthFactor > maxFactor) {
                mobHealthFactor = maxFactor;
            }
            if (mobProtectionFactor > maxFactorProtection) {
                mobProtectionFactor = maxFactorProtection;
            }
            double mobHealth = witherEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            double mobProtection = witherEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            mobHealth *= mobHealthFactor;
            mobProtection *= mobProtectionFactor;
            witherEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
            witherEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(mobProtection);
        }
    }
}
