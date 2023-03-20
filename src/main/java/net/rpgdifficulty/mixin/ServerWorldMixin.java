package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"))
    private void spawnEntityMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof MobEntity) {
            if (entity instanceof EnderDragonEntity) {
                MobStrengthener.changeEnderDragonAttribute((MobEntity) entity, (ServerWorld) (Object) this);
            } else if (entity.getType().isIn(RpgDifficultyMain.BOSS_ENTITY_TYPES)) {
                MobStrengthener.changeBossAttributes((MobEntity) entity, (ServerWorld) (Object) this);
            } else {
                MobStrengthener.changeAttributes((MobEntity) entity, (ServerWorld) (Object) this);
            }
        }
        if (entity instanceof PersistentProjectileEntity) {
            if (((PersistentProjectileEntity) entity).getOwner() != null && ((PersistentProjectileEntity) entity).getOwner() instanceof MobEntity)
                MobStrengthener.changeOnlyDamageAttribute((MobEntity) ((PersistentProjectileEntity) entity).getOwner(), (ServerWorld) (Object) this, entity, false);
        }
    }
}
