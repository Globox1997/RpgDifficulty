package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(MooshroomEntity.class)
public abstract class MooshroomEntityMixin extends CowEntity {

    public MooshroomEntityMixin(EntityType<? extends CowEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "sheared", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/CowEntity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void shearedMixin(SoundCategory shearedSoundCategory, CallbackInfo info, CowEntity cowEntity) {
        if (world instanceof ServerWorld) {
            MobStrengthener.changeAttributes(cowEntity, (ServerWorld) world);
        }
    }
}
