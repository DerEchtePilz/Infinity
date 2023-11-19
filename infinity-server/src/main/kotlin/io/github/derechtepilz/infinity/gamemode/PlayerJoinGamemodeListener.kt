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

package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.gamemode.switching.switchGamemode
import io.github.derechtepilz.infinity.util.Keys0
import io.github.derechtepilz.infinity.util.sendTabListFooter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType

class PlayerJoinGamemodeListener : Listener {

	private val mm: MiniMessage = MiniMessage.miniMessage()
	private val infinityComponent = mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>")

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		val player: Player = event.player
		// TODO: Change messages sent to the player upon login
		if (!player.persistentDataContainer.has(Keys0.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING)) {
			if (event.player.getGamemode() == Gamemode.MINECRAFT) {
				sendInfinitySuggestion(player)
			}
			sendDefaultGamemodeMessage(player)
		} else {
			sendResetDefaultGamemode(player)
		}
		sendTabListFooter(player, player.getGamemode())
		if (player.hasDefaultGamemode()) {
			val defaultGamemode = Gamemode.valueOf(player.persistentDataContainer.get(Keys0.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING)!!.uppercase())
			if (defaultGamemode == player.getGamemode()) {
				return
			}
			player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN)
		}
	}

	private fun sendInfinitySuggestion(player: Player) {
		player.sendMessage(Component.text("Want to play ")
			.color(NamedTextColor.YELLOW)
			.append(infinityComponent)
			.append(Component.text("? Click ").color(NamedTextColor.YELLOW))
			.append(Component.text("[here]")
				.color(NamedTextColor.GREEN)
				.clickEvent(ClickEvent.runCommand("/infinity gamemode infinity"))
				.hoverEvent(Component.text("Click to play ")
					.color(NamedTextColor.YELLOW)
					.append(infinityComponent)
				)
			)
			.append(Component.text(" to play!").color(NamedTextColor.YELLOW))
		)
	}

	private fun sendDefaultGamemodeMessage(player: Player) {
		player.sendMessage(Component.text("Set default gamemode: ")
			.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			.color(NamedTextColor.GRAY)
			.append(Component.text("[").color(NamedTextColor.WHITE))
			.append(Component.text("Minecraft")
				.color(NamedTextColor.GREEN)
				.clickEvent(ClickEvent.runCommand("/infinity defaultgamemode minecraft"))
				.hoverEvent(Component.text("Set your default gamemode to ")
					.color(NamedTextColor.GRAY)
					.append(Component.text("Minecraft").color(NamedTextColor.GREEN))
					.append(Component.newline())
					.append(Component.newline())
					.append(Component.text("This will make ").color(NamedTextColor.GRAY))
					.append(Component.text("Minecraft").color(NamedTextColor.GREEN))
					.append(Component.text(" your default gamemode").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Component.text("and makes you join the last ").color(NamedTextColor.GRAY))
					.append(Component.text("Minecraft ").color(NamedTextColor.GREEN))
					.append(Component.text("world").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Component.text("you were in if you joined ").color(NamedTextColor.GRAY))
					.append(infinityComponent)
					.append(Component.newline())
					.append(Component.text("in a previous session.").color(NamedTextColor.GRAY))
				)
			)
			.append(Component.text("]").color(NamedTextColor.WHITE))
			.append(Component.text(" [").color(NamedTextColor.WHITE))
			.append(infinityComponent
				.clickEvent(ClickEvent.runCommand("/infinity defaultgamemode infinity"))
				.hoverEvent(Component.text("Set your default gamemode to ")
					.color(NamedTextColor.GRAY)
					.append(infinityComponent)
					.append(Component.newline())
					.append(Component.newline())
					.append(Component.text("This will make ").color(NamedTextColor.GRAY))
					.append(infinityComponent)
					.append(Component.text(" your default").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Component.text("gamemode and makes you join the last ").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(infinityComponent)
					.append(Component.text(" world you were in if you").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Component.text("joined ").color(NamedTextColor.GRAY))
					.append(Component.text("Minecraft").color(NamedTextColor.GREEN))
					.append(Component.text(" in a previous session.").color(NamedTextColor.GRAY))
				)
			)
			.append(Component.text("]").color(NamedTextColor.WHITE))
		)
	}

	private fun sendResetDefaultGamemode(player: Player) {
		player.sendMessage(Component.text("Reset default gamemode ")
			.color(NamedTextColor.GRAY)
			.append(Component.text("[here]")
				.color(NamedTextColor.GREEN)
				.clickEvent(ClickEvent.runCommand("/infinity defaultgamemode reset"))
			)
		)
	}

}