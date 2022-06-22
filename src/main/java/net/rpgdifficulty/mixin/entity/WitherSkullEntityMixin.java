package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(WitherSkullEntity.class)
public abstract class WitherSkullEntityMixin extends ExplosiveProjectileEntity {

    public WitherSkullEntityMixin(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "onEntityHit", constant = @Constant(floatValue = 8.0f), require = 0)
    private float onEntityHitMixin(float original) {
        if (this.world instanceof ServerWorld) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }
}
