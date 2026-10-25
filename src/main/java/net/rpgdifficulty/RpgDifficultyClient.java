package net.rpgdifficulty;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.rpgdifficulty.zone.ClientZoneTracker;

@Environment(EnvType.CLIENT)
public class RpgDifficultyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();
            if (RpgDifficultyMain.CONFIG.hudTesting && !client.options.hideGui && client.hitResult != null && client.hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) client.hitResult).getEntity();
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) ((EntityHitResult) client.hitResult).getEntity();
                    int scaledWidth = drawContext.guiWidth();
                    int scaledHeight = drawContext.guiHeight();
                    RenderSystem.enableBlend();
                    drawContext.drawString(client.font, BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString(), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.95F),
                            16777215);
                    drawContext.drawString(client.font, Component.translatable("text.rpgdifficulty.hud_health", livingEntity.getHealth()), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.91F), 16777215);
                    RenderSystem.disableBlend();
                }
            }
        });
        ClientZoneTracker.init();
    }

}
