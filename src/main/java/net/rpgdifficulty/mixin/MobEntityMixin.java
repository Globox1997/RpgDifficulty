package net.rpgdifficulty.mixin;

import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "getXpToDrop", at = @At(value = "RETURN", ordinal = 0))
    private int getXpToDropMixin(int original) {
        return MobStrengthener.getXpToDropAddition((MobEntity) (Object) this, (ServerWorld) world, original);
    }

}
