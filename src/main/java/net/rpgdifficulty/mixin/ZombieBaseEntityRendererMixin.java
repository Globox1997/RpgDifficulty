package net.rpgdifficulty.mixin;

import net.minecraft.client.render.entity.EntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.rpgdifficulty.access.EntityAccess;

@Mixin(ZombieBaseEntityRenderer.class)
public abstract class ZombieBaseEntityRendererMixin<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {

    public ZombieBaseEntityRendererMixin(EntityRendererFactory.Context ctx, M bipedEntityModel, float f, float g, float h, float i) {
        super(ctx, bipedEntityModel, f, g, h, i);
    }

    @Override
    public void scale(ZombieEntity zombieEntity, MatrixStack matrixStack, float f) {
        if (zombieEntity.getDataTracker().get(((EntityAccess) zombieEntity).getTrackedDataBoolean()))
            matrixStack.scale(1.3F, 1.3F, 1.3F);
    }

}
