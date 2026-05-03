package net.rpgdifficulty.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Made by Herobrot
@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity {

    public WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updateAttributesForTamed", at = @At("TAIL"))
    private void updateAttributesForTamedMixin(CallbackInfo info) {
        if (!this.isTamed() || !(this.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
        MobStrengthener.changeAttributes((WolfEntity) (Object) this, serverWorld, null, false);
    }

}
