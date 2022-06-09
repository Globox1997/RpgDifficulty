package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "dropLoot", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, Identifier identifier, LootTable lootTable, LootContext.Builder builder) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot)
            MobStrengthener.dropMoreLoot((LivingEntity) (Object) this, lootTable, builder);
    }
}
