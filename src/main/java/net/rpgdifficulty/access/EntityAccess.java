package net.rpgdifficulty.access;

import net.minecraft.entity.data.TrackedData;

public interface EntityAccess {

    void setBig();

    TrackedData<Boolean> getTrackedDataBoolean();
}