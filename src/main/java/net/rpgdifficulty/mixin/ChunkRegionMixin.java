package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(WorldGenRegion.class)
public class ChunkRegionMixin {

    @Shadow
    @Final
    @Mutable
    private ServerLevel level;

    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void spawnEntityMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof Mob mobEntity) {
            MobStrengthener.changeAttributes(mobEntity, level, null, entity.getType().is(RpgDifficultyMain.BOSS_ENTITY_TYPES));
        } else if (entity instanceof AbstractArrow persistentProjectileEntity) {
            if (persistentProjectileEntity.getOwner() instanceof Mob mobEntity) {
                MobStrengthener.changeAttributes(mobEntity, level, persistentProjectileEntity, false);
            }
        }
    }
}
