package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(EnderDragon.class)
public abstract class EnderDragonEntityMixin extends Mob {

    public EnderDragonEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "knockBack", constant = @Constant(floatValue = 5.0f), require = 0)
    private float launchLivingEntitiesMixin(float original) {
        if (this.level() instanceof ServerLevel) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }

    @ModifyConstant(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Ljava/util/List;)V", constant = @Constant(floatValue = 10.0f), require = 0)
    private float damageLivingEntitiesMixin(float original) {
        if (this.level() instanceof ServerLevel) {
            return original * (float) MobStrengthener.getDamageFactor(this);
        }
        return original;
    }

}
