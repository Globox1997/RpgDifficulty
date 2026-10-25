package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.monster.Drowned;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;

@Environment(EnvType.CLIENT)
@Mixin(DrownedRenderer.class)
public abstract class DrownedEntityRendererMixin extends AbstractZombieRenderer<Drowned, DrownedModel<Drowned>> {

    public DrownedEntityRendererMixin(Context ctx, DrownedModel<Drowned> bodyModel, DrownedModel<Drowned> legsArmorModel, DrownedModel<Drowned> bodyArmorModel) {
        super(ctx, bodyModel, legsArmorModel, bodyArmorModel);
    }

    @Override
    protected void scale(Drowned entity, PoseStack matrices, float amount) {
        if (entity.getEntityData().get(((ZombieEntityAccess) entity).getTrackedDataBoolean()))
            matrices.scale(RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize);
        super.scale(entity, matrices, amount);
    }

}
