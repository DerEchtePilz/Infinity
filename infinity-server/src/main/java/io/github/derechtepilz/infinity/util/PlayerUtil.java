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

package io.github.derechtepilz.infinity.util;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.gamemode.gameclass.GameClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerUtil {

	private PlayerUtil() {
	}

	private static final NamespacedKey gameClassKey = new NamespacedKey(Infinity.NAME, "gameclass");

	public static Gamemode getGamemode(Player player) {
		World world = player.getWorld();
		return Gamemode.getFromKey(world.getKey());
	}

	public static TextComponent getGameClass(Player player) {
		return GameClass.valueOf(player.getPersistentDataContainer().get(gameClassKey, PersistentDataType.STRING).toUpperCase()).get();
	}

	public static void setGameClass(Player player, GameClass gameClass) {
		player.getPersistentDataContainer().set(gameClassKey, PersistentDataType.STRING, gameClass.name().toLowerCase());
	}

	public static boolean hasDefaultGamemode(Player player) {
		return player.getPersistentDataContainer().has(Keys.DEFAULT_GAMEMODE.get());
	}

	public static void terminateStoryTitleTask(Player player) {
		Bukkit.getScheduler().cancelTask(Infinity.getInstance().getStartStoryTask().getOrDefault(player.getUniqueId(), -1));
		Infinity.getInstance().getStartStoryTask().remove(player.getUniqueId());
	}

	private static String getLastWorldKey(Player player, Gamemode gamemode) {
		return player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING, gamemode.getWorld().getKey().asString());
	}

	public static void sendTabListFooter(Player player, Gamemode gamemode) {
		switch (gamemode) {
			case MINECRAFT -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
				.color(NamedTextColor.GRAY)
				.append(Component.newline())
				.append(Component.text("/infinity gamemode infinity").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Set your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode <gamemode>").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Reset your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode reset").color(NamedTextColor.YELLOW))
			);

			case INFINITY -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
				.color(NamedTextColor.GRAY)
				.append(Component.newline())
				.append(Component.text("/infinity gamemode minecraft").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Set your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode <gamemode>").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Reset your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode reset").color(NamedTextColor.YELLOW))
			);
		}
	}

}
