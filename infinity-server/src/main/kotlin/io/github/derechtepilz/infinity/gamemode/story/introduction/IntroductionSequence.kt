package io.github.derechtepilz.infinity.gamemode.story.introduction

import io.github.derechtepilz.infinity.Infinity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.scheduler.BukkitTask
import java.util.Vector

class IntroductionSequence(private val player: Player) {

	private val guidePrefix: TextComponent
	private val mythicalCreature: TextComponent

	private val queuedMessagesList: MutableList<Component> = mutableListOf()
	private val queuedMessages: MutableMap<Component, Int> = mutableMapOf()
	private val sentMessages: Vector<Component> = Vector()

	private val storyIntroductionGuide: Villager

	init {
		// Hide all other players
		for (target in Bukkit.getOnlinePlayers()) {
			player.hidePlayer(Infinity.INSTANCE, target)
		}

		// Spawn the story guide for the player
		storyIntroductionGuide = player.world.spawnEntity(Location(player.world, 0.5, 101.0, 2.5), EntityType.VILLAGER, CreatureSpawnEvent.SpawnReason.CUSTOM) as Villager
		storyIntroductionGuide.customName(Component.text().content("Guide").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).build())
		storyIntroductionGuide.isVisibleByDefault = false
		storyIntroductionGuide.isInvulnerable = true
		storyIntroductionGuide.isPersistent = true
		storyIntroductionGuide.isCustomNameVisible = true
		storyIntroductionGuide.setRotation(-180.0f, 0.0f)
		storyIntroductionGuide.setAI(false)
		player.showEntity(Infinity.INSTANCE, storyIntroductionGuide)

		guidePrefix = Component.text().content("[")
			.color(NamedTextColor.WHITE)
			.append(Component.text().content("Guide").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
			.append(Component.text().content("]: ")).build()

		mythicalCreature = Component.text().content("X").color(NamedTextColor.GOLD).decorate(TextDecoration.OBFUSCATED)
			.appendSpace()
			.append(Component.text().content("Mythical Creature").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE))
			.appendSpace()
			.append(Component.text().content("X").color(NamedTextColor.GOLD).decorate(TextDecoration.OBFUSCATED))
			.appendSpace().build()

		startDialogue()
	}

	fun resetIntroduction() {
		storyIntroductionGuide.remove()
	}

	private fun startDialogue() {
		queueGuideMessage(Component.text().content("Oh, hello ...?").build(), 0)
		queueGuideMessage(Component.text().content("Ah, got it!")
			.appendSpace()
			.append(Component.text().content(player.name).color(NamedTextColor.YELLOW))
			.append(Component.text().content("!"))
			.build(), 2
		)
		queueGuideMessage(Component.text().content("Welcome to ").append(Infinity.INSTANCE.infinityComponent).append(Component.text().content("!")).build(), 2)
		queueGuideMessage(Component.text().content("I am going to tell you a story about what happened here. It was a really long time ago...").build(), 2)
		queueGuideMessage(Component.text().content("Thousands of years ago, there were three worlds, full of life and potential. ").build(), 5)
		queueGuideMessage(Component.text().content("People could travel these worlds with ease and learn from each other.").build(), 5)
		queueGuideMessage(Component.text().content("These worlds were guarded by a ")
			.append(mythicalCreature)
			.append(Component.text().content("!")).build(), 5
		)
		queueGuideMessage(Component.text().content("This ")
			.append(mythicalCreature)
			.append(Component.text().content(" watched over these people and kept the worlds alive and connected.")).build(), 5
		)
		queueGuideMessage(Component.text().content("The connections were an important piece in the grand scheme as they allowed travel between the worlds.").build(), 7)
		queueGuideMessage(Component.text().content("They allowed the exchange and potential that was present.").build(), 7)
		queueGuideMessage(Component.text().content("They allowed the people to evolve equally and prevented wars as all people were equally smart and strong").build(), 5)
		queueGuideMessage(Component.text().content("But some day, and we do not know the reason, but at some point, the ")
			.append(mythicalCreature)
			.append(Component.text().content(" got really angry and destroyed the connections between the worlds.")).build(), 7
		)
		queueGuideMessage(Component.text().content("Because of that, the worlds were disconnected and exchange didn't happen anymore.").build(), 8)
		queueGuideMessage(Component.text().content("Over the years that have passed since then, each of the three worlds have developed their own civilizations."), 5)
		queueGuideMessage(Component.text().content("The civilizations are known as classes since a few hundred years ago. There were a few years where a temporary connection has been established, hence we know about the classes."), 6)
		queueGuideMessage(Component.text().content("You, ${player.name}, have been chosen to gain the chance to be born into one of these classes."), 11)
		queueGuideMessage(Component.text().content("Each class has their own name and their own abilities. You have the ability to travel between the worlds, that is if you are able to gain enough knowledge about your world and its features."), 6)
		queueGuideMessage(Component.text().content("You can always travel back here. This is kind of the core of the worlds. This is where you will travel to other worlds in the future."), 11)
		queueGuideMessage(Component.text().content("Do you understand this so far?"), 8)
		queueGuideMessage(Component.text().content("Anyway (:D), let me introduce you to those classes..."), 10)

		// Class names with different abilities (three each)

		player.sendGuideMessages()
	}

	private fun queueGuideMessage(message: ComponentLike, delayInSeconds: Int) {
		queueGuideMessage(message.asComponent(), delayInSeconds)
	}

	private fun queueGuideMessage(message: Component, delayInSeconds: Int) {
		val guideMessage = guidePrefix.append(message)
		queuedMessages[guideMessage] = delayInSeconds * 20
		queuedMessagesList.add(guideMessage)
	}

	private fun Player.sendGuideMessages() {
		Bukkit.getScheduler().runTaskAsynchronously(Infinity.INSTANCE, Runnable {
			var currentIndex = 0
			while (currentIndex < queuedMessagesList.size) {
				if (currentIndex == sentMessages.size) {
					val message = queuedMessagesList[currentIndex]
					Bukkit.getScheduler().runTaskLater(Infinity.INSTANCE, Runnable {
						this.sendMessage(message)
						sentMessages.add(message)
					}, queuedMessages[message]!!.toLong())
					currentIndex++
					continue
				}
			}
		})
	}

}