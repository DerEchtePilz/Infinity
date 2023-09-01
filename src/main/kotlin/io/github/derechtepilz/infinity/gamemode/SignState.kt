package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.util.Keys
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

		private val homeTeleportLocation = Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get())!!, 6.0, 101.0, 9.0)

		override fun loadFor(player: Player, delay: Boolean) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity.INSTANCE, {
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
				SKY -> side.line(2, Component.text().content("Sky").color(NamedTextColor.AQUA).build())
				STONE -> side.line(2, Component.text().content("Stone").color(NamedTextColor.DARK_GRAY).build())
				NETHER -> side.line(2, Component.text().content("Nether").color(NamedTextColor.DARK_RED).build())
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

	enum class ClassSelectionState(val color: NamedTextColor, val value: String) : State<ClassSelectionState> {
		NO_CLASS_SELECTED(NamedTextColor.YELLOW, "01"),
		CLASS_1(NamedTextColor.AQUA, "02"),
		CLASS_2(NamedTextColor.DARK_GRAY, "03"),
		CLASS_3(NamedTextColor.DARK_RED, "04"),
		CLASS_SELECTED(NamedTextColor.BLACK, "05");

		private val classSelectionLocation = Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get())!!, -6.0, 101.0, 9.0)

		override fun loadFor(player: Player, delay: Boolean) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity.INSTANCE, {
				loadFor(player)
			}, if (delay) 10 else 0)
		}

		override fun getNext(): ClassSelectionState {
			return when (this) {
				NO_CLASS_SELECTED -> CLASS_1
				CLASS_1 -> CLASS_2
				CLASS_2 -> CLASS_3
				CLASS_3 -> CLASS_1
				CLASS_SELECTED -> CLASS_SELECTED
			}
		}

		override fun asString(): TextComponent {
			return Component.text().content(this.normalize()).color(this.color).build()
		}

		private fun loadFor(player: Player) {
			val signState = Material.CHERRY_WALL_SIGN.createBlockData().createBlockState() as Sign
			val side = signState.getSide(Side.FRONT)
			when (this) {
				NO_CLASS_SELECTED, CLASS_1, CLASS_2, CLASS_3 -> {
					side.line(0, Component.text().content("Select class:").decoration(TextDecoration.UNDERLINED, true).build())
					side.line(1, Component.empty())
					side.line(2, Component.text().content(this.normalize()).color(this.color).build())
				}
				CLASS_SELECTED -> {
					side.line(0, Component.empty())
					side.line(1, Component.text().content("Class selected:").color(NamedTextColor.GOLD).build())
					side.line(2, Component.text().content(player.getClass()).color(NamedTextColor.GREEN).build())
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
					CLASS_1.value -> CLASS_1
					CLASS_2.value -> CLASS_2
					CLASS_3.value -> CLASS_3
					else -> CLASS_SELECTED
				}
			}
		}

	}

	enum class ClassSwitchingState(val color: NamedTextColor, val value: String) : State<ClassSwitchingState> {
		NO_CLASS_SELECTED(NamedTextColor.YELLOW, "01"),
		CLASS_1(NamedTextColor.AQUA, "02"),
		CLASS_2(NamedTextColor.DARK_GRAY, "03"),
		CLASS_3(NamedTextColor.DARK_RED, "04");

		private val classSwitchingLocation = Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get())!!, -8.0, 101.0, 9.0)

		override fun loadFor(player: Player, delay: Boolean) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity.INSTANCE, {
				loadFor(player)
			}, if (delay) 10 else 0)
		}

		override fun getNext(): ClassSwitchingState {
			return when (this) {
				NO_CLASS_SELECTED -> CLASS_1
				CLASS_1 -> CLASS_2
				CLASS_2 -> CLASS_3
				CLASS_3 -> CLASS_1
			}
		}

		override fun asString(): TextComponent {
			return Component.text().content(this.normalize()).color(this.color).build()
		}

		private fun loadFor(player: Player) {
			val signState = Material.CHERRY_WALL_SIGN.createBlockData().createBlockState() as Sign
			val side = signState.getSide(Side.FRONT)
			when (this) {
				NO_CLASS_SELECTED, CLASS_1, CLASS_2, CLASS_3 -> {
					side.line(0, Component.text().content("Switch class:").decoration(TextDecoration.UNDERLINED, true).build())
					side.line(1, Component.empty())
					side.line(2, Component.text().content(this.normalize()).color(this.color).build())
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
					CLASS_1.value -> CLASS_1
					CLASS_2.value -> CLASS_2
					else -> CLASS_3
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