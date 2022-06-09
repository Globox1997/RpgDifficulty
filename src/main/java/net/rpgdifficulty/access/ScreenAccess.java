package net.rpgdifficulty.access;

import net.minecraft.client.gui.Drawable;

public interface ScreenAccess {

    <T extends Drawable> T addAnotherDrawable(T drawable);
}
