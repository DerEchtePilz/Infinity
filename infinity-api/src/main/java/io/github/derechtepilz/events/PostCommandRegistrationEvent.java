package io.github.derechtepilz.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called after Infinity's commands are initialized.
 * <p>
 * This purely is a status event. It doesn't contain any information and cannot be used to modify Infinity's command registration.
 */
public class PostCommandRegistrationEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	@NotNull
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

}
