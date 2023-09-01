package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.util.Keys
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

class PlayerListener : Listener {

	private val mm: MiniMessage = MiniMessage.miniMessage()
	private val infinityComponent = mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>")

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		val player: Player = event.player
		if (!player.persistentDataContainer.has(Keys.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING)) {
			if (event.player.getGamemode() == Gamemode.MINECRAFT) {
				sendInfinitySuggestion(player)
			}
			sendDefaultGamemodeMessage(player)
		} else {
			sendResetDefaultGamemode(player)
		}
		sendTabListFooter(player, player.getGamemode())
		if (player.hasDefaultGamemode()) {
			val defaultGamemode = Gamemode.valueOf(player.persistentDataContainer.get(Keys.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING)!!.uppercase())
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