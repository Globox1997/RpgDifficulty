package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;

@Environment(EnvType.CLIENT)
@Mixin(DrownedEntityRenderer.class)
public abstract class DrownedEntityRendererMixin extends ZombieBaseEntityRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>> {

    public DrownedEntityRendererMixin(Context ctx, DrownedEntityModel<DrownedEntity> bodyModel, DrownedEntityModel<DrownedEntity> legsArmorModel, DrownedEntityModel<DrownedEntity> bodyArmorModel) {
        super(ctx, bodyModel, legsArmorModel, bodyArmorModel);
    }

    @Override
    protected void scale(DrownedEntity entity, MatrixStack matrices, float amount) {
        if (entity.getDataTracker().get(((ZombieEntityAccess) entity).getTrackedDataBoolean()))
            matrices.scale(RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize);
        super.scale(entity, matrices, amount);
    }

}
