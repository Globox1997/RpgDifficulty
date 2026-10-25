package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.monster.zombie.DrownedModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.rpgdifficulty.RpgDifficultyClient;
import net.rpgdifficulty.RpgDifficultyMain;

@Environment(EnvType.CLIENT)
@Mixin(DrownedRenderer.class)
public abstract class DrownedEntityRendererMixin extends AbstractZombieRenderer<Drowned, ZombieRenderState, DrownedModel> {

    public DrownedEntityRendererMixin(Context ctx, DrownedModel model, DrownedModel babyModel, ArmorModelSet<DrownedModel> armorSet, ArmorModelSet<DrownedModel> babyArmorSet) {
        super(ctx, model, babyModel, armorSet, babyArmorSet);
    }

    @Override
    protected void scale(ZombieRenderState state, PoseStack matrices) {
        if (state.getDataOrDefault(RpgDifficultyClient.BIG_ZOMBIE, false)) {
            matrices.scale(RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize, RpgDifficultyMain.CONFIG.bigZombieSize);
        }
        super.scale(state, matrices);
    }

}
