package io.github.derechtepilz.infinity.gamemode.story.introduction;

import io.github.derechtepilz.infinity.gamemode.story.StoryHandler;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerQuitInStoryListener implements Listener {

	public static PlayerQuitInStoryListener INSTANCE;

	public PlayerQuitInStoryListener() {
		INSTANCE = this;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		resetIntroduction(player);
	}

	public void resetIntroduction(Player player) {
		if (player.getPersistentDataContainer().has(Keys.INTRODUCTION_SEQUENCE.get(), PersistentDataType.BOOLEAN)) {
			player.getPersistentDataContainer().remove(Keys.STORY_STARTED.get());
			player.getPersistentDataContainer().remove(Keys.INTRODUCTION_SEQUENCE.get());
			player.getPersistentDataContainer().remove(Keys.GAMEMODE_SWITCH_ENABLED.get());

			PlayerUtil.switchGamemode(player, PlayerTeleportEvent.TeleportCause.PLUGIN);

			if (StoryHandler.INTRODUCTIONS.get(player.getUniqueId()) == null) return;
			StoryHandler.INTRODUCTIONS.get(player.getUniqueId()).resetIntroduction();
		}
	}

}
