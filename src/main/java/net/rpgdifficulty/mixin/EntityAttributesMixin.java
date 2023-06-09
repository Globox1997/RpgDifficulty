package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@Mixin(EntityAttributes.class)
public class EntityAttributesMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void registerMixin(String id, EntityAttribute attribute, CallbackInfoReturnable<EntityAttribute> info) {
        if (id.equals("generic.max_health")) {
            info.setReturnValue(Registry.register(Registries.ATTRIBUTE, id, new ClampedEntityAttribute("attribute.name.generic.max_health", 20.0, 1.0, 10240.0).setTracked(true)));
        }
    }
}
