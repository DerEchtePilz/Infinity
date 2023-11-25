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

package io.github.derechtepilz.infinity.gamemode.story.introduction;

import io.github.derechtepilz.infinity.gamemode.states.GamemodeState;
import io.github.derechtepilz.infinity.gamemode.story.StoryHandler;
import io.github.derechtepilz.infinity.util.Keys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerQuitInStoryHandler {

	private PlayerQuitInStoryHandler() {
	}

	public static void resetIntroduction(Player player) {
		if (player.getPersistentDataContainer().has(Keys.INTRODUCTION_SEQUENCE.get(), PersistentDataType.BOOLEAN)) {
			player.getPersistentDataContainer().remove(Keys.STORY_STARTED.get());
			player.getPersistentDataContainer().remove(Keys.INTRODUCTION_SEQUENCE.get());
			player.getPersistentDataContainer().remove(Keys.GAMEMODE_SWITCH_ENABLED.get());

			GamemodeState.MINECRAFT.loadFor(player);

			if (StoryHandler.INTRODUCTIONS.get(player.getUniqueId()) == null) return;
			StoryHandler.INTRODUCTIONS.get(player.getUniqueId()).resetIntroduction();
		}
	}

}
