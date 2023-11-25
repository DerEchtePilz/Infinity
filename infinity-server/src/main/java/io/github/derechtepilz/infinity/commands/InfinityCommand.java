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

package io.github.derechtepilz.infinity.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.Registry;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.gamemode.states.GamemodeState;
import io.github.derechtepilz.infinity.gamemode.story.StoryHandler;
import io.github.derechtepilz.infinity.items.InfinityItem;
import io.github.derechtepilz.infinity.items.Rarity;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.github.derechtepilz.infinity.util.Reflection;
import io.github.derechtepilz.infinity.util.StringUtil;
import io.papermc.paper.math.Position;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class InfinityCommand {

	private InfinityCommand() {
	}

	private static final String[] itemIds = Registry.Item.getItemIds();
	private static final Predicate<CommandSourceStack> requirePlayer = (stack) -> stack.source.getBukkitSender(stack) instanceof Player;
	private static final Predicate<CommandSourceStack> requirePlayerOperator = requirePlayer.and((stack) -> stack.source.getBukkitSender(stack).isOp());

	public static void register() {
		Reflection.getDedicatedServer().getCommands().getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("infinity")
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("give")
				.requires(requirePlayerOperator)
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("*")
					.executes((ctx) -> {
							Player sender = getSenderAsPlayer(ctx);
							sender.getInventory().addItem(Registry.Item.getAllItems());
							return 1;
						}
					))
			)
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("upgrade")
				.requires(requirePlayerOperator)
				.executes((ctx) -> {
					Player player = getSenderAsPlayer(ctx);
					ItemStack heldItem = player.getInventory().getItemInMainHand();
					int selectedSlot = player.getInventory().getHeldItemSlot();
					if (!isInfinityItem(heldItem)) {
						player.sendMessage(Component.text("Cannot upgrade rarity for item ")
							.color(NamedTextColor.RED)
							.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
							.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
						);
						return 1;
					}
					// Upgrade the item
					String itemId = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING);
					String currentRarity = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.RARITY_KEY, PersistentDataType.STRING);
					int variationId = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER);

					InfinityItem basicItem = Registry.Item.getItem(itemId, variationId);
					InfinityItem upgradedItem = basicItem.upgradeItem(Rarity.valueOf(currentRarity.toUpperCase()));
					player.getInventory().setItem(selectedSlot, upgradedItem);
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
					);
					return 1;
				})
			)
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("downgrade")
				.requires(requirePlayerOperator)
				.executes((ctx) -> {
					Player player = getSenderAsPlayer(ctx);
					// Check if there's an InfinityItem in the main hand
					ItemStack heldItem = player.getInventory().getItemInMainHand();
					int selectedSlot = player.getInventory().getHeldItemSlot();
					if (!isInfinityItem(heldItem)) {
						player.sendMessage(Component.text("Cannot downgrade rarity for item ")
							.color(NamedTextColor.RED)
							.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
							.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
						);
						return 1;
					}
					// Downgrade the item
					String itemId = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING);
					String currentRarity = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.RARITY_KEY, PersistentDataType.STRING);
					int variationId = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER);

					InfinityItem basicItem = Registry.Item.getItem(itemId, variationId);
					InfinityItem downgradedItem = basicItem.downgradeItem(Rarity.valueOf(currentRarity.toUpperCase()));
					player.getInventory().setItem(selectedSlot, downgradedItem);
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
					);
					return 1;
				})
			)
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("startstory")
				.requires(requirePlayer.and(
					stack -> stack.source.getBukkitSender(stack).hasPermission("infinity.startstory"))
				)
				.executes((ctx) -> {
					Player player = getSenderAsPlayer(ctx);
					Infinity.getInstance().getPlayerPermissions().getOrDefault(player.getUniqueId(), player.addAttachment(Infinity.getInstance())).setPermission("infinity.startstory", false);
					PlayerUtil.terminateStoryTitleTask(player);
					player.updateCommands();
					player.getPersistentDataContainer().set(Keys.STORY_STARTED.get(), PersistentDataType.BOOLEAN, true);
					player.sendMessage(Component.text().content("Story started! Have fun!").color(NamedTextColor.LIGHT_PURPLE).build());

					// Disable gamemode switching for the player executing this command
					player.getPersistentDataContainer().set(Keys.GAMEMODE_SWITCH_ENABLED.get(), PersistentDataType.BOOLEAN, false); // Disable gamemode switching during introduction sequence
					player.getPersistentDataContainer().set(Keys.INTRODUCTION_SEQUENCE.get(), PersistentDataType.BOOLEAN, true); // Track if the player currently sees the introduction sequence

					// Initiate story start sequence
					StoryHandler.startIntroduction(player);
					return 1;
				})
			)
		);

		for (String itemId : itemIds) {
			Reflection.getDedicatedServer().getCommands().getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("infinity")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("give")
					.requires(requirePlayerOperator)
					.then(LiteralArgumentBuilder.<CommandSourceStack>literal(itemId)
						.then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("variation", IntegerArgumentType.integer(0, 100))
							.executes((ctx) -> {
								Player player = getSenderAsPlayer(ctx);
								int variationId = ctx.getArgument("variation", int.class);
								InfinityItem item = Registry.Item.getItem(itemId, variationId);
								if (item == null) {
									InfinityItem backup = Registry.Item.getItem(itemId, 0);
									player.sendMessage(Component.text("The ")
										.color(NamedTextColor.RED)
										.append(backup.displayName().color(NamedTextColor.RED).hoverEvent(Registry.Item.getItem(itemId, 0)
										))
										.append(Component.text(" does not have a variation id $variationId"))
									);
									return 1;
								}
								player.getInventory().addItem(item);
								return 1;
							})
						)
						.executes((ctx) -> {
							Player player = getSenderAsPlayer(ctx);
							InfinityItem item = Registry.Item.getItem(itemId, -1);
							player.getInventory().addItem(item);
							return 1;
						})
					)
				)
			);
		}

		for (String rarityName : Arrays.stream(Rarity.values()).map(rarity -> rarity.name().toLowerCase()).toArray(String[]::new)) {
			Reflection.getDedicatedServer().getCommands().getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("infinity")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("changerarity")
					.requires(requirePlayerOperator)
					.then(LiteralArgumentBuilder.<CommandSourceStack>literal(rarityName)
						.executes((ctx) -> {
							Player player = getSenderAsPlayer(ctx);
							Rarity rarity = Rarity.valueOf(rarityName.toUpperCase());
							ItemStack heldItem = player.getInventory().getItemInMainHand();
							int selectedSlot = player.getInventory().getHeldItemSlot();
							if (!isInfinityItem(heldItem)) {
								player.sendMessage(Component.text("Cannot change rarity for item ")
									.color(NamedTextColor.RED)
									.append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
									.append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
								);
								return 1;
							}
							String itemId = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING);
							int variation = heldItem.getItemMeta().getPersistentDataContainer().get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER);
							InfinityItem item = Registry.Item.getItem(itemId, variation);
							player.getInventory().setItem(selectedSlot, item.updateRarityTo(rarity));
							return 1;
						})
					)
				)
			);
		}

		for (String gamemode : List.of("infinity", "minecraft")) {
			Reflection.getDedicatedServer().getCommands().getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("infinity")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("gamemode")
					.then(LiteralArgumentBuilder.<CommandSourceStack>literal(gamemode)
						.requires(requirePlayer)
						.executes((ctx -> {
								Player player = getSenderAsPlayer(ctx);
								switch (gamemode) {
									case "infinity" -> {
										if (PlayerUtil.getGamemode(player) != Gamemode.INFINITY) {
											GamemodeState.INFINITY.loadFor(player);
										} else {
											player.sendMessage(Component.text("You cannot execute this command right now as you are already playing ")
												.color(NamedTextColor.RED)
												.append(Infinity.getInstance().getInfinityComponent())
											);
										}
									}

									case "minecraft" -> {
										if (PlayerUtil.getGamemode(player) != Gamemode.MINECRAFT) {
											GamemodeState.MINECRAFT.loadFor(player);
										} else {
											player.sendMessage(Component.text("You cannot execute this command right now as you are already playing ")
												.color(NamedTextColor.RED)
												.append(Component.text("Minecraft").color(NamedTextColor.GREEN))
											);
										}
									}
								}
								return 1;
							})
						)
					)
				)
			);
		}

		for (String gamemodeOption : List.of("infinity", "minecraft", "reset")) {
			Reflection.getDedicatedServer().getCommands().getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("infinity")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("defaultgamemode")
					.requires(requirePlayer)
					.then(LiteralArgumentBuilder.<CommandSourceStack>literal(gamemodeOption)
						.executes((ctx) -> {
							Player player = getSenderAsPlayer(ctx);
							if (gamemodeOption.equals("infinity") || gamemodeOption.equals("minecraft")) {
								player.getPersistentDataContainer().set(Keys.DEFAULT_GAMEMODE.get(), PersistentDataType.STRING, gamemodeOption);
								player.sendMessage(Component.text("Set default gamemode to ")
									.color(NamedTextColor.GRAY)
									.append(Component.text(StringUtil.capitalize(gamemodeOption))
										.color(switch (gamemodeOption) {
												case "infinity" -> NamedTextColor.LIGHT_PURPLE;
												case "minecraft" -> NamedTextColor.GREEN;
												default -> NamedTextColor.GRAY;
											}
										)
									)
									.append(Component.text("!").color(NamedTextColor.GRAY))
								);
							} else {
								player.getPersistentDataContainer().remove(Keys.DEFAULT_GAMEMODE.get());
								player.sendMessage(Component.text("Reset default gamemode!").color(NamedTextColor.RED));
							}
							return 1;
						})
					)
				)
			);
		}
	}

	private static boolean isInfinityItem(ItemStack item) {
		return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING);
	}

	private static Player getSenderAsPlayer(CommandContext<CommandSourceStack> ctx) {
		return (Player) ctx.getSource().source.getBukkitSender(ctx.getSource());
	}

}
