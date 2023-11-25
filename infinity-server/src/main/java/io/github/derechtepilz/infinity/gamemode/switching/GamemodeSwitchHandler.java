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

package io.github.derechtepilz.infinity.gamemode.switching;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.gamemode.states.GamemodeState;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.github.derechtepilz.infinity.world.WorldCarver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;

public class GamemodeSwitchHandler implements Listener {

	/**
	 * The <code>PlayerTeleportEvent</code> is called <b>before</b> the player is actually teleported.
	 * <p>
	 * <code>event.getPlayer().getWorld()</code> will be equal to <code>event.getFrom().getWorld()</code>
	 */
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom();

		Gamemode currentGamemode = Gamemode.getFromKey(from.getWorld().getKey());
		Gamemode nextGamemode = Gamemode.getFromKey(event.getTo().getWorld().getKey());

		// Update sign regardless of gamemode but only if worlds have changed
		if (from.getWorld().getKey().equals(event.getTo().getWorld().getKey())) {
			return;
		}
		if (nextGamemode == Gamemode.INFINITY) {
			WorldCarver.LobbyCarver.setupPlayerSignWithDelay(player);
		}
		if (currentGamemode == nextGamemode) {
			return;
		}

		if (nextGamemode == Gamemode.INFINITY && !player.getPersistentDataContainer().has(Keys.STORY_STARTED.get(), PersistentDataType.BOOLEAN)) {
			// Initiate a sequence that requires a manual player action to actually start the story
			Infinity.getInstance().getPlayerPermissions().getOrDefault(player.getUniqueId(), player.addAttachment(Infinity.getInstance())).setPermission("infinity.startstory", true);
			player.updateCommands();
			player.sendMessage(Component.text().content("Start the story!")
				.color(NamedTextColor.GREEN)
				.decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE)
				.hoverEvent(Component.text().content("This will start the story for you!")
					.color(NamedTextColor.GREEN)
					.appendNewline()
					.append(Component.text().content("During the introduction sequence, you will not be able to switch your gamemode!").color(NamedTextColor.RED))
					.appendNewline()
					.append(Component.text().content("If you leave the server in any way possible, once you rejoin the server you will have to start the introduction sequence again!").color(NamedTextColor.RED))
					.build()
				)
				.clickEvent(ClickEvent.runCommand("/infinity startstory"))
			);
			Infinity.getInstance().getStartStoryTask().put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(Infinity.getInstance(), () -> {
				player.showTitle(Title.title(Infinity.getInstance().getInfinityComponent(),
					Component.text().content("Click the message in the chat to start the story!")
						.color(NamedTextColor.WHITE)
						.decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE)
						.build(), Title.Times.times(Duration.ZERO, Duration.ofMillis(300L), Duration.ZERO)));
			}, 0, 5));
		}

		if (nextGamemode != Gamemode.INFINITY) {
			PlayerUtil.terminateStoryTitleTask(player);
			removeStartStoryPermission(player);
		}

		player.setGameMode(event.getTo().getWorld().getKey().equals(Keys.WORLD_LOBBY.get()) ? GameMode.ADVENTURE : GameMode.SURVIVAL);
		PlayerUtil.sendTabListFooter(player, nextGamemode);

		if (event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND) {
			// Safeguard, so teleporting with /execute in <dimension> run teleport x y z doesn't cause a StackOverflowError
			// since Player#teleport() calls a PlayerTeleportEvent
			// For this to work, this plugin always needs to teleport with the PLUGIN cause, NOT the COMMAND cause
			return;
		}

		Location location = GamemodeState.valueOf(nextGamemode.name()).loadFor(player);
		event.setTo(location);
	}

	private void removeStartStoryPermission(Player player) {
		Infinity.getInstance().getPlayerPermissions().getOrDefault(player.getUniqueId(), player.addAttachment(Infinity.getInstance())).setPermission("infinity.startstory", false);
	}

}
