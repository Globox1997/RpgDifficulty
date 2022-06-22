package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.server.world.ServerWorld;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(ShulkerBulletEntity.class)
public abstract class ShulkerBulletEntityMixin {

    @ModifyConstant(method = "onEntityHit", constant = @Constant(floatValue = 4.0f), require = 0)
    private float onEntityHitMixin(float original) {
        if (((ProjectileEntity) (Object) this).world instanceof ServerWorld) {
            return original * (float) MobStrengthener.getDamageFactor((ProjectileEntity) (Object) this);
        }
        return original;
    }
}
