package net.rpgdifficulty.mixin.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.EntityAccess;

@Environment(EnvType.CLIENT)
@Mixin(ZombieBaseEntityRenderer.class)
public abstract class ZombieBaseEntityRendererMixin<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {

    public ZombieBaseEntityRendererMixin(EntityRendererFactory.Context ctx, M bipedEntityModel, float f, float g, float h, float i) {
        super(ctx, bipedEntityModel, f, g, h, i);
    }

    @Override
    public void scale(ZombieEntity zombieEntity, MatrixStack matrixStack, float f) {
        if (zombieEntity.getDataTracker().get(((EntityAccess) zombieEntity).getTrackedDataBoolean()))
            matrixStack.scale(RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize);
    }

}
