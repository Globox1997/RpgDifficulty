package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.rpgdifficulty.access.ScreenAccess;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public class ScreenMixin implements ScreenAccess {

    @Override
    public <T extends Drawable> T addAnotherDrawable(T drawable) {
        return addDrawable(drawable);
    }

    @Shadow
    protected <T extends Drawable> T addDrawable(T drawable) {
        return drawable;
    }
}
