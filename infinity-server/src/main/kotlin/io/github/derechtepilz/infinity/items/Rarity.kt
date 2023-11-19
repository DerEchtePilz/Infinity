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

package io.github.derechtepilz.infinity.items

import io.github.derechtepilz.infinity.util.capitalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.format.TextDecoration.State

enum class Rarity(private val color: NamedTextColor, private val rarity: String) {

	UNCOMMON(NamedTextColor.YELLOW, "uncommon"),
	RARE(NamedTextColor.GREEN, "rare"),
	EPIC(NamedTextColor.AQUA, "epic"),
	LEGENDARY(NamedTextColor.RED, "legendary"),
	MYTHIC(NamedTextColor.DARK_RED, "mythic");

	fun color(): NamedTextColor {
		return color
	}

	fun rarityId(): String {
		return rarity
	}

	fun rarityString(): Component {
		val component = Component.text()
		if (this == MYTHIC) {
			component.append(Component.text("X")
				.decorations(mapOf(Pair(TextDecoration.BOLD, State.TRUE), Pair(TextDecoration.ITALIC, State.FALSE), Pair(TextDecoration.OBFUSCATED, State.TRUE)))
				.color(NamedTextColor.GOLD)
				.append(Component.text(" "))
			)
		}
		component.append(Component.text(rarity.capitalize())
			.decorations(mapOf(Pair(TextDecoration.ITALIC, State.FALSE), Pair(TextDecoration.BOLD, State.TRUE)))
			.color(color)
		)
		if (this == MYTHIC) {
			component.append(Component.text(" "))
				.append(Component.text("X")
					.decorations(mapOf(Pair(TextDecoration.BOLD, State.TRUE), Pair(TextDecoration.ITALIC, State.FALSE), Pair(TextDecoration.OBFUSCATED, State.TRUE)))
					.color(NamedTextColor.GOLD)
				)
		}
		return component.asComponent()
	}

	fun asTool(): Tool {
		return Tool.valueOf(this.name)
	}

	enum class Tool(private val materialType: String, private val modifier: String) {
		UNCOMMON("STONE", "+50%"),
		RARE("IRON", "+100%"),
		EPIC("DIAMOND", "+200%"),
		LEGENDARY("NETHERITE", "+300%"),
		MYTHIC("GOLDEN", "+500%");

		fun asPickaxe(): String {
			return "${materialType}_PICKAXE"
		}

		fun asAxe(): String {
			return "${materialType}_AXE"
		}

		fun asShovel(): String {
			return "${materialType}_SHOVEL"
		}

		fun asSword(): String {
			return "${materialType}_SWORD"
		}

		fun asHoe(): String {
			return "${materialType}_HOE"
		}

		fun modifier(): String {
			return modifier
		}

	}

	fun nextRarity(): Rarity {
		return when (this) {
			UNCOMMON -> RARE
			RARE -> EPIC
			EPIC -> LEGENDARY
			LEGENDARY -> MYTHIC
			MYTHIC -> MYTHIC
		}
	}

	fun previousRarity(): Rarity {
		return when (this) {
			MYTHIC -> LEGENDARY
			LEGENDARY -> EPIC
			EPIC -> RARE
			RARE -> UNCOMMON
			UNCOMMON -> UNCOMMON
		}
	}

}