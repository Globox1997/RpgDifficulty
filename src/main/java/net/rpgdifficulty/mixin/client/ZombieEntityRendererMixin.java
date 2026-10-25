package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Zombie;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;

@Environment(EnvType.CLIENT)
@Mixin(ZombieRenderer.class)
public abstract class ZombieEntityRendererMixin extends AbstractZombieRenderer<Zombie, ZombieModel<Zombie>> {

    public ZombieEntityRendererMixin(Context ctx, ZombieModel<Zombie> bodyModel, ZombieModel<Zombie> legsArmorModel, ZombieModel<Zombie> bodyArmorModel) {
        super(ctx, bodyModel, legsArmorModel, bodyArmorModel);
    }

    @Override
    protected void scale(Zombie entity, PoseStack matrices, float amount) {
        if (entity.getEntityData().get(((ZombieEntityAccess) entity).getTrackedDataBoolean()))
            matrices.scale(RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize);
        super.scale(entity, matrices, amount);
    }

}
