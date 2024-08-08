package net.rpgdifficulty.access;

import net.minecraft.entity.data.TrackedData;

public interface ZombieEntityAccess {

    void setBig();

    TrackedData<Boolean> getTrackedDataBoolean();
}