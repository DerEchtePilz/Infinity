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

package io.github.derechtepilz.infinity.gamemode.modification;

import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatHandler implements Listener {

	@EventHandler
	public void onChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		Gamemode playerGamemode = PlayerUtil.getGamemode(player);
		event.viewers().removeIf(audience -> audience instanceof Player p && PlayerUtil.getGamemode(p) != playerGamemode);
	}

}
