package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(EvokerFangsEntity.class)
public abstract class EvokerFangsEntityMixin extends Entity {

    public EvokerFangsEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyConstant(method = "damage", constant = @Constant(floatValue = 6.0f), require = 0)
    private float damageMixin(float original) {
        if (this.world instanceof ServerWorld) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }
}
