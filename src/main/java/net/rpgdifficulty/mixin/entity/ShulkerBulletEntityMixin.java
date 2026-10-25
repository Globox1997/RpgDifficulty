package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(ShulkerBullet.class)
public abstract class ShulkerBulletEntityMixin {

    @ModifyConstant(method = "onHitEntity", constant = @Constant(floatValue = 4.0f), require = 0)
    private float onEntityHitMixin(float original) {
        if (((Projectile) (Object) this).level() instanceof ServerLevel) {
            return original * (float) MobStrengthener.getDamageFactor((Projectile) (Object) this);
        }
        return original;
    }
}
