package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @Inject(method = "trySpawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SnowGolemEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void trySpawnEntityMixinOne(World world, BlockPos pos, CallbackInfo info, BlockPattern.Result result, SnowGolemEntity snowGolemEntity, BlockPos blockPos) {
        if (world instanceof ServerWorld) {
            MobStrengthener.changeAttributes(snowGolemEntity, (ServerWorld) world);
        }
    }

    @Inject(method = "trySpawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/IronGolemEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void trySpawnEntityMixinTwo(World world, BlockPos pos, CallbackInfo info, BlockPattern.Result result, BlockPos blockPos2, IronGolemEntity ironGolemEntity) {
        if (world instanceof ServerWorld) {
            MobStrengthener.changeAttributes(ironGolemEntity, (ServerWorld) world);
        }
    }
}
