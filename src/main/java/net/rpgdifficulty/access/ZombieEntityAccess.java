package net.rpgdifficulty.access;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface ZombieEntityAccess {

    void setBig();

    EntityDataAccessor<Boolean> getTrackedDataBoolean();
}