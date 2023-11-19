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

package io.github.derechtepilz.infinity.util

import io.github.derechtepilz.infinity.gamemode.Gamemode
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

/********************
 *     GAMEMODE     *
 ********************/

fun sendTabListFooter(player: Player, gamemode: Gamemode) {
	when (gamemode) {
		Gamemode.MINECRAFT -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
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
		)

		Gamemode.INFINITY -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
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
		)

		Gamemode.UNKNOWN -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
			.color(NamedTextColor.GRAY)
			.append(Component.newline())
			.append(Component.text("/infinity gamemode <gamemode>").color(NamedTextColor.YELLOW))
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
		)
	}
}

/********************
 *     UTILITY      *
 ********************/

fun World.isEqualToAny(vararg worlds: World): Boolean {
	for (world in worlds) {
		if (world == this) {
			return true
		}
	}
	return false
}

fun String.capitalize(): String {
	return this.replaceFirstChar { firstChar -> if (firstChar.isLowerCase()) firstChar.titlecase(Locale.getDefault()) else firstChar.toString() }
}