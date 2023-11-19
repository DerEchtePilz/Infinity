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

package io.github.derechtepilz.infinity.gamemode.modification

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.getGamemode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AdvancementDisableHandler : Listener {

	@EventHandler
	fun onGainCriterion(event: PlayerAdvancementCriterionGrantEvent) {
		val player = event.player
		if (player.getGamemode() == Gamemode.INFINITY) {
			event.isCancelled = true
		}
	}

}