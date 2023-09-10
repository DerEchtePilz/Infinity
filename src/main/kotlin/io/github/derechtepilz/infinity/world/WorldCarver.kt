package io.github.derechtepilz.infinity.world

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.SignListener
import io.github.derechtepilz.infinity.gamemode.SignState
import io.github.derechtepilz.infinity.structure.StructureLoader
import io.github.derechtepilz.infinity.util.Keys
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
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
				SignState.HomeDimensionState.loadState(player, SignState.HomeDimensionState.UNSET)
				for (uuid in SignListener.INSTANCE.classSelection.keys) {
					SignListener.INSTANCE.classSelection[uuid]!!.loadFor(Bukkit.getPlayer(uuid)!!, true)
				}
				for (uuid in SignListener.INSTANCE.switchClassSelection.keys) {
					SignListener.INSTANCE.switchClassSelection[uuid]!!.loadFor(Bukkit.getPlayer(uuid)!!, true)
				}
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

}