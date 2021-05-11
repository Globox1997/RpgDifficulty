package net.rpgdifficulty.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(StriderEntity.class)
public class StriderEntityMixin {

    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
            @Nullable EntityData entityData, @Nullable CompoundTag entityTag, CallbackInfoReturnable<EntityData> info,
            MobEntity mobEntity) {
        double mobHealthFactor = 1.0F;
        ServerWorld serverWorld = (ServerWorld) world.toServerWorld();
        float worldSpawnDistance = MathHelper.sqrt(mobEntity.squaredDistanceTo(serverWorld.getSpawnPos().getX(),
                serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ()));
        int worldTime = (int) world.toServerWorld().getTime();

        int spawnDistanceDivided = (int) worldSpawnDistance / RpgDifficultyMain.CONFIG.increasingDistance;
        mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.distanceFactor;

        int timeDivided = worldTime / (RpgDifficultyMain.CONFIG.increasingTime * 1200);
        mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.timeFactor;

        double maxFactor = RpgDifficultyMain.CONFIG.maxFactorHealth;
        if (mobHealthFactor > maxFactor) {
            mobHealthFactor = maxFactor;
        }
        double mobHealth = mobEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        mobHealth *= mobHealthFactor;
        mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mobHealth);
        mobEntity.heal(mobEntity.getMaxHealth());
    }

}
