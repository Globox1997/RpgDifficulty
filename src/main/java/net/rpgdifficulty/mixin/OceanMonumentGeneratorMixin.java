package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.StructureWorldAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(OceanMonumentGenerator.Piece.class)
public class OceanMonumentGeneratorMixin {

    @Inject(method = "Lnet/minecraft/structure/OceanMonumentGenerator$Piece;method_14772(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;III)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ElderGuardianEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onTrackedDataSetMixin(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int i, int j,
            int k, CallbackInfoReturnable<Boolean> info, int l, int m, int n, ElderGuardianEntity elderGuardianEntity) {
        MobStrengthener.changeOnlyHealthAttribute(elderGuardianEntity, (ServerWorld) elderGuardianEntity.world);
    }
}
