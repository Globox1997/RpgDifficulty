package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity {

    public PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onGrowUp", at = @At(value = "HEAD"))
    protected void onGrowUpMixin(CallbackInfo info) {
        if (!world.isClient && getBreedingAge() == 0 && world instanceof ServerWorld
                && Math.abs(DefaultAttributeRegistryAccessor.getRegistry().get(this.getType()).getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
                        - ((MobEntity) (Object) this).getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)) <= 0.0001D) {
            MobStrengthener.changeAttributes((MobEntity) (Object) this, (ServerWorld) world);
        }
    }

    @Shadow
    public int getBreedingAge() {
        return 0;
    }
}
