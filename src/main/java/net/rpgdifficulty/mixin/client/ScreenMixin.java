package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.rpgdifficulty.access.ScreenAccess;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public class ScreenMixin implements ScreenAccess {

    @Override
    public <T extends Renderable> T addAnotherDrawable(T drawable) {
        return addRenderableOnly(drawable);
    }

    @Shadow
    protected <T extends Renderable> T addRenderableOnly(T drawable) {
        return drawable;
    }
}
