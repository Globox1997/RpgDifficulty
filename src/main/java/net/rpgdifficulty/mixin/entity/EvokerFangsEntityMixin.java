package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(EvokerFangs.class)
public abstract class EvokerFangsEntityMixin extends Entity {

    public EvokerFangsEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyConstant(method = "dealDamageTo", constant = @Constant(floatValue = 6.0f), require = 0)
    private float damageMixin(float original) {
        if (this.level() instanceof ServerLevel) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }
}
