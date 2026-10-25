package net.rpgdifficulty.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Attributes.class)
public class EntityAttributesMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void registerMixin(String id, Attribute attribute, CallbackInfoReturnable<Holder<Attribute>> info) {
        if (id.equals("max_health")) {
            info.setReturnValue(
                    Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Identifier.withDefaultNamespace(id), new RangedAttribute("attribute.name.max_health", 20.0, 1.0, 10240.0).setSyncable(true)));
        }
    }
}
