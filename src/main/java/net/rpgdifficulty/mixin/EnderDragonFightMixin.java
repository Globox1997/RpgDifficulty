package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {

    @Inject(method = "createDragon", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createDragonMixin(CallbackInfoReturnable<EnderDragonEntity> info,
            EnderDragonEntity enderDragonEntity) {
        MobStrengthener.changeEnderDragonAttribute(enderDragonEntity, (ServerWorld) enderDragonEntity.getEntityWorld());
    }
}
