package net.rpgdifficulty.neoforge.event;

import net.neoforged.bus.api.Event;
import net.rpgdifficulty.zone.ZoneSyncManager;

public class ZoneEnterEvent extends Event {

    private final ZoneSyncManager.ZoneEntry zone;

    public ZoneEnterEvent(ZoneSyncManager.ZoneEntry zone) {
        this.zone = zone;
    }

    public ZoneSyncManager.ZoneEntry getZone() {
        return zone;
    }

}
