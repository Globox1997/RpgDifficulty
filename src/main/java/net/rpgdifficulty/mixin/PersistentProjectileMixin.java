package net.rpgdifficulty.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.rpgdifficulty.access.ProjectileAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileMixin implements ProjectileAccess {

    @Unique
    private boolean rpgScaled = false;

    @Override
    public boolean isRpgScaled() {
        return rpgScaled;
    }

    @Override
    public void setRpgScaled(boolean scaled) {
        this.rpgScaled = scaled;
    }
}