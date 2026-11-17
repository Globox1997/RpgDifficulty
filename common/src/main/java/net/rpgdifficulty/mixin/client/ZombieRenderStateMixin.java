package net.rpgdifficulty.mixin.client;

import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.rpgdifficulty.access.ZombieRenderStateAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZombieRenderState.class)
public class ZombieRenderStateMixin implements ZombieRenderStateAccess {

    @Unique
    private boolean rpgdifficulty$big = false;

    @Override
    public boolean rpgdifficulty$isBig() {
        return rpgdifficulty$big;
    }

    @Override
    public void rpgdifficulty$setBig(boolean big) {
        rpgdifficulty$big = big;
    }
}
