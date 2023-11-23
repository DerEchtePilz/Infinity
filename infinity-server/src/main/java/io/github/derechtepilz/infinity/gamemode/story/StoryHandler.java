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

package io.github.derechtepilz.infinity.gamemode.story;

import io.github.derechtepilz.infinity.gamemode.story.introduction.IntroductionSequence;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StoryHandler {

	private StoryHandler() {}

	public static final Map<UUID, IntroductionSequence> INTRODUCTIONS = new HashMap<>();

	public static void startIntroduction(Player player) {
		IntroductionSequence introduction = new IntroductionSequence(player);
		INTRODUCTIONS.put(player.getUniqueId(), introduction);
	}

}
