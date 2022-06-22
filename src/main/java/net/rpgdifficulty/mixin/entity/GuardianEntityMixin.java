package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(GuardianEntity.class)
public abstract class GuardianEntityMixin extends HostileEntity {

    public GuardianEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "damage", constant = @Constant(floatValue = 2.0f), require = 0)
    private float damageMixin(float original) {
        if (this.world instanceof ServerWorld) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }
}
