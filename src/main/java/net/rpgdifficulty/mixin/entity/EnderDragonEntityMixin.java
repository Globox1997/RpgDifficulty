package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity {

    public EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "launchLivingEntities", constant = @Constant(floatValue = 5.0f), require = 0)
    private float launchLivingEntitiesMixin(float original) {
        if (this.getWorld() instanceof ServerWorld) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }

    @ModifyConstant(method = "damageLivingEntities", constant = @Constant(floatValue = 10.0f), require = 0)
    private float damageLivingEntitiesMixin(float original) {
        if (this.getWorld() instanceof ServerWorld) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }

}
