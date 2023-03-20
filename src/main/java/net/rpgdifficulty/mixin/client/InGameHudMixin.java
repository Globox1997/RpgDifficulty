package net.rpgdifficulty.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;
import net.rpgdifficulty.RpgDifficultyMain;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper {

    @Shadow
    @Final
    @Mutable
    private final MinecraftClient client;;

    public InGameHudMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
    private void renderMixin(MatrixStack matrixStack, float f, CallbackInfo info) {
        if (client.crosshairTarget != null && RpgDifficultyMain.CONFIG.hudTesting && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) client.crosshairTarget).getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) ((EntityHitResult) client.crosshairTarget).getEntity();
                int scaledWidth = client.getWindow().getScaledWidth();
                int scaledHeight = client.getWindow().getScaledHeight();
                RenderSystem.enableBlend();
                this.client.textRenderer.drawWithShadow(matrixStack, Registry.ENTITY_TYPE.getId(livingEntity.getType()).toString(), scaledWidth * 0.01F, scaledHeight * 0.95F, 16777215);
                this.client.textRenderer.drawWithShadow(matrixStack, "Health: " + livingEntity.getHealth(), scaledWidth * 0.01F, scaledHeight * 0.91F, 16777215);
                RenderSystem.disableBlend();
            }
        }
    }

}
