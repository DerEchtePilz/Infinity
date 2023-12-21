package io.github.derechtepilz.infinity.commonevents;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener;
import io.github.derechtepilz.infinity.gamemode.modification.DeathHandler;
import io.github.derechtepilz.infinity.gamemode.modification.TablistHandler;
import io.github.derechtepilz.infinity.gamemode.story.introduction.PlayerQuitInStoryHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEventListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		SignListener.getInstance().saveSignStatesFor(event.getPlayer());
		DeathHandler.getInstance().saveSpawnPointsFor(event.getPlayer());
		TablistHandler.getInstance().removeFromTablist(event.getPlayer());
		PlayerQuitInStoryHandler.resetIntroduction(event.getPlayer());
	}

}
