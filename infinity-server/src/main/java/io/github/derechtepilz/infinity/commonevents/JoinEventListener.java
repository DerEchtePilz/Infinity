package io.github.derechtepilz.infinity.commonevents;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.PlayerJoinServerListener;
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener;
import io.github.derechtepilz.infinity.gamemode.modification.DeathHandler;
import io.github.derechtepilz.infinity.gamemode.modification.TablistHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEventListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PlayerJoinServerListener.sendJoinMessage(event.getPlayer());
		SignListener.getInstance().setupSignStates(event.getPlayer());
		DeathHandler.getInstance().loadPlayerSpawnPoints(event.getPlayer());
		TablistHandler.getInstance().addToTablist(event.getPlayer());
		Infinity.getInstance().getPlayerDataHandler().removeFromOfflinePlayers(event.getPlayer());
	}

}
