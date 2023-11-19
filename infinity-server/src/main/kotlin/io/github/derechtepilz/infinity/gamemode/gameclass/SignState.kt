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

package io.github.derechtepilz.infinity.gamemode.gameclass

import io.github.derechtepilz.infinity.Infinity0
import io.github.derechtepilz.infinity.gamemode.getClass
import io.github.derechtepilz.infinity.gamemode.normalize
import io.github.derechtepilz.infinity.util.Keys0
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.entity.Player

class SignState {

	enum class HomeDimensionState(val value: String) : State<HomeDimensionState> {
		UNSET("01"),
		SKY("02"),
		STONE("03"),
		NETHER("04");

		private val homeTeleportLocation = Location(Bukkit.getWorld(Keys0.WORLD_LOBBY.get())!!, 6.0, 101.0, 9.0)

		override fun loadFor(player: Player, delay: Boolean) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity0.INSTANCE, {
				loadFor(player)
			}, if (delay) 10 else 0)
		}

		override fun getNext(): HomeDimensionState {
			throw UnsupportedOperationException("HomeDimensionState does not support cycling through dimensions!")
		}

		override fun asString(): TextComponent {
			return Component.text().content(this.normalize()).build()
		}

		private fun loadFor(player: Player) {
			val signState = Material.CHERRY_WALL_SIGN.createBlockData().createBlockState() as Sign
			val side = signState.getSide(Side.FRONT)
			side.line(0, Component.empty())
			side.line(1, Component.text("Travel to:"))
			when (this) {
				UNSET -> side.line(2, Component.text().content("Home unknown").color(NamedTextColor.DARK_RED).build())
				SKY -> side.line(2, GameClass.Dimension.valueOf(this.name).get())
				STONE -> side.line(2, GameClass.Dimension.valueOf(this.name).get())
				NETHER -> side.line(2, GameClass.Dimension.valueOf(this.name).get())
			}
			side.line(3, Component.empty())
			player.sendBlockUpdate(homeTeleportLocation, signState)
		}

		companion object {
			fun loadState(player: Player, state: HomeDimensionState) {
				state.loadFor(player, true)
			}

			fun getByValue(value: String): HomeDimensionState {
				return when (value) {
					UNSET.value -> UNSET
					SKY.value -> SKY
					STONE.value -> STONE
					else -> NETHER
				}
			}
		}

	}

	enum class ClassSelectionState(val value: String) : State<ClassSelectionState> {
		NO_CLASS_SELECTED("01"),
		AIRBORN("02"),
		STONEBORN("03"),
		LAVABORN("04"),
		CLASS_SELECTED("05");

		private val classSelectionLocation = Location(Bukkit.getWorld(Keys0.WORLD_LOBBY.get())!!, -6.0, 101.0, 9.0)

		override fun loadFor(player: Player, delay: Boolean) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity0.INSTANCE, {
				loadFor(player)
			}, if (delay) 10 else 0)
		}

		override fun getNext(): ClassSelectionState {
			return when (this) {
				NO_CLASS_SELECTED -> AIRBORN
				AIRBORN -> STONEBORN
				STONEBORN -> LAVABORN
				LAVABORN -> AIRBORN
				CLASS_SELECTED -> CLASS_SELECTED
			}
		}

		override fun asString(): TextComponent {
			return GameClass.valueOf(this.name).get()
		}

		private fun loadFor(player: Player) {
			val signState = Material.CHERRY_WALL_SIGN.createBlockData().createBlockState() as Sign
			val side = signState.getSide(Side.FRONT)
			when (this) {
				NO_CLASS_SELECTED, AIRBORN, STONEBORN, LAVABORN -> {
					side.line(0, Component.text().content("Select class:").decoration(TextDecoration.UNDERLINED, true).build())
					side.line(1, Component.empty())
					side.line(2, GameClass.valueOf(this.name).get())
				}
				CLASS_SELECTED -> {
					side.line(0, Component.empty())
					side.line(1, Component.text().content("Class selected:").color(NamedTextColor.GOLD).build())
					side.line(2, player.getClass())
				}
			}
			side.line(3, Component.empty())
			player.sendBlockUpdate(classSelectionLocation, signState)
		}

		companion object {
			fun loadState(player: Player, state: HomeDimensionState) {
				state.loadFor(player, true)
			}

			fun getByValue(value: String): ClassSelectionState {
				return when (value) {
					NO_CLASS_SELECTED.value -> NO_CLASS_SELECTED
					AIRBORN.value -> AIRBORN
					STONEBORN.value -> STONEBORN
					LAVABORN.value -> LAVABORN
					else -> CLASS_SELECTED
				}
			}
		}

	}

	enum class ClassSwitchingState(val value: String) : State<ClassSwitchingState> {
		NO_CLASS_SELECTED("01"),
		AIRBORN("02"),
		STONEBORN("03"),
		LAVABORN("04");

		private val classSwitchingLocation = Location(Bukkit.getWorld(Keys0.WORLD_LOBBY.get())!!, -8.0, 101.0, 9.0)

		override fun loadFor(player: Player, delay: Boolean) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity0.INSTANCE, {
				loadFor(player)
			}, if (delay) 10 else 0)
		}

		override fun getNext(): ClassSwitchingState {
			return when (this) {
				NO_CLASS_SELECTED -> AIRBORN
				AIRBORN -> STONEBORN
				STONEBORN -> LAVABORN
				LAVABORN -> AIRBORN
			}
		}

		override fun asString(): TextComponent {
			return GameClass.valueOf(this.name).get()
		}

		private fun loadFor(player: Player) {
			val signState = Material.CHERRY_WALL_SIGN.createBlockData().createBlockState() as Sign
			val side = signState.getSide(Side.FRONT)
			when (this) {
				NO_CLASS_SELECTED, AIRBORN, STONEBORN, LAVABORN -> {
					side.line(0, Component.text().content("Switch class:").decoration(TextDecoration.UNDERLINED, true).build())
					side.line(1, Component.empty())
					side.line(2, GameClass.valueOf(this.name).get())
					side.line(3, Component.empty())
				}
			}
			player.sendBlockUpdate(classSwitchingLocation, signState)
		}

		companion object {
			fun loadState(player: Player, state: ClassSwitchingState) {
				state.loadFor(player, true)
			}

			fun getByValue(value: String): ClassSwitchingState {
				return when (value) {
					NO_CLASS_SELECTED.value -> NO_CLASS_SELECTED
					AIRBORN.value -> AIRBORN
					STONEBORN.value -> STONEBORN
					else -> LAVABORN
				}
			}
		}

	}

	interface State<T> {

		fun loadFor(player: Player, delay: Boolean = false)

		fun getNext(): T

		fun asString(): Component

	}

}