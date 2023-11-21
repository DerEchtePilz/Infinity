package io.github.derechtepilz.infinity.gamemode.modification;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AdvancementDisableHandler implements Listener {

	@EventHandler
	public void onGainCriterion(PlayerAdvancementCriterionGrantEvent event) {
		Player player = event.getPlayer();
		if (PlayerUtil.getGamemode(player) == Gamemode.INFINITY) {
			event.setCancelled(true);
		}
	}

}
