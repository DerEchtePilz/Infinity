package io.github.derechtepilz.infinity.util;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.gameclass.GameClass;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerUtil {

	private PlayerUtil() {
	}

	private static final NamespacedKey gameClassKey = new NamespacedKey(Infinity.NAME, "gameclass");

	public static TextComponent getGameClass(Player player) {
		return GameClass.valueOf(player.getPersistentDataContainer().get(gameClassKey, PersistentDataType.STRING).toUpperCase()).get();
	}

	public static void setGameClass(Player player, GameClass gameClass) {
		player.getPersistentDataContainer().set(gameClassKey, PersistentDataType.STRING, gameClass.name().toLowerCase());
	}

}
