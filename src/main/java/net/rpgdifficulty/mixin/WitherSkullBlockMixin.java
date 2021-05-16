package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(WitherSkullBlock.class)
public class WitherSkullBlockMixin {

    @Inject(method = "Lnet/minecraft/block/WitherSkullBlock;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SkullBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/WitherEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onPlacedMixin(World world, BlockPos pos, SkullBlockEntity blockEntity, CallbackInfo info,
            BlockPattern blockPattern, BlockPattern.Result result, WitherEntity witherEntity) {
        if (world instanceof ServerWorld) {
            MobStrengthener.changeBossAttributes(witherEntity, (ServerWorld) world);
        }
    }
}
