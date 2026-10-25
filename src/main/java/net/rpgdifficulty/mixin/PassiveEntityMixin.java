package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.api.MobStrengthener;
import net.rpgdifficulty.mixin.access.DefaultAttributeRegistryAccess;

@Mixin(AgeableMob.class)
public abstract class PassiveEntityMixin extends PathfinderMob {

    public PassiveEntityMixin(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "ageBoundaryReached", at = @At(value = "HEAD"))
    protected void onGrowUpMixin(CallbackInfo info) {
        if (!this.level().isClientSide() && getAge() == 0
                && DefaultAttributeRegistryAccess.getRegistry().get(this.getType()) != null
                && Math.abs(DefaultAttributeRegistryAccess.getRegistry().get(this.getType()).getBaseValue(Attributes.MAX_HEALTH)
                - this.getAttributeBaseValue(Attributes.MAX_HEALTH)) <= 0.0001D) {
            MobStrengthener.changeAttributes(this, (ServerLevel) this.level(), null, false);
        }
    }

    @Shadow
    public int getAge() {
        return 0;
    }
}
