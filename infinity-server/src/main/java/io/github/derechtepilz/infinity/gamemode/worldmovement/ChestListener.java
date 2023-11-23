package io.github.derechtepilz.infinity.gamemode.worldmovement;

import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class ChestListener implements Listener {

	private final Vector northChestLocation = new Vector(0, 100, -1);
	private final Vector eastChestLocation = new Vector(1, 100, 0);
	private final Vector southChestLocation = new Vector(0, 100, 1);
	private final Vector westChestLocation = new Vector(-1, 100, 0);

	@EventHandler
	public void onEnderChestOpen(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (PlayerUtil.getGamemode(player) != Gamemode.INFINITY) {
			return;
		}
		Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Block clickedBlock = event.getClickedBlock();
		if (!(clickedBlock.getState() instanceof EnderChest)) {
			return;
		}
		Vector clickedLocation = event.getClickedBlock().getLocation().toVector();
		if (clickedLocation.equals(northChestLocation) || clickedLocation.equals(eastChestLocation) || clickedLocation.equals(southChestLocation) || clickedLocation.equals(westChestLocation)) {
			event.setCancelled(true);
			new EnderChestInventory(player);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (PlayerUtil.getGamemode(player) != Gamemode.INFINITY) {
			return;
		}
		Vector blockLocation = event.getBlock().getLocation().toVector();
		if (blockLocation.equals(northChestLocation) || blockLocation.equals(eastChestLocation) || blockLocation.equals(southChestLocation) || blockLocation.equals(westChestLocation)) {
			event.setCancelled(true);
		}
	}

}
