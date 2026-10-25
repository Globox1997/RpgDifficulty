package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.rpgdifficulty.RpgDifficultyClient;
import net.rpgdifficulty.RpgDifficultyMain;

@Environment(EnvType.CLIENT)
@Mixin(ZombieRenderer.class)
public abstract class ZombieEntityRendererMixin extends AbstractZombieRenderer<Zombie, ZombieRenderState, ZombieModel<ZombieRenderState>> {

    public ZombieEntityRendererMixin(Context ctx, ZombieModel<ZombieRenderState> model, ZombieModel<ZombieRenderState> babyModel, ArmorModelSet<ZombieModel<ZombieRenderState>> armorSet,
            ArmorModelSet<ZombieModel<ZombieRenderState>> babyArmorSet) {
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
