package net.rpgdifficulty.mixin.access;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccess {

    @Accessor("baseDamage")
    double getBaseDamage();
}
