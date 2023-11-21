package io.github.derechtepilz.infinity.gamemode.gameclass;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum GameClass {

	NO_CLASS_SELECTED(Component.text().content("No class selected").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false).build()),
	AIRBORN(Component.text().content("Airborn").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).build()),
	STONEBORN(Component.text().content("Stoneborn").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false).build()),
	LAVABORN(Component.text().content("Lavaborn").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false).build());

	private final TextComponent formatted;

	GameClass(TextComponent formatted) {
		this.formatted = formatted;
	}

	public TextComponent get() {
		return formatted;
	}

	public enum Dimension {

		LOBBY(Component.text().content("Lobby").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).build()),
		SKY(Component.text().content("Sky").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).build()),
		STONE(Component.text().content("Stone").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false).build()),
		NETHER(Component.text().content("Nether").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false).build());

		private final TextComponent formatted;

		Dimension(TextComponent formatted) {
			this.formatted = formatted;
		}

		public TextComponent get() {
			return formatted;
		}

	}

}