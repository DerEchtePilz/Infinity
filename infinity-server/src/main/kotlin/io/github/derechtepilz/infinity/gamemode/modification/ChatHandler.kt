/*
 *  Infinity - a Minecraft story-game for Paper servers
 *  Copyright (C) 2023  DerEchtePilz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.derechtepilz.infinity.gamemode.modification

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