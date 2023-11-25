package io.github.derechtepilz.infinity.gamemode.states;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

public interface State<T> {

	default void loadFor(Player player) {
		loadFor(player, false);
	}

	default void loadFor(Player player, boolean delay) {
		throw new UnsupportedOperationException("This method has not been implemented!");
	}

	default T getNext() {
		throw new UnsupportedOperationException("This method has not been implemented!");
	}

	default TextComponent asString() {
		throw new UnsupportedOperationException("This method has not been implemented!");
	}

}
