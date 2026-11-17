package net.rpgdifficulty.access;

import net.minecraft.client.gui.components.Renderable;

public interface ScreenAccess {

    <T extends Renderable> T addAnotherDrawable(T drawable);
}
