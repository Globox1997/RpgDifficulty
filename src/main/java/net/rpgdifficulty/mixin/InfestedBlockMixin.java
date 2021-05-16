package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(InfestedBlock.class)
public class InfestedBlockMixin {
    @Inject(method = "spawnSilverfish", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SilverfishEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void spawnSilverfishMixin(ServerWorld serverWorld, BlockPos pos, CallbackInfo info,
            SilverfishEntity silverfishEntity) {
        MobStrengthener.changeAttributes(silverfishEntity, serverWorld);
    }
}
