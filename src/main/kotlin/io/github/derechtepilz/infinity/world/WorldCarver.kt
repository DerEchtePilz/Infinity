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

package io.github.derechtepilz.infinity.world

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener
import io.github.derechtepilz.infinity.gamemode.gameclass.SignState
import io.github.derechtepilz.infinity.structure.StructureLoader
import io.github.derechtepilz.infinity.util.Keys
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockState
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class WorldCarver {

	@Suppress("ReplaceManualRangeWithIndicesCalls")
	class LobbyCarver(val world: World) {

		init {
			if (world.key != Keys.WORLD_LOBBY.get()) {
				throw IllegalArgumentException("World 'infinity:lobby' expected but received '${world.key}'")
			}
			// Load structure if there's no block at 0 100 0
			val canLoadStructure = world.getBlockAt(0, 100, 0).type == Material.AIR
			if (canLoadStructure) {
				StructureLoader(world.key.key, Infinity.INSTANCE.getResource("lobby/lobby_spawn.json")!!)
			}

			/*
			 *  8 101 9 -> Travel to Minecraft
			 *  6 101 9 -> Travel to Home dimension
			 * -5 101 9 -> Class selection info
			 * -6 101 9 -> Select first class
			 * -7 101 9 -> Switch class warning
			 * -8 101 9 -> Switch class
			 */
			// Place signs
			val travelToMinecraft = world.getBlockAt(8, 101, 9)
			travelToMinecraft.type = Material.CHERRY_WALL_SIGN
			val travelToMinecraftState = travelToMinecraft.state as Sign
			travelToMinecraftState.persistentDataContainer.set(Keys.SIGN_TAG_MINECRAFT_TELEPORT.get(), PersistentDataType.STRING, "minecraftTeleport")
			travelToMinecraftState.update()

			val travelToHomeDimension = world.getBlockAt(6, 101, 9)
			travelToHomeDimension.type = Material.CHERRY_WALL_SIGN
			val travelToHomeDimensionState = travelToHomeDimension.state as Sign
			travelToHomeDimensionState.persistentDataContainer.set(Keys.SIGN_TAG_HOME_DIMENSION_TELEPORT.get(), PersistentDataType.STRING, "homeDimensionTeleport")
			travelToHomeDimensionState.update()

			val classSelectionInfo = world.getBlockAt(-5, 101, 9)
			classSelectionInfo.type = Material.CHERRY_WALL_SIGN

			val selectFirstClass = world.getBlockAt(-6, 101, 9)
			selectFirstClass.type = Material.CHERRY_WALL_SIGN
			val selectFirstClassState = selectFirstClass.state as Sign
			selectFirstClassState.persistentDataContainer.set(Keys.SIGN_TAG_SELECT_CLASS.get(), PersistentDataType.STRING, "selectClass")
			selectFirstClassState.update()

			val switchClassWarning = world.getBlockAt(-7, 101, 9)
			switchClassWarning.type = Material.CHERRY_WALL_SIGN

			val switchClass = world.getBlockAt(-8, 101, 9)
			switchClass.type = Material.CHERRY_WALL_SIGN
			val switchClassState = switchClass.state as Sign
			switchClassState.persistentDataContainer.set(Keys.SIGN_TAG_SWITCH_CLASS.get(), PersistentDataType.STRING, "switchClass")
			switchClassState.update()

			applyText(travelToMinecraft.state, arrayOf(
				Component.empty(),
				Component.text("Travel to:"),
				Component.text("Minecraft").color(NamedTextColor.GREEN),
				Component.empty()
			)
			)

			applyText(travelToHomeDimension.state, arrayOf(
				Component.empty(),
				Component.text().content("Travel to:").build(),
				Component.text().content("[Home]").build(),
				Component.empty()
			)
			)

			applyText(classSelectionInfo.state, arrayOf(
				Component.text().content("Class selection:").decorate(TextDecoration.UNDERLINED).build(),
				Component.text().content("Select:")
					.append(Component.text().content(" "))
					.append(Component.text().content("left-click").color(NamedTextColor.GREEN))
					.build(),
				Component.text().content("Cycle:")
					.append(Component.text().content(" "))
					.append(Component.text().content("right-click").color(NamedTextColor.GREEN))
					.build(),
				Component.empty()
			)
			)

			applyText(selectFirstClass.state, arrayOf(
				Component.text().content("Select class:").decorate(TextDecoration.UNDERLINED).build(),
				Component.empty(),
				Component.text().content("[Select class...]").color(NamedTextColor.GREEN).build(),
				Component.empty()
			)
			)

			applyText(switchClassWarning.state, arrayOf(
				Component.text().content("!! Warning !!").color(NamedTextColor.DARK_RED).decorate(TextDecoration.UNDERLINED).build(),
				Component.text().content("Switching class").build(),
				Component.text().content("will reset your").build(),
				Component.text().content("profile!!").build()
			)
			)

			applyText(switchClass.state, arrayOf(
				Component.text().content("Switch class:").decorate(TextDecoration.UNDERLINED).build(),
				Component.empty(),
				Component.text().content("[Select class...]").color(NamedTextColor.GREEN).build(),
				Component.empty()
			)
			)
		}

		companion object {

			fun setupPlayerSignsWithDelay(player: Player) {
				SignListener.INSTANCE.homeDimension[player.uniqueId]!!.loadFor(player, true)
				SignListener.INSTANCE.classSelection[player.uniqueId]!!.loadFor(player, true)
				SignListener.INSTANCE.switchClassSelection[player.uniqueId]!!.loadFor(player, true)
			}

		}

		private fun applyText(sign: BlockState, content: Array<Component>) {
			val signState = sign as Sign
			for (i in 0 until content.size) {
				signState.getSide(Side.FRONT).line(i, content[i])
			}
			signState.update()
		}

	}

	class SkyCarver(val world: World) {
		init {
			if (world.key != Keys.WORLD_SKY.get()) {
				throw IllegalArgumentException("World 'infinity:sky' expected but received '${world.key}'")
			}
			// Load structure if there's no block at 0 100 0
			val canLoadStructure = world.getBlockAt(0, 100, 0).type == Material.AIR
			if (canLoadStructure) {
				StructureLoader(world.key.key, Infinity.INSTANCE.getResource("sky/sky_spawn.json")!!)
			}
		}
	}

	class StoneCarver(val world: World) {
		init {
			if (world.key != Keys.WORLD_STONE.get()) {
				throw IllegalArgumentException("World 'infinity:stone' expected but received '${world.key}'")
			}
			// Load structure if there's a block at 0 101 0
			val canLoadStructure = world.getBlockAt(0, 101, 0).type != Material.AIR
			if (canLoadStructure) {
				StructureLoader(world.key.key, Infinity.INSTANCE.getResource("stone/stone_spawn.json")!!)
			}
		}
	}

	class NetherCarver(val world: World) {
		init {
			if (world.key != Keys.WORLD_NETHER.get()) {
				throw IllegalArgumentException("World 'infinity:nether' expected but received '${world.key}'")
			}
			// Load structure if there's lava at 0 100 0
			val canLoadStructure = world.getBlockAt(0, 100, 0).type == Material.LAVA
			if (canLoadStructure) {
				StructureLoader(world.key.key, Infinity.INSTANCE.getResource("nether/nether_spawn.json")!!)
			}
		}
	}


}