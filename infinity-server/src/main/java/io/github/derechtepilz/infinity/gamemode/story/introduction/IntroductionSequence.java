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

import io.github.derechtepilz.infinity.Infinity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;

public class IntroductionSequence {

	private final TextComponent guidePrefix = Component.text().content("[")
		.color(NamedTextColor.WHITE)
		.append(Component.text().content("Guide").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
		.append(Component.text().content("]: ")).build();

	private final TextComponent mythicalCreature = Component.text().content("X").color(NamedTextColor.GOLD).decorate(TextDecoration.OBFUSCATED)
		.appendSpace()
		.append(Component.text().content("Mythical Creature").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE))
		.appendSpace()
		.append(Component.text().content("X").color(NamedTextColor.GOLD).decorate(TextDecoration.OBFUSCATED))
		.appendSpace().build();

	private final List<Component> queuedMessagesList = new ArrayList<>();
	private final Map<Component, Integer> queuedMessages = new HashMap<>();
	private final Vector<Component> sentMessages = new Vector<>();

	private final Player player;
	private final Villager storyIntroductionGuide;

	public IntroductionSequence(Player player) {
		this.player = player;
		storyIntroductionGuide = (Villager) player.getWorld().spawnEntity(new Location(player.getWorld(), 0.5, 101.0, 2.5), EntityType.VILLAGER, CreatureSpawnEvent.SpawnReason.CUSTOM);
		storyIntroductionGuide.customName(Component.text().content("Guide").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).build());
		storyIntroductionGuide.setVisibleByDefault(false);
		storyIntroductionGuide.setInvulnerable(true);
		storyIntroductionGuide.setPersistent(true);
		storyIntroductionGuide.setCustomNameVisible(true);
		storyIntroductionGuide.setRotation(-180.0f, 0.0f);
		storyIntroductionGuide.setAI(false);
		player.showEntity(Infinity.getInstance(), storyIntroductionGuide);

		startDialogue();
	}

	public void resetIntroduction() {
		storyIntroductionGuide.remove();
	}

	private void startDialogue() {
		queueGuideMessage(Component.text().content("Oh, hello ...?").build(), 0);
		queueGuideMessage(Component.text().content("Ah, got it!")
			.appendSpace()
			.append(Component.text().content(player.getName()).color(NamedTextColor.YELLOW))
			.append(Component.text().content("!"))
			.build(), 2
		);
		queueGuideMessage(Component.text().content("Welcome to ").append(Infinity.getInstance().getInfinityComponent()).append(Component.text().content("!")).build(), 2);
		queueGuideMessage(Component.text().content("I am going to tell you a story about what happened here. It was a really long time ago...").build(), 2);
		queueGuideMessage(Component.text().content("Thousands of years ago, there were three worlds, full of life and potential. ").build(), 5);
		queueGuideMessage(Component.text().content("People could travel these worlds with ease and learn from each other.").build(), 5);
		queueGuideMessage(Component.text().content("These worlds were guarded by a ")
			.append(mythicalCreature)
			.append(Component.text().content("!")).build(), 5
		);
		queueGuideMessage(Component.text().content("This ")
			.append(mythicalCreature)
			.append(Component.text().content(" watched over these people and kept the worlds alive and connected.")).build(), 5
		);
		queueGuideMessage(Component.text().content("The connections were an important piece in the grand scheme as they allowed travel between the worlds.").build(), 7);
		queueGuideMessage(Component.text().content("They allowed the exchange and potential that was present.").build(), 7);
		queueGuideMessage(Component.text().content("They allowed the people to evolve equally and prevented wars as all people were equally smart and strong").build(), 5);
		queueGuideMessage(Component.text().content("But some day, and we do not know the reason, but at some point, the ")
			.append(mythicalCreature)
			.append(Component.text().content(" got really angry and destroyed the connections between the worlds.")).build(), 7
		);
		queueGuideMessage(Component.text().content("Because of that, the worlds were disconnected and exchange didn't happen anymore.").build(), 8);
		queueGuideMessage(Component.text().content("Over the years that have passed since then, each of the three worlds have developed their own civilizations."), 5);
		queueGuideMessage(Component.text().content("The civilizations are known as classes since a few hundred years ago. There were a few years where a temporary connection has been established, hence we know about the classes."), 6);
		queueGuideMessage(Component.text().content("You, " + player.getName() + ", have been chosen to gain the chance to be born into one of these classes."), 11);
		queueGuideMessage(Component.text().content("Each class has their own name and their own abilities. You have the ability to travel between the worlds, that is if you are able to gain enough knowledge about your world and its features."), 6);
		queueGuideMessage(Component.text().content("You can always travel back here. This is kind of the core of the worlds. This is where you will travel to other worlds in the future."), 11);
		queueGuideMessage(Component.text().content("Do you understand this so far?"), 8);
		queueGuideMessage(Component.text().content("Anyway (:D), let me introduce you to those classes..."), 10);

		// Class names with different abilities (three each)

		sendGuideMessages();
	}

	private void queueGuideMessage(ComponentLike message, int delayInSeconds) {
		queueGuideMessage(message.asComponent(), delayInSeconds);
	}

	private void queueGuideMessage(Component message, int delayInSeconds) {
		TextComponent guideMessage = guidePrefix.append(message);
		queuedMessages.put(guideMessage, delayInSeconds * 20);
		queuedMessagesList.add(guideMessage);
	}

	private void sendGuideMessages() {
		Bukkit.getScheduler().runTaskAsynchronously(Infinity.getInstance(), () -> {
			int currentIndex = 0;
			while (currentIndex < queuedMessagesList.size()) {
				if (currentIndex == sentMessages.size()) {
					Component message = queuedMessagesList.get(currentIndex);
					Bukkit.getScheduler().runTaskLater(Infinity.getInstance(), () -> {
						player.sendMessage(message);
						sentMessages.add(message);
					}, queuedMessages.get(message));
					currentIndex++;
				}
			}
		});
	}

}
