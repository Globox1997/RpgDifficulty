package net.rpgdifficulty.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfigClock implements Drawable {

    private final Identifier CLOCK_TEXTURE = Identifier.of("rpgdifficulty", "textures/gui/clock.png");
    private final Text translatableText;
    private final MinecraftClient minecraftClient;
    private int x;
    private int y;

    public ConfigClock(MinecraftClient client, int x, int y) {
        this.minecraftClient = client;
        this.x = x;
        this.y = y;
        translatableText = Text.translatable("text.autoconfig.rpgdifficulty.clock", client.world.getTime() / 1200);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(CLOCK_TEXTURE, this.x, this.y, 0, 0, 16, 16, 16, 16);

        if (isMouseWithinBounds(16, 16, mouseX, mouseY)) {
            renderMousehoverTooltip(context, mouseX, mouseY);
        }
    }

    private boolean isMouseWithinBounds(int width, int height, double pointX, double pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }

    private void renderMousehoverTooltip(DrawContext context, int mouseX, int mouseY) {
        int j = minecraftClient.textRenderer.getWidth(translatableText);
        int l = mouseX - j - 5;
        int m = mouseY;
        if (l < 0) {
            l = mouseX + 12;
        }
        context.drawTooltip(minecraftClient.textRenderer, translatableText, l, m);
    }

}
