package net.rpgdifficulty;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.rpgdifficulty.zone.ClientZoneTracker;

@Environment(EnvType.CLIENT)
public class RpgDifficultyClient implements ClientModInitializer {

    public static final RenderStateDataKey<Boolean> BIG_ZOMBIE = RenderStateDataKey.create(() -> "rpgdifficulty:big_zombie");

    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("rpgdifficulty", "testing_hud"), (drawContext, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();
            if (RpgDifficultyMain.CONFIG.hudTesting && !client.gui.hud.isHidden() && client.hitResult != null && client.hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) client.hitResult).getEntity();
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) ((EntityHitResult) client.hitResult).getEntity();
                    int scaledWidth = drawContext.guiWidth();
                    int scaledHeight = drawContext.guiHeight();
                    drawContext.text(client.font, BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString(), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.95F),
                            0xFFFFFFFF);
                    drawContext.text(client.font, Component.translatable("text.rpgdifficulty.hud_health", livingEntity.getHealth()), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.91F), 0xFFFFFFFF);
                }
            }
        });
        ClientZoneTracker.init();
    }

}
