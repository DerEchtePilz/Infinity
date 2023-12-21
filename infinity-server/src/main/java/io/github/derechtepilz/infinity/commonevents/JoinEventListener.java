package io.github.derechtepilz.infinity.commonevents;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.PlayerJoinServerListener;
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener;
import io.github.derechtepilz.infinity.gamemode.modification.DeathHandler;
import io.github.derechtepilz.infinity.gamemode.modification.TablistHandler;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class JoinEventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST) // It's not that critical, it is just necessary that this event runs as one of the last
	public void onJoin(PlayerJoinEvent event) {
		PlayerJoinServerListener.sendJoinMessage(event.getPlayer());
		PlayerJoinServerListener.setupPlayerData(event.getPlayer());
		SignListener.getInstance().setupSignStates(event.getPlayer());
		DeathHandler.getInstance().loadPlayerSpawnPoints(event.getPlayer());
		TablistHandler.getInstance().addToTablist(event.getPlayer());

		Infinity.getInstance().getPlayerGamemode().put(event.getPlayer().getUniqueId(), PlayerUtil.getGamemode(event.getPlayer()));

		// The reason why it should run last:
		event.getPlayer().getPersistentDataContainer().set(Keys.HAS_JOINED.get(), PersistentDataType.BOOLEAN, true);
	}

}
