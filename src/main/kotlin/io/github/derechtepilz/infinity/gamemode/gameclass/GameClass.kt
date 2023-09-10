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

package io.github.derechtepilz.infinity.gamemode.gameclass

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

enum class GameClass(private val formatted: TextComponent) {

	NO_CLASS_SELECTED(Component.text().content("No class selected").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false).build()),
	AIRBORN(Component.text().content("Airborn").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).build()),
	STONEBORN(Component.text().content("Stoneborn").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false).build()),
	LAVABORN(Component.text().content("Lavaborn").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false).build());

	fun get(): TextComponent {
		return formatted
	}

	enum class Dimension(private val formatted: TextComponent) {

		LOBBY(Component.text().content("Lobby").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).build()),
		SKY(Component.text().content("Sky").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).build()),
		STONE(Component.text().content("Stone").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false).build()),
		NETHER(Component.text().content("Nether").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false).build());

		fun get(): TextComponent {
			return formatted
		}

	}

}