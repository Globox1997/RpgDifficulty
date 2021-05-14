package net.rpgdifficulty.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(StriderEntity.class)
public class StriderEntityMixin {

    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
            @Nullable EntityData entityData, @Nullable CompoundTag entityTag, CallbackInfoReturnable<EntityData> info,
            MobEntity mobEntity) {
        MobStrengthener.changeOnlyHealthAttribute(mobEntity, world.toServerWorld());
    }

}
