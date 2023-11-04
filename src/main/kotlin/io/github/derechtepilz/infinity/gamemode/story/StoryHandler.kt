package io.github.derechtepilz.infinity.gamemode.story

import io.github.derechtepilz.infinity.gamemode.story.introduction.IntroductionSequence
import org.bukkit.entity.Player
import java.util.UUID

object StoryHandler {

	val introductions: MutableMap<UUID, IntroductionSequence> = mutableMapOf()

	@JvmStatic
	fun startIntroduction(player: Player) {
		val introduction = IntroductionSequence(player)
		introductions[player.uniqueId] = introduction
	}

}