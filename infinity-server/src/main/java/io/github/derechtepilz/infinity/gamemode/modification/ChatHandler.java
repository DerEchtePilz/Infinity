package io.github.derechtepilz.infinity.gamemode.modification;

import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatHandler implements Listener {

	@EventHandler
	public void onChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		Gamemode playerGamemode = PlayerUtil.getGamemode(player);
		event.viewers().removeIf(audience -> audience instanceof Player p && PlayerUtil.getGamemode(p) != playerGamemode);
	}

}
