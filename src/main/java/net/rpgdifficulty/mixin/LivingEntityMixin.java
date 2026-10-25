package net.rpgdifficulty.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import java.util.function.Consumer;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    protected void dropLootMixin(ServerLevel level, DamageSource source, boolean causedByPlayer, ResourceKey<LootTable> registryKey, Consumer<ItemStack> itemStackConsumer, CallbackInfo info,
            @Local LootTable lootTable, @Local LootParams lootContextParameterSet) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot && (Object) this instanceof Mob mobEntity) {
            MobStrengthener.dropMoreLoot(mobEntity, lootTable, lootContextParameterSet);
        }
    }

}
