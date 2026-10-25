package net.rpgdifficulty.mixin.access;

import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DefaultAttributes.class)
public interface DefaultAttributeRegistryAccess {

    @Accessor("SUPPLIERS")
    static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getRegistry() {
        throw new AssertionError("This should not occur!");
    }
}
