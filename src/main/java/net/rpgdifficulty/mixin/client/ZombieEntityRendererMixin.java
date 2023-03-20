package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;

@Environment(EnvType.CLIENT)
@Mixin(ZombieEntityRenderer.class)
public abstract class ZombieEntityRendererMixin extends ZombieBaseEntityRenderer<ZombieEntity, ZombieEntityModel<ZombieEntity>> {

    public ZombieEntityRendererMixin(Context ctx, ZombieEntityModel<ZombieEntity> bodyModel, ZombieEntityModel<ZombieEntity> legsArmorModel, ZombieEntityModel<ZombieEntity> bodyArmorModel) {
        super(ctx, bodyModel, legsArmorModel, bodyArmorModel);
    }

    @Override
    protected void scale(ZombieEntity entity, MatrixStack matrices, float amount) {
        if (entity.getDataTracker().get(((ZombieEntityAccess) entity).getTrackedDataBoolean()))
            matrices.scale(RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize);
        super.scale(entity, matrices, amount);
    }

}
