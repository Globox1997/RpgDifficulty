package net.rpgdifficulty.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(EndCityGenerator.class)
public class EndCityGeneratorMixin {

    @Inject(method = "Lnet/minecraft/structure/EndCityGenerator$Piece;handleMetadata(Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockBox;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ShulkerEntity;setPosition(DDD)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void handleMetadataMixin(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox, CallbackInfo info, ShulkerEntity shulkerEntity) {
        MobStrengthener.changeAttributes(shulkerEntity, world.toServerWorld());
    }
}
