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

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import io.github.derechtepilz.infinity.Infinity0
import io.github.derechtepilz.infinity.Registry0
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.getGamemode
import io.github.derechtepilz.infinity.gamemode.story.StoryHandler
import io.github.derechtepilz.infinity.gamemode.switching.switchGamemode
import io.github.derechtepilz.infinity.gamemode.switching.terminateStoryTitleTask
import io.github.derechtepilz.infinity.items.InfinityItem
import io.github.derechtepilz.infinity.items.Rarity
import io.github.derechtepilz.infinity.util.Keys0
import io.github.derechtepilz.infinity.util.Reflection
import io.github.derechtepilz.infinity.util.capitalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.DimensionArgument
import net.minecraft.commands.arguments.coordinates.Coordinates
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.resources.ResourceLocation
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.function.Predicate

object InfinityCommand {

	fun register() {
		val itemIds = Registry0.Item.getItemIds()
		val requirePlayer = Predicate<CommandSourceStack> { stack -> stack.source.getBukkitSender(stack) is Player }
		val requirePlayerOperator = Predicate<CommandSourceStack> { stack -> stack.source.getBukkitSender(stack) is Player && stack.source.getBukkitSender(stack).isOp }

		Reflection.getDedicatedServer().commands.dispatcher.register(LiteralArgumentBuilder.literal<CommandSourceStack>("infinity")
			.then(LiteralArgumentBuilder.literal<CommandSourceStack>("give")
				.requires(requirePlayerOperator)
				.then(LiteralArgumentBuilder.literal<CommandSourceStack>("*")
					.executes { ctx ->
						val sender = getSenderAsPlayer(ctx)
						sender.inventory.addItem(*Registry0.Item.getAllItems())
						return@executes 1
					}
				)
			)
			.then(LiteralArgumentBuilder.literal<CommandSourceStack>("upgrade")
				.requires(requirePlayerOperator)
				.executes { ctx ->
					val player = getSenderAsPlayer(ctx)
					val heldItem = player.inventory.itemInMainHand
					val selectedSlot = player.inventory.heldItemSlot
					if (!isInfinityItem(heldItem)) {
						player.sendMessage(Component.text("Cannot upgrade rarity for item ")
							.color(NamedTextColor.RED)
							.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
							.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
						)
						return@executes 1
					}
					// Upgrade the item
					val itemId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)!!
					val currentRarity = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.RARITY_KEY, PersistentDataType.STRING)!!
					val variationId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER)!!

					val basicItem = Registry0.Item.getItem(itemId, variationId)!!
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
					return@executes 1
				}
			)
			.then(LiteralArgumentBuilder.literal<CommandSourceStack>("downgrade")
				.requires(requirePlayerOperator)
				.executes { ctx ->
					val player = getSenderAsPlayer(ctx);
					// Check if there's an InfinityItem in the main hand
					val heldItem = player.inventory.itemInMainHand
					val selectedSlot = player.inventory.heldItemSlot
					if (!isInfinityItem(heldItem)) {
						player.sendMessage(Component.text("Cannot downgrade rarity for item ")
							.color(NamedTextColor.RED)
							.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
							.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
						)
						return@executes 1
					}
					// Downgrade the item
					val itemId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)!!
					val currentRarity = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.RARITY_KEY, PersistentDataType.STRING)!!
					val variationId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER)!!

					val basicItem = Registry0.Item.getItem(itemId, variationId)!!
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
					return@executes 1
				}
			)
			.then(LiteralArgumentBuilder.literal<CommandSourceStack>("teleport")
				.requires(requirePlayerOperator)
				.then(RequiredArgumentBuilder.argument<CommandSourceStack, ResourceLocation>("world", DimensionArgument.dimension())
					.then(RequiredArgumentBuilder.argument<CommandSourceStack, Coordinates>("location", Vec3Argument.vec3())
						.executes { ctx ->
							val player = getSenderAsPlayer(ctx)
							val targetWorld = DimensionArgument.getDimension(ctx, "world").world
							val vec3 = Vec3Argument.getVec3(ctx, "location")
							val targetLocation = Location(null, vec3.x, vec3.y, vec3.z)
							val currentGamemode = Gamemode.getFromKey(player.world.key)
							val targetGamemode = Gamemode.getFromKey(targetWorld.key)
							if (currentGamemode == targetGamemode) {
								player.teleport(Location(targetWorld, targetLocation.x, targetLocation.y, targetLocation.z))
							} else {
								player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN, targetWorld, targetLocation)
							}
							return@executes 1
						}
					)
					.executes { ctx ->
						val player = getSenderAsPlayer(ctx)
						val targetWorld = DimensionArgument.getDimension(ctx, "world").world
						val currentGamemode = player.getGamemode()
						val targetGamemode = Gamemode.getFromKey(targetWorld.key)
						if (currentGamemode == targetGamemode) {
							player.teleport(Location(targetWorld, player.location.x, player.location.y, player.location.z))
						} else {
							player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN, targetWorld, Location(targetWorld, player.location.x, player.location.y, player.location.z))
						}
						return@executes 1
					}
				)
			)
			.then(LiteralArgumentBuilder.literal<CommandSourceStack>("startstory")
				.requires(requirePlayer.and { stack -> stack.source.getBukkitSender(stack).hasPermission("infinity.startstory") })
				.executes { ctx ->
					val player = getSenderAsPlayer(ctx)
					Infinity0.INSTANCE.playerPermissions.getOrDefault(player.uniqueId, player.addAttachment(Infinity0.INSTANCE)).setPermission("infinity.startstory", false)
					player.updateCommands()
					player.terminateStoryTitleTask()
					player.persistentDataContainer.set(Keys0.STORY_STARTED.get(), PersistentDataType.BOOLEAN, true)
					player.sendMessage(Component.text().content("Story started! Have fun!").color(NamedTextColor.LIGHT_PURPLE).build())

					// Disable gamemode switching for the player executing this command
					player.persistentDataContainer.set(Keys0.GAMEMODE_SWITCH_ENABLED.get(), PersistentDataType.BOOLEAN, false) // Disable gamemode switching during introduction sequence
					player.persistentDataContainer.set(Keys0.INTRODUCTION_SEQUENCE.get(), PersistentDataType.BOOLEAN, true) // Track if the player currently sees the introduction sequence

					// Initiate story start sequence
					StoryHandler.startIntroduction(player)
					return@executes 1
				}
			)
		)

		for (itemId in itemIds) {
			Reflection.getDedicatedServer().commands.dispatcher.register(LiteralArgumentBuilder.literal<CommandSourceStack>("infinity")
				.then(LiteralArgumentBuilder.literal<CommandSourceStack>("give")
					.requires(requirePlayerOperator)
					.then(LiteralArgumentBuilder.literal<CommandSourceStack>(itemId)
						.then(RequiredArgumentBuilder.argument<CommandSourceStack, Int>("variation", IntegerArgumentType.integer(0, 100))
							.executes { ctx ->
								val player = getSenderAsPlayer(ctx)
								val variationId = ctx.getArgument("variation", Int::class.java)
								val item = Registry0.Item.getItem(itemId, variationId)
								if (item == null) {
									val backup = Registry0.Item.getItem(itemId, 0)!!
									player.sendMessage(Component.text("The ")
										.color(NamedTextColor.RED)
										.append(backup.displayName().color(NamedTextColor.RED).hoverEvent(Registry0.Item.getItem(itemId, 0)!!))
										.append(Component.text(" does not have a variation id $variationId"))
									)
									return@executes 1
								}
								player.inventory.addItem(item)
								return@executes 1
							}
						)
						.executes { ctx ->
							val player = getSenderAsPlayer(ctx)
							val item = Registry0.Item.getItem(itemId, -1)!!
							player.inventory.addItem(item)
							return@executes 1
						}
					)
				)
			)
		}

		for (rarity in Rarity.entries.map { rarity -> rarity.name.lowercase() }.toTypedArray()) {
			Reflection.getDedicatedServer().commands.dispatcher.register(LiteralArgumentBuilder.literal<CommandSourceStack>("infinity")
				.then(LiteralArgumentBuilder.literal<CommandSourceStack>("changerarity")
					.requires(requirePlayerOperator)
					.executes { ctx ->
						val player = getSenderAsPlayer(ctx)
						val rarity = Rarity.valueOf(rarity.uppercase())
						val heldItem = player.inventory.itemInMainHand
						val selectedSlot = player.inventory.heldItemSlot
						if (!isInfinityItem(heldItem)) {
							player.sendMessage(Component.text("Cannot change rarity for item ")
								.color(NamedTextColor.RED)
								.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
								.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
							)
							return@executes 1
						}
						val itemId = heldItem.itemMeta.persistentDataContainer[InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING]!!
						val variation = heldItem.itemMeta.persistentDataContainer[InfinityItem.VARIATION_ID, PersistentDataType.INTEGER]!!
						val item = Registry0.Item.getItem(itemId, variation)
						player.inventory.setItem(selectedSlot, item!!.updateRarityTo(rarity))
						return@executes 1
					}
				)
			)
		}

		for (gamemode in arrayOf("infinity", "minecraft")) {
			Reflection.getDedicatedServer().commands.dispatcher.register(LiteralArgumentBuilder.literal<CommandSourceStack>("infinity")
				.then(LiteralArgumentBuilder.literal<CommandSourceStack>("gamemode")
					.then(LiteralArgumentBuilder.literal<CommandSourceStack>(gamemode)
						.requires(requirePlayer)
						.executes { ctx ->
							val player = getSenderAsPlayer(ctx)
							when (gamemode) {
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
							return@executes 1
						}
					)
				)
			)
		}

		for (gamemodeOption in arrayOf("infinity", "minecraft", "reset")) {
			Reflection.getDedicatedServer().commands.dispatcher.register(LiteralArgumentBuilder.literal<CommandSourceStack>("infinity")
				.then(LiteralArgumentBuilder.literal<CommandSourceStack?>("defaultgamemode")
					.requires(requirePlayer)
					.then(LiteralArgumentBuilder.literal<CommandSourceStack?>(gamemodeOption)
						.executes { ctx ->
							val player = getSenderAsPlayer(ctx)
							if (gamemodeOption == "infinity" || gamemodeOption == "minecraft") {
								when (gamemodeOption) {
									"infinity" -> player.persistentDataContainer.set(Keys0.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING, gamemodeOption)
									"minecraft" -> player.persistentDataContainer.set(Keys0.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING, gamemodeOption)
								}
								player.sendMessage(Component.text("Set default gamemode to ")
									.color(NamedTextColor.GRAY)
									.append(Component.text(gamemodeOption.capitalize())
										.color(when (gamemodeOption) {
											"infinity" -> NamedTextColor.LIGHT_PURPLE
											"minecraft" -> NamedTextColor.GREEN
											else -> NamedTextColor.GRAY
										}
										)
									)
									.append(Component.text("!").color(NamedTextColor.GRAY))
								)
							} else {
								player.persistentDataContainer.remove(Keys0.DEFAULT_GAMEMODE.get())
								player.sendMessage(Component.text("Reset default gamemode!").color(NamedTextColor.RED))
							}
							return@executes 1
						}
					)
				)
			)
		}

	}

	private fun isInfinityItem(item: ItemStack): Boolean {
		return item.hasItemMeta() && item.itemMeta.persistentDataContainer.has(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)
	}

	private fun getWrongExecutorException(): CommandSyntaxException {
		return CommandSyntaxException(SimpleCommandExceptionType(LiteralMessage("You cannot execute this command!")), LiteralMessage("You cannot execute this command!"))
	}

	private fun getSenderAsPlayer(ctx: CommandContext<CommandSourceStack>): Player {
		return ctx.source.source.getBukkitSender(ctx.source) as Player
	}

}