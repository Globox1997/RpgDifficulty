package net.rpgdifficulty.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {

    @Shadow
    @Mutable
    @Final
    private MinecraftServer server;

    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void spawnEntityMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof Mob mobEntity) {
            if (server.isSameThread()) {
                MobStrengthener.changeAttributes(mobEntity, (ServerLevel) (Object) this, null, entity.getType().is(RpgDifficultyMain.BOSS_ENTITY_TYPES));
            } else {
                server.execute(() -> MobStrengthener.changeAttributes(mobEntity, (ServerLevel) (Object) this, null, entity.getType().is(RpgDifficultyMain.BOSS_ENTITY_TYPES)));
            }
        } else if (entity instanceof AbstractArrow persistentProjectileEntity) {
            if (persistentProjectileEntity.getOwner() instanceof Mob mobEntity) {
                if (server.isSameThread()) {
                    MobStrengthener.changeAttributes(mobEntity, (ServerLevel) (Object) this, persistentProjectileEntity, false);
                } else {
                    server.execute(() -> MobStrengthener.changeAttributes(mobEntity, (ServerLevel) (Object) this, persistentProjectileEntity, false));
                }
            }
        }
    }
}
