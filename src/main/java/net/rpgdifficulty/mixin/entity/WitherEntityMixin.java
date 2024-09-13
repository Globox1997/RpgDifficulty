package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.mixin.access.DefaultAttributeRegistryAccess;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends HostileEntity {

    public WitherEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "mobTick", constant = @Constant(floatValue = 10f))
    private float mobTickMixin(float original) {
        if (this.getWorld() instanceof ServerWorld) {
            float oldMaxHealth = (float) DefaultAttributeRegistryAccess.getRegistry().get(this.getType()).getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH);
            if (this.getMaxHealth() - oldMaxHealth > 0.01D) {
                return (this.getMaxHealth() + (this.getMaxHealth() / 3 - oldMaxHealth / 3)) / 30f;
            }
        }
        return original;
    }
}
