package net.rpgdifficulty.zone;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ClientZoneEvents {

    @FunctionalInterface
    public interface ZoneEnter {
        void onEnter(ZoneSyncManager.ZoneEntry zone);
    }

    public static final Event<ZoneEnter> ENTER = EventFactory.createArrayBacked(ZoneEnter.class,
            listeners -> zone -> {
                for (ZoneEnter listener : listeners) {
                    listener.onEnter(zone);
                }
            });

}
