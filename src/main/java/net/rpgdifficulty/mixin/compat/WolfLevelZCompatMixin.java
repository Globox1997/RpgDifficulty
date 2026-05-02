package net.rpgdifficulty.mixin.compat;

import net.levelz.access.LevelManagerAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(WolfEntity.class)
public abstract class WolfLevelZCompatMixin extends TameableEntity {

    protected WolfLevelZCompatMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updateAttributesForTamed", at = @At("TAIL"))
    private void updateAttributesForTamedLevelZMixin(CallbackInfo info) {
        if (!this.isTamed() || !(this.getWorld() instanceof ServerWorld serverWorld)) return;
        if (RpgDifficultyMain.CONFIG.levelFactor <= 0.001D) return;
        if (RpgDifficultyMain.CONFIG.excludedEntity.contains(
                this.getType().toString().replace("entity.", "").replace(".", ":"))) return;

        // Calcular levelBonus
        double x = this.getX(), y = this.getY(), z = this.getZ();
        int playerCount = 0, totalPlayerLevel = 0;
        for (PlayerEntity playerEntity : serverWorld.getPlayers()) {
            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(playerEntity)) continue;
            if (playerEntity.getWorld().getDimension().equals(this.getWorld().getDimension())
                    && Math.sqrt(playerEntity.squaredDistanceTo(x, y, z)) <= RpgDifficultyMain.CONFIG.playerRadius) {
                playerCount++;
                totalPlayerLevel += ((LevelManagerAccess) playerEntity).getLevelManager().getOverallLevel();
            }
        }
        if (playerCount == 0) {
            PlayerEntity closest = serverWorld.getClosestPlayer(x, y, z, -1.0, false);
            if (closest != null) {
                playerCount++;
                totalPlayerLevel += ((LevelManagerAccess) closest).getLevelManager().getOverallLevel();
            }
        }
        if (playerCount == 0) return;

        double levelBonus = ((double) totalPlayerLevel / playerCount) * RpgDifficultyMain.CONFIG.levelFactor;


        WolfEntity wolf = (WolfEntity)(Object) this;
        Objects.requireNonNull(wolf.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(40.0);
        wolf.setHealth(40.0f);
        if (wolf.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE))
            Objects.requireNonNull(wolf.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(4.0);
        if (wolf.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR))
            Objects.requireNonNull(wolf.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(0.0);

        MobStrengthener.applyTamedWolfAttributes(wolf, serverWorld, levelBonus);
    }
}