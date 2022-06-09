package net.rpgdifficulty.config;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfigClock extends DrawableHelper implements Drawable {

    private final Identifier CLOCK_TEXTURE = new Identifier("rpgdifficulty", "textures/gui/clock.png");
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float var4) {
        RenderSystem.setShaderTexture(0, CLOCK_TEXTURE);
        DrawableHelper.drawTexture(matrices, this.x, this.y, 0, 0, 16, 16, 16, 16);

        if (isMouseWithinBounds(16, 16, mouseX, mouseY))
            renderMousehoverTooltip(matrices, mouseX, mouseY);
    }

    private boolean isMouseWithinBounds(int width, int height, double pointX, double pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }

    private void renderMousehoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        int j = minecraftClient.textRenderer.getWidth(translatableText);
        int l = mouseX - j - 5;
        int m = mouseY;
        if (l < 0)
            l = mouseX + 12;

        int n = m - 3;
        this.fillGradient(matrices, l - 3, n, l + j + 3, m + 8 + 3, -1073741824, -1073741824);
        minecraftClient.textRenderer.drawWithShadow(matrices, translatableText, (float) l, (float) m, 0xFFFFFF);
    }

}
