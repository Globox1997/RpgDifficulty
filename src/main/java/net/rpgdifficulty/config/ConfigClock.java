package net.rpgdifficulty.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfigClock implements Renderable {

    private final ResourceLocation CLOCK_TEXTURE = ResourceLocation.fromNamespaceAndPath("rpgdifficulty", "textures/gui/clock.png");
    private final Component translatableText;
    private final Minecraft minecraftClient;
    private int x;
    private int y;

    public ConfigClock(Minecraft client, int x, int y) {
        this.minecraftClient = client;
        this.x = x;
        this.y = y;
        translatableText = Component.translatable("text.autoconfig.rpgdifficulty.clock", client.level.getGameTime() / 1200);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.blit(CLOCK_TEXTURE, this.x, this.y, 0, 0, 16, 16, 16, 16);

        if (isMouseWithinBounds(16, 16, mouseX, mouseY)) {
            renderMousehoverTooltip(context, mouseX, mouseY);
        }
    }

    private boolean isMouseWithinBounds(int width, int height, double pointX, double pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }

    private void renderMousehoverTooltip(GuiGraphics context, int mouseX, int mouseY) {
        int j = minecraftClient.font.width(translatableText);
        int l = mouseX - j - 5;
        int m = mouseY;
        if (l < 0) {
            l = mouseX + 12;
        }
        context.renderTooltip(minecraftClient.font, translatableText, l, m);
    }

}
