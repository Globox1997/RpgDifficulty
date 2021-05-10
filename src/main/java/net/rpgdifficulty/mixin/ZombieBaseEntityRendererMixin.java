package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;

@Mixin(ZombieBaseEntityRenderer.class)
public abstract class ZombieBaseEntityRendererMixin<T extends ZombieEntity, M extends ZombieEntityModel<T>>
        extends BipedEntityRenderer<T, M> {

    public ZombieBaseEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher, M bipedEntityModel, float f,
            float g, float h, float i) {
        super(entityRenderDispatcher, bipedEntityModel, f, g, h, i);
    }

    @Override
    public void scale(ZombieEntity zombieEntity, MatrixStack matrixStack, float f) {
        if (zombieEntity.getHeight() > 2.1F) {
            matrixStack.scale(1.3F, 1.3F, 1.3F);
        }

    }

}
