package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(Guardian.class)
public abstract class GuardianEntityMixin extends Monster {

    public GuardianEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "hurt", constant = @Constant(floatValue = 2.0f), require = 0)
    private float damageMixin(float original) {
        if (this.level() instanceof ServerLevel) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }
}
