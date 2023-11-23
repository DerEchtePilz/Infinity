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

package io.github.derechtepilz.infinity.gamemode.worldmovement;

import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.PlayerUtil;
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
