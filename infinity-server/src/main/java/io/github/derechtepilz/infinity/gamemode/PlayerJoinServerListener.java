package io.github.derechtepilz.infinity.gamemode;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinServerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		// TODO: Change messages sent to the player upon login
		if (!player.getPersistentDataContainer().has(Keys.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING)) {
			if (PlayerUtil.getGamemode(player) == Gamemode.MINECRAFT) {
				sendInfinitySuggestion(player);
			}
			sendDefaultGamemodeMessage(player);
		} else {
			sendResetDefaultGamemode(player);
		}
		PlayerUtil.sendTabListFooter(player, PlayerUtil.getGamemode(player));
		if (PlayerUtil.hasDefaultGamemode(player)) {
			Gamemode defaultGamemode = Gamemode.valueOf(player.getPersistentDataContainer().get(Keys.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING).toUpperCase());
			if (defaultGamemode == PlayerUtil.getGamemode(player)) {
				return;
			}
			PlayerUtil.switchGamemode(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
		}
	}

	private void sendInfinitySuggestion(Player player) {
		player.sendMessage(Component.text("Want to play ")
			.color(NamedTextColor.YELLOW)
			.append(Infinity.getInstance().getInfinityComponent())
			.append(Component.text("? Click ").color(NamedTextColor.YELLOW))
			.append(Component.text("[here]")
				.color(NamedTextColor.GREEN)
				.clickEvent(ClickEvent.runCommand("/infinity gamemode infinity"))
				.hoverEvent(Component.text("Click to play ")
					.color(NamedTextColor.YELLOW)
					.append(Infinity.getInstance().getInfinityComponent())
				)
			)
			.append(Component.text(" to play!").color(NamedTextColor.YELLOW))
		);
	}

	private void sendDefaultGamemodeMessage(Player player) {
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
					.append(Infinity.getInstance().getInfinityComponent())
					.append(Component.newline())
					.append(Component.text("in a previous session.").color(NamedTextColor.GRAY))
				)
			)
			.append(Component.text("]").color(NamedTextColor.WHITE))
			.append(Component.text(" [").color(NamedTextColor.WHITE))
			.append(Infinity.getInstance().getInfinityComponent()
				.clickEvent(ClickEvent.runCommand("/infinity defaultgamemode infinity"))
				.hoverEvent(Component.text("Set your default gamemode to ")
					.color(NamedTextColor.GRAY)
					.append(Infinity.getInstance().getInfinityComponent())
					.append(Component.newline())
					.append(Component.newline())
					.append(Component.text("This will make ").color(NamedTextColor.GRAY))
					.append(Infinity.getInstance().getInfinityComponent())
					.append(Component.text(" your default").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Component.text("gamemode and makes you join the last ").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Infinity.getInstance().getInfinityComponent())
					.append(Component.text(" world you were in if you").color(NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Component.text("joined ").color(NamedTextColor.GRAY))
					.append(Component.text("Minecraft").color(NamedTextColor.GREEN))
					.append(Component.text(" in a previous session.").color(NamedTextColor.GRAY))
				)
			)
			.append(Component.text("]").color(NamedTextColor.WHITE))
		);
	}

	private void sendResetDefaultGamemode(Player player) {
		player.sendMessage(Component.text("Reset default gamemode ")
			.color(NamedTextColor.GRAY)
			.append(Component.text("[here]")
				.color(NamedTextColor.GREEN)
				.clickEvent(ClickEvent.runCommand("/infinity defaultgamemode reset"))
			)
		);
	}

}
