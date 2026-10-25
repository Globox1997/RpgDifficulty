package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.rpgdifficulty.RpgDifficultyClient;
import net.rpgdifficulty.access.ZombieEntityAccess;

@Environment(EnvType.CLIENT)
@Mixin(AbstractZombieRenderer.class)
public abstract class AbstractZombieRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/zombie/Zombie;Lnet/minecraft/client/renderer/entity/state/ZombieRenderState;F)V", at = @At("TAIL"))
    private void extractRenderStateMixin(Zombie entity, ZombieRenderState state, float partialTicks, CallbackInfo info) {
        state.setData(RpgDifficultyClient.BIG_ZOMBIE, entity.getEntityData().get(((ZombieEntityAccess) entity).getTrackedDataBoolean()));
    }

}
