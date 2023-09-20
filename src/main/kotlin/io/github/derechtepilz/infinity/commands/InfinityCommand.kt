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

package io.github.derechtepilz.infinity.commands

import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.infinity.Registry
import io.github.derechtepilz.infinity.config.ConfigHandler
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.getGamemode
import io.github.derechtepilz.infinity.gamemode.switching.switchGamemode
import io.github.derechtepilz.infinity.items.InfinityItem
import io.github.derechtepilz.infinity.items.Rarity
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.capitalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.TimeUnit

object InfinityCommand {

	fun register() {
		commandTree("infinity") {
			literalArgument("give") {
				withRequirement { sender: CommandSender -> sender.isOp }
				literalArgument("*") {
					playerExecutor { player, _ ->
						player.inventory.addItem(*Registry.Item.getAllItems())
					}
				}
				multiLiteralArgument(nodeName = "itemId", *Registry.Item.getItemIds()) {
					integerArgument("variation", 0, 100, true) {
						playerExecutor { player, args ->
							val itemId = args.getUnchecked<String>("itemId")!!
							val variationId = args.getOrDefaultUnchecked("variation", -1)!!
							val item = Registry.Item.getItem(itemId, variationId)
							if (item == null) {
								val backup = Registry.Item.getItem(itemId, 0)!!
								player.sendMessage(Component.text("The ")
									.color(NamedTextColor.RED)
									.append(backup.displayName().color(NamedTextColor.RED).hoverEvent(Registry.Item.getItem(itemId, 0)!!))
									.append(Component.text(" does not have a variation id $variationId"))
								)
								return@playerExecutor
							}
							player.inventory.addItem(item)
						}
					}
				}
			}
			literalArgument("changerarity") {
				withRequirement { sender: CommandSender -> sender.isOp }
				multiLiteralArgument(nodeName = "rarity", *Rarity.values().map { rarity -> rarity.name.lowercase() }.toTypedArray()) {
					playerExecutor { player, args ->
						val rarity = Rarity.valueOf((args["rarity"] as String).uppercase())
						val heldItem = player.inventory.itemInMainHand
						val selectedSlot = player.inventory.heldItemSlot
						if (!isInfinityItem(heldItem)) {
							player.sendMessage(Component.text("Cannot change rarity for item ")
								.color(NamedTextColor.RED)
								.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
								.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
							)
							return@playerExecutor
						}
						val itemId = heldItem.itemMeta.persistentDataContainer[InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING]!!
						val variation = heldItem.itemMeta.persistentDataContainer[InfinityItem.VARIATION_ID, PersistentDataType.INTEGER]!!
						val item = Registry.Item.getItem(itemId, variation)
						player.inventory.setItem(selectedSlot, item!!.updateRarityTo(rarity))
					}
				}
			}
			literalArgument("upgrade") {
				withRequirement { sender: CommandSender -> sender.isOp }
				playerExecutor { player, _ ->
					// Check if there's an InfinityItem in the main hand
					val heldItem = player.inventory.itemInMainHand
					val selectedSlot = player.inventory.heldItemSlot
					if (!isInfinityItem(heldItem)) {
						player.sendMessage(Component.text("Cannot upgrade rarity for item ")
							.color(NamedTextColor.RED)
							.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
							.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
						)
						return@playerExecutor
					}
					// Upgrade the item
					val itemId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)!!
					val currentRarity = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.RARITY_KEY, PersistentDataType.STRING)!!
					val variationId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER)!!

					val basicItem = Registry.Item.getItem(itemId, variationId)!!
					val upgradedItem = basicItem.upgradeItem(Rarity.valueOf(currentRarity.uppercase()))
					player.inventory.setItem(selectedSlot, upgradedItem)
					player.sendMessage(Component.text("Your ")
						.color(NamedTextColor.GREEN)
						.append(heldItem.displayName().hoverEvent(heldItem))
						.append(Component.text(" has been upgraded to an ").color(NamedTextColor.GREEN))
						.append(Component.text("[").color(NamedTextColor.WHITE)
							.hoverEvent(upgradedItem)
							.append(upgradedItem.displayName())
							.append(Component.text("]").color(NamedTextColor.WHITE))
						)
						.append(Component.text("!").color(NamedTextColor.GREEN))
					)
				}
			}
			literalArgument("downgrade") {
				withRequirement { sender: CommandSender -> sender.isOp }
				playerExecutor { player, _ ->
					// Check if there's an InfinityItem in the main hand
					val heldItem = player.inventory.itemInMainHand
					val selectedSlot = player.inventory.heldItemSlot
					if (!isInfinityItem(heldItem)) {
						player.sendMessage(Component.text("Cannot downgrade rarity for item ")
							.color(NamedTextColor.RED)
							.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
							.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
						)
						return@playerExecutor
					}
					// Downgrade the item
					val itemId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)!!
					val currentRarity = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.RARITY_KEY, PersistentDataType.STRING)!!
					val variationId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER)!!

					val basicItem = Registry.Item.getItem(itemId, variationId)!!
					val downgradedItem = basicItem.downgradeItem(Rarity.valueOf(currentRarity.uppercase()))
					player.inventory.setItem(selectedSlot, downgradedItem)
					player.sendMessage(Component.text("Your ")
						.color(NamedTextColor.RED)
						.append(heldItem.displayName().hoverEvent(heldItem))
						.append(Component.text(" has been downgraded to an ").color(NamedTextColor.RED))
						.append(Component.text("[").color(NamedTextColor.WHITE)
							.hoverEvent(downgradedItem)
							.append(downgradedItem.displayName())
							.append(Component.text("]").color(NamedTextColor.WHITE))
						)
						.append(Component.text("!").color(NamedTextColor.RED))
					)
				}
			}
			literalArgument("teleport") {
				withRequirement { sender: CommandSender -> sender.isOp }
				worldArgument("world") {
					playerExecutor { player, args ->
						val targetWorld = args["world"] as World
						val currentGamemode = player.getGamemode()
						val targetGamemode = Gamemode.getFromKey(targetWorld.key)
						if (currentGamemode == targetGamemode) {
							player.teleport(Location(targetWorld, player.location.x, player.location.y, player.location.z))
						} else {
							player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN, targetWorld, Location(targetWorld, player.location.x, player.location.y, player.location.z))
						}
					}
					locationArgument("location") {
						playerExecutor { player, args ->
							val targetWorld = args["world"] as World
							val targetLocation = args["location"] as Location
							val currentGamemode = Gamemode.getFromKey(player.world.key)
							val targetGamemode = Gamemode.getFromKey(targetWorld.key)
							if (currentGamemode == targetGamemode) {
								player.teleport(Location(targetWorld, targetLocation.x, targetLocation.y, targetLocation.z))
							} else {
								player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN, targetWorld, targetLocation)
							}
						}
					}
				}
			}
			literalArgument("gamemode") {
				multiLiteralArgument(nodeName = "gamemode", "infinity", "minecraft") {
					playerExecutor { player, args ->
						when (args["gamemode"] as String) {
							"infinity" -> {
								if (player.getGamemode() != Gamemode.INFINITY) {
									player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN)
								} else {
									player.sendMessage(Component.text("You cannot execute this command right now as you are already playing ")
										.color(NamedTextColor.RED)
										.append(MiniMessage.miniMessage().deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>"))
									)
								}
							}
							"minecraft" -> {
								if (player.getGamemode() != Gamemode.MINECRAFT) {
									player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN)
								} else {
									player.sendMessage(Component.text("You cannot execute this command right now as you are already playing ")
										.color(NamedTextColor.RED)
										.append(Component.text("Minecraft").color(NamedTextColor.GREEN))
									)
								}
							}
						}
					}
				}
			}
			literalArgument("defaultgamemode") {
				multiLiteralArgument(nodeName = "gamemode", "infinity", "minecraft") {
					playerExecutor { player, args ->
						val chosenGamemode = args["gamemode"] as String
						when (chosenGamemode) {
							"infinity" -> player.persistentDataContainer.set(Keys.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING, chosenGamemode)
							"minecraft" -> player.persistentDataContainer.set(Keys.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING, chosenGamemode)
						}
						player.sendMessage(Component.text("Set default gamemode to ")
							.color(NamedTextColor.GRAY)
							.append(Component.text(chosenGamemode.capitalize())
								.color(when (chosenGamemode) {
									"infinity" -> NamedTextColor.LIGHT_PURPLE
									"minecraft" -> NamedTextColor.GREEN
									else -> NamedTextColor.GRAY
								}
								)
							)
							.append(Component.text("!").color(NamedTextColor.GRAY))
						)
					}
				}
				literalArgument("reset") {
					playerExecutor { player, _ ->
						player.persistentDataContainer.remove(Keys.DEFAULT_GAMEMODE.get())
						player.sendMessage(Component.text("Reset default gamemode!").color(NamedTextColor.RED))
					}
				}
			}
			literalArgument("config") {
				withRequirement { sender: CommandSender -> sender.isOp }
				literalArgument("edit") {
					literalArgument("player-data-backup") {
						literalArgument("interval") {
							integerArgument("interval") {
								playerExecutor { player, args ->
									val intervalInput = args["interval"] as Int
									val backupKey = ConfigHandler.getBackupInterval()
									backupKey.interval = intervalInput

									val currentTimeUnit = backupKey.activeTimeUnit
									player.sendMessage(Component.text().content("Player data will now be backed up every ")
										.color(NamedTextColor.GRAY)
										.append(Component.text().content("$intervalInput $currentTimeUnit").color(NamedTextColor.GOLD))
										.append(Component.text().content("!"))
									)

									ConfigHandler.setBackupInterval(backupKey)
								}
							}
						}
						literalArgument("timeUnit") {
							multiLiteralArgument(nodeName = "timeUnit", "seconds", "minutes", "hours") {
								playerExecutor { player, args ->
									val timeUnitInput = args["timeUnit"] as String
									val timeUnit = TimeUnit.valueOf(timeUnitInput.uppercase())
									val backupKey = ConfigHandler.getBackupInterval()
									backupKey.activeTimeUnit = timeUnit

									val currentInterval = backupKey.interval
									player.sendMessage(Component.text().content("Player data will now be backed up every ")
										.color(NamedTextColor.GRAY)
										.append(Component.text().content("$currentInterval $timeUnitInput").color(NamedTextColor.GOLD))
										.append(Component.text().content("!"))
									)

									ConfigHandler.setBackupInterval(backupKey)
								}
							}
						}
					}
				}
				literalArgument("reload") {
					playerExecutor { player, _ ->
						ConfigHandler.loadConfig()
						player.sendMessage(Component.text().content("The config has been reloaded!").color(NamedTextColor.GRAY).build())
					}
				}
				literalArgument("save") {
					playerExecutor { player, _ ->
						ConfigHandler.saveConfig()
						player.sendMessage(Component.text().content("The config has been saved!").color(NamedTextColor.GRAY).build())
					}
				}
				literalArgument("reset") {
					playerExecutor { player, _ ->
						ConfigHandler.resetConfig()
						player.sendMessage(Component.text().content("The config has been reset!").color(NamedTextColor.GRAY).build())
					}
				}
			}
		}
	}

	private fun isInfinityItem(item: ItemStack): Boolean {
		return item.hasItemMeta() && item.itemMeta.persistentDataContainer.has(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)
	}

}