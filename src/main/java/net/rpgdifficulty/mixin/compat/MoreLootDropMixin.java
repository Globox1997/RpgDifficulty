package net.rpgdifficulty.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.rpgdifficulty.api.MobStrengthener;
import net.rpgz.access.InventoryAccess;

@Mixin(MobStrengthener.class)
public class MoreLootDropMixin {

    @Redirect(method = "dropMoreLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
    private static ItemEntity dropMoreLootMixin(MobEntity mobEntity, ItemStack itemStack) {
        ((InventoryAccess) mobEntity).addingInventoryItems(itemStack);
        return null;
    }
}
