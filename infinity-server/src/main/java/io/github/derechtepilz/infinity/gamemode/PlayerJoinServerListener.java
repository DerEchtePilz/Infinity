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

package io.github.derechtepilz.infinity.gamemode;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.data.InfinityData;
import io.github.derechtepilz.infinity.data.MinecraftData;
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer;
import io.github.derechtepilz.infinity.gamemode.states.GamemodeState;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinServerListener {

	private PlayerJoinServerListener() {}

	public static void sendJoinMessage(Player player) {
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
			GamemodeState.valueOf(defaultGamemode.name()).loadFor(player);
		}
	}

	public static void setupPlayerData(Player player) {
		if (player.getPersistentDataContainer().has(Keys.HAS_JOINED.get())) {
			return;
		}
		String inventoryData = InventorySerializer.serialize(player);
		String experienceData = ExperienceSerializer.serialize(player);
		String healthHungerData = HealthHungerSerializer.serialize(player);
		String potionEffectData = EffectSerializer.serialize(player);

		MinecraftData minecraftData =  Infinity.getInstance().getMinecraftData();
		InfinityData infinityData =  Infinity.getInstance().getInfinityData();

		minecraftData.setInventoryData(player.getUniqueId(), inventoryData);
		minecraftData.setExperienceData(player.getUniqueId(), experienceData);
		minecraftData.setHealthHungerData(player.getUniqueId(), healthHungerData);
		minecraftData.setPotionEffectData(player.getUniqueId(), potionEffectData);

		infinityData.setInventoryData(player.getUniqueId(), inventoryData);
		infinityData.setExperienceData(player.getUniqueId(), experienceData);
		infinityData.setHealthHungerData(player.getUniqueId(), healthHungerData);
		infinityData.setPotionEffectData(player.getUniqueId(), potionEffectData);
	}

	public static void updatePlayerGamemode(Player player) {
		player.sendMessage(Component.text().content("An Infinity backup has been loaded. Because of that, your world, inventories and some stats might have changed.")
			.color(NamedTextColor.RED)
			.build()
		);
		GamemodeState.valueOf(Infinity.getInstance().getPlayerGamemode().get(player.getUniqueId()).name()).loadFor(player);
	}

	private static void sendInfinitySuggestion(Player player) {
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

	private static void sendDefaultGamemodeMessage(Player player) {
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

	private static void sendResetDefaultGamemode(Player player) {
		player.sendMessage(Component.text("Reset default gamemode ")
			.color(NamedTextColor.GRAY)
			.append(Component.text("[here]")
				.color(NamedTextColor.GREEN)
				.clickEvent(ClickEvent.runCommand("/infinity defaultgamemode reset"))
			)
		);
	}

}
