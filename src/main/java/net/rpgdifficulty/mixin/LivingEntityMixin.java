package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    // Could check for damagesource and increase the damage amount
    // Warden sonic boom is in lambda at sonic boom task class
    // @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    // private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
    // System.out.println(source + " : " + source.getSource());
    // }

    @Inject(method = "dropFromLootTable", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, ResourceKey<LootTable> registryKey, LootTable lootTable, LootParams.Builder builder,
            LootParams lootContextParameterSet) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot && (Object) this instanceof Mob mobEntity) {
            MobStrengthener.dropMoreLoot(mobEntity, lootTable, lootContextParameterSet);
        }
    }

}
