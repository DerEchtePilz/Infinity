package io.github.derechtepilz.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when worlds created by the Infinity API are created or loaded.
 */
public class WorldCreateLoadEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @NotNull
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
