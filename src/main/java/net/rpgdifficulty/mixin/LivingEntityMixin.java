package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // Could check for damagesource and increase the damage amount
    // Warden sonic boom is in lambda at sonic boom task class
    // @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    // private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
    // System.out.println(source + " : " + source.getSource());
    // }

    @Inject(method = "dropLoot", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, Identifier identifier, LootTable lootTable, LootContext.Builder builder) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot)
            MobStrengthener.dropMoreLoot((LivingEntity) (Object) this, lootTable, builder);
    }
}
