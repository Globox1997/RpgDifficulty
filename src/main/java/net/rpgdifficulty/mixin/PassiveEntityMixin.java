package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;
import net.rpgdifficulty.mixin.access.DefaultAttributeRegistryAccess;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity {

    public PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onGrowUp", at = @At(value = "HEAD"))
    protected void onGrowUpMixin(CallbackInfo info) {
        if (!this.getWorld().isClient() && getBreedingAge() == 0
                && DefaultAttributeRegistryAccess.getRegistry().get(this.getType()) != null
                && Math.abs(DefaultAttributeRegistryAccess.getRegistry().get(this.getType()).getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
                - this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)) <= 0.0001D) {
            MobStrengthener.changeAttributes(this, (ServerWorld) this.getWorld(), null, false);
        }
    }

    @Shadow
    public int getBreedingAge() {
        return 0;
    }
}
