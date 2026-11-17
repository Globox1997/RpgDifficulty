package net.rpgdifficulty;

import net.rpgdifficulty.network.BigZombiePayload;
import net.rpgdifficulty.access.ZombieEntityAccess;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class RpgDifficultyClient {

    public static final Identifier TESTING_HUD_ID = RpgDifficultyMain.identifierOf("testing_hud");

    public static void handleBigZombie(BigZombiePayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.level.getEntity(payload.entityId()) instanceof Zombie zombie) {
            ((ZombieEntityAccess) zombie).applyBig(payload.big());
        }
    }

    public static void renderTestingHud(GuiGraphicsExtractor drawContext, DeltaTracker tickDelta) {
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
    }

}
