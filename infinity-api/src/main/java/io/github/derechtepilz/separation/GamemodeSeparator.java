package io.github.derechtepilz.separation;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface GamemodeSeparator {

	void updateInventory(Player player, Map<UUID, String> inventory);

	void updatePotionEffects(Player player, Map<UUID, String> potionEffects);

	void updateHealthHunger(Player player, Map<UUID, String> healthHunger);

	void updateExperience(Player player, Map<UUID, String> experience);

}
