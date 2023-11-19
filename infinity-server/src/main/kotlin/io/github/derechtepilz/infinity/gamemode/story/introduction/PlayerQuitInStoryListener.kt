package io.github.derechtepilz.infinity.gamemode.story.introduction

import io.github.derechtepilz.infinity.gamemode.story.StoryHandler
import io.github.derechtepilz.infinity.gamemode.switching.switchGamemode
import io.github.derechtepilz.infinity.util.Keys
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType

class PlayerQuitInStoryListener : Listener {

	init {
		INSTANCE = this
	}

	companion object {
		lateinit var INSTANCE: PlayerQuitInStoryListener
	}

	@EventHandler
	fun onQuit(event: PlayerQuitEvent) {
		val player = event.player
		resetIntroduction(player)
	}

	fun resetIntroduction(player: Player) {
		if (player.persistentDataContainer.has(Keys.INTRODUCTION_SEQUENCE.get(), PersistentDataType.BOOLEAN)) {
			player.persistentDataContainer.remove(Keys.STORY_STARTED.get())
			player.persistentDataContainer.remove(Keys.INTRODUCTION_SEQUENCE.get())
			player.persistentDataContainer.remove(Keys.GAMEMODE_SWITCH_ENABLED.get())

			player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN)

			StoryHandler.introductions[player.uniqueId] ?: return
			StoryHandler.introductions[player.uniqueId]!!.resetIntroduction()
		}
	}

}