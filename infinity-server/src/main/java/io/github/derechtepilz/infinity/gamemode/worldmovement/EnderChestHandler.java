package io.github.derechtepilz.infinity.gamemode.worldmovement;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.util.Keys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestHandler implements Listener {

	@EventHandler
	public void onEnderChestClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) return;
		if (event.getClickedInventory() != event.getView().getTopInventory()) return;
		if (event.getView().title() != EnderChestInventory.getInstance().getEnderChestTitle()) return;
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		if (event.getCurrentItem() == null) return;
		ItemStack item = event.getCurrentItem();
		Inventory inventory = event.getClickedInventory();
		if (item.equals(EnderChestInventory.getInstance().getClassOneTeleport())) {
			if (event.isRightClick()) {
				player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 1.0f));
				inventory.setItem(21, EnderChestInventory.getInstance().getClassTwoTeleport());
				return;
			}
			if (event.isLeftClick()) {
				player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f));
				Bukkit.getScheduler().runTaskLater(Infinity.getInstance(), () -> {
					player.teleport(new Location(Bukkit.getWorld(Keys.WORLD_SKY.get()), 0.5, 101.0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
				}, 20);
			}
			return;
		}
		if (item.equals(EnderChestInventory.getInstance().getClassTwoTeleport())) {
			if (event.isRightClick()) {
				player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 1.0f));
				inventory.setItem(21, EnderChestInventory.getInstance().getClassThreeTeleport());
				return;
			}
			if (event.isLeftClick()) {
				player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f));
				Bukkit.getScheduler().runTaskLater(Infinity.getInstance(), () -> {
					player.teleport(new Location(Bukkit.getWorld(Keys.WORLD_STONE.get()), 0.5, 101.0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
				}, 20);
			}
			return;
		}
		if (item.equals(EnderChestInventory.getInstance().getClassThreeTeleport())) {
			if (event.isRightClick()) {
				player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 1.0f));
				inventory.setItem(21, EnderChestInventory.getInstance().getClassOneTeleport());
				return;
			}
			if (event.isLeftClick()) {
				player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f));
				Bukkit.getScheduler().runTaskLater(Infinity.getInstance(), () -> {
					player.teleport(new Location(Bukkit.getWorld(Keys.WORLD_NETHER.get()), 0.5, 101.0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
				}, 20);
			}
		}
		if (item.equals(EnderChestInventory.getInstance().getLobbyTeleport())) {
			player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f));
			Bukkit.getScheduler().runTaskLater(Infinity.getInstance(), () -> {
				player.teleport(new Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get()), 0.5, 101.0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
			}, 20);
		}
	}
}
