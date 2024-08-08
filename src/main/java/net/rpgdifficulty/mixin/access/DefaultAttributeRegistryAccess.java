package net.rpgdifficulty.mixin.access;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;

@Mixin(DefaultAttributeRegistry.class)
public interface DefaultAttributeRegistryAccess {

    @Accessor("DEFAULT_ATTRIBUTE_REGISTRY")
    static Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> getRegistry() {
        throw new AssertionError("This should not occur!");
    }
}
