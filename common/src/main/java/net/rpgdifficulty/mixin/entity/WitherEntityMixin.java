package net.rpgdifficulty.mixin.entity;

import net.rpgdifficulty.api.MobStrengthener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

@Mixin(WitherBoss.class)
public abstract class WitherEntityMixin extends Monster {

    public WitherEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "customServerAiStep", constant = @Constant(floatValue = 10f))
    private float mobTickMixin(float original) {
        if (this.level() instanceof ServerLevel) {
            float oldMaxHealth = (float) MobStrengthener.getDefaultAttributes(this.getType()).getBaseValue(Attributes.MAX_HEALTH);
            if (this.getMaxHealth() - oldMaxHealth > 0.01D) {
                return (this.getMaxHealth() + (this.getMaxHealth() / 3 - oldMaxHealth / 3)) / 30f;
            }
        }
        return original;
    }
}
