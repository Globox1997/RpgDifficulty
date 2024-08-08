package net.rpgdifficulty;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@Environment(EnvType.CLIENT)
public class RpgDifficultyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!client.options.hudHidden) {
                if (client.crosshairTarget != null && RpgDifficultyMain.CONFIG.hudTesting && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) client.crosshairTarget).getEntity();
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) ((EntityHitResult) client.crosshairTarget).getEntity();
                        int scaledWidth = drawContext.getScaledWindowWidth();
                        int scaledHeight = drawContext.getScaledWindowHeight();
                        RenderSystem.enableBlend();
                        drawContext.drawTextWithShadow(client.textRenderer, Registries.ENTITY_TYPE.getId(livingEntity.getType()).toString(), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.95F),
                                16777215);
                        drawContext.drawTextWithShadow(client.textRenderer, "Health: " + livingEntity.getHealth(), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.91F), 16777215);
                        RenderSystem.disableBlend();
                    }
                }
            }
        });
    }

}
