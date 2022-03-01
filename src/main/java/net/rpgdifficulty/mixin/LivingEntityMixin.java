package net.rpgdifficulty.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.nameplate.access.MobEntityAccess;
import net.rpgdifficulty.RpgDifficultyMain;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "dropLoot", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, Identifier identifier, LootTable lootTable, LootContext.Builder builder) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot && RpgDifficultyMain.isNameplateLoaded && (Object) this instanceof MobEntity) {
            int level = ((MobEntityAccess) (Object) this).getMobRpgLevel();
            if (level > 0) {
                float dropChance = level * RpgDifficultyMain.CONFIG.moreLootChance;
                if (dropChance > RpgDifficultyMain.CONFIG.maxLootChance)
                    dropChance = RpgDifficultyMain.CONFIG.maxLootChance;
                if (this.world.random.nextFloat() <= dropChance) {
                    List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));
                    for (int i = 0; i < list.size(); i++) {
                        if (this.world.random.nextFloat() < RpgDifficultyMain.CONFIG.chanceForEachItem)
                            continue;
                        ItemStack stack = list.get(i);
                        stack.increment((int) (stack.getCount() * dropChance));
                        this.dropStack(stack);
                    }
                }
            }
        }

    }
}
