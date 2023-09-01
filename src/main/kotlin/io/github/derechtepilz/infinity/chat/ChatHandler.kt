package io.github.derechtepilz.infinity.chat

import io.github.derechtepilz.infinity.gamemode.getGamemode
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@Suppress("OverrideOnly")
class ChatHandler : Listener {

	@EventHandler
	fun onChat(event: AsyncChatEvent) {
		val player = event.player
		event.isCancelled = true
		val playerGamemode = player.getGamemode()
		for (p in Bukkit.getOnlinePlayers()) {
			if (p.getGamemode() != playerGamemode) {
				continue
			}
			val message = event.renderer().render(player, player.displayName(), event.message(), p)
			p.sendMessage(message)
		}
	}

}