package net.rpgdifficulty.mixin.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.api.MobStrengthener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Made by Herobrot
@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal {

    public WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "applyTamingSideEffects", at = @At("TAIL"))
    private void updateAttributesForTamedMixin(CallbackInfo info) {
        if (!this.isTame() || !(this.level() instanceof ServerLevel serverWorld)) {
            return;
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0);
        MobStrengthener.changeAttributes((Wolf) (Object) this, serverWorld, null, false);
        double healthRatio = this.getAttributeBaseValue(Attributes.MAX_HEALTH) / 8.0;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0 * healthRatio);
        this.setHealth(this.getMaxHealth());
    }

}
