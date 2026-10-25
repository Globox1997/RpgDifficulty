package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(SmallFireball.class)
public abstract class SmallFireballEntityMixin extends Fireball {

    public SmallFireballEntityMixin(EntityType<? extends Fireball> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "onHitEntity", constant = @Constant(floatValue = 5.0f), require = 0)
    private float onEntityHitMixin(float original) {
        if (this.level() instanceof ServerLevel) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }
}
