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
