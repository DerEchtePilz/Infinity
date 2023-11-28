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

package io.github.derechtepilz.infinity.items;

import io.github.derechtepilz.infinity.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Map;

public enum Rarity {

	UNCOMMON(NamedTextColor.YELLOW, "uncommon"),
	RARE(NamedTextColor.GREEN, "rare"),
	EPIC(NamedTextColor.AQUA, "epic"),
	LEGENDARY(NamedTextColor.RED, "legendary"),
	MYTHIC(NamedTextColor.DARK_RED, "mythic");

	private final NamedTextColor color;
	private final String rarity;

	Rarity(NamedTextColor color, String rarity) {
		this.color = color;
		this.rarity = rarity;
	}

	public NamedTextColor color() {
		return color;
	}

	public String rarityId() {
		return rarity;
	}

	public Component rarityString() {
		TextComponent.Builder component = Component.text();
		if (this == MYTHIC) {
			component.append(Component.text().content("X")
				.decorations(Map.of(TextDecoration.BOLD, TextDecoration.State.TRUE, TextDecoration.ITALIC, TextDecoration.State.FALSE, TextDecoration.OBFUSCATED, TextDecoration.State.TRUE))
				.color(NamedTextColor.GOLD)
				.append(Component.text().content(" "))
			);
		}
		component.append(Component.text().content(StringUtil.capitalize(rarity))
			.decorations(Map.of(TextDecoration.ITALIC, TextDecoration.State.FALSE, TextDecoration.BOLD, TextDecoration.State.TRUE))
			.color(color)
		);
		if (this == MYTHIC) {
			component.append(Component.text(" "))
				.append(Component.text("X")
					.decorations(Map.of(TextDecoration.BOLD, TextDecoration.State.TRUE, TextDecoration.ITALIC, TextDecoration.State.FALSE, TextDecoration.OBFUSCATED, TextDecoration.State.TRUE))
					.color(NamedTextColor.GOLD)
				);
		}
		return component.asComponent();
	}

	public Tool asTool() {
		return Tool.valueOf(this.name());
	}

	public enum Tool {

		UNCOMMON("STONE", "+50%"),
		RARE("IRON", "+100%"),
		EPIC("DIAMOND", "+200%"),
		LEGENDARY("NETHERITE", "+300%"),
		MYTHIC("GOLDEN", "+500%");

		private final String materialType;
		private final String modifier;

		Tool(String materialType, String modifier) {
			this.materialType = materialType;
			this.modifier = modifier;
		}

		public String asPickaxe() {
			return materialType + "_PICKAXE";
		}

		public String asAxe() {
			return materialType + "_AXE";
		}

		public String asShovel() {
			return materialType + "_SHOVEL";
		}

		public String asSword() {
			return materialType + "_SWORD";
		}

		public String aHoe() {
			return materialType + "_HOE";
		}

		public String modifier() {
			return modifier;
		}

	}

	public Rarity nextRarity() {
		return switch (this) {
			case UNCOMMON -> RARE;
			case RARE -> EPIC;
			case EPIC -> LEGENDARY;
			case LEGENDARY, MYTHIC -> MYTHIC;
		};
	}

	public Rarity previousRarity() {
		return switch (this) {
			case MYTHIC -> LEGENDARY;
			case LEGENDARY -> EPIC;
			case EPIC -> RARE;
			case RARE, UNCOMMON -> UNCOMMON;
		};
	}

}
