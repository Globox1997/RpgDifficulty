package net.rpgdifficulty.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

    }

    @Nullable
    @Inject(method = "initialize", at = @At("HEAD"))
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt,
            CallbackInfoReturnable<EntityData> info) {
        if (DefaultAttributeRegistryAccessor.getRegistry().get(((MobEntity) (Object) this).getType()) != null
                && Math.abs(DefaultAttributeRegistryAccessor.getRegistry().get(((MobEntity) (Object) this).getType()).getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
                        - ((MobEntity) (Object) this).getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)) <= 0.0001D)
            MobStrengthener.changeAttributes((MobEntity) (Object) this, world.toServerWorld());
    }

    @ModifyVariable(method = "getXpToDrop", at = @At(value = "RETURN", ordinal = 0))
    private int getXpToDropMixin(int original) {
        return MobStrengthener.getXpToDropAddition((MobEntity) (Object) this, (ServerWorld) world, original);
    }

}
