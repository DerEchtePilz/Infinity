package io.github.derechtepilz.infinity.gamemode.modification;

import io.github.derechtepilz.infinity.gamemode.Gamemode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class PortalDisableHandler implements Listener {

	@EventHandler
	public void onCreatePortal(PortalCreateEvent event) {
		if (Gamemode.getFromKey(event.getWorld().getKey()) == Gamemode.INFINITY) {
			event.setCancelled(true);
		}
	}

}
