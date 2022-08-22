package net.rpgdifficulty.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(MobStrengthener.class)
public class MoreLootDropMixin {

    // public static void dropMoreLoot(LivingEntity livingEntity, LootTable lootTable, LootContext.Builder builder) {

    // }

    @Redirect(method = "dropMoreLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
    private static ItemEntity dropMoreLootMixin(LivingEntity livingEntity, ItemStack itemStack) {

        System.out.println(itemStack);
        return null;
        // (Lnet/minecraft/entity/ItemEntity;)Lnet/minecraft/entity/ItemEntity; Expected signature:
        // (Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;
    }
}
