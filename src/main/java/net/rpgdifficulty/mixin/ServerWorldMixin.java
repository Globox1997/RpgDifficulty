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
        if (entity instanceof MobEntity mobEntity) {
            if (entity instanceof EnderDragonEntity) {
                MobStrengthener.changeEnderDragonAttribute(mobEntity, (ServerWorld) (Object) this);
            } else if (entity.getType().isIn(RpgDifficultyMain.BOSS_ENTITY_TYPES)) {
                MobStrengthener.changeBossAttributes(mobEntity, (ServerWorld) (Object) this);
            } else {
                MobStrengthener.changeAttributes(mobEntity, (ServerWorld) (Object) this);
            }
        }
        if (entity instanceof PersistentProjectileEntity persistentProjectileEntity) {
            if (persistentProjectileEntity.getOwner() instanceof MobEntity mobEntity)
                MobStrengthener.changeOnlyDamageAttribute(mobEntity, (ServerWorld) (Object) this, persistentProjectileEntity, false);
        }
    }
}
