package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"))
    private void spawnEntityMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof MobEntity && Math.abs(DefaultAttributeRegistryAccessor.getRegistry().get(entity.getType()).getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
                - ((MobEntity) entity).getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)) <= 0.0001D) {
            MobStrengthener.changeAttributes((MobEntity) entity, (ServerWorld) (Object) this);
        }

    }
}
