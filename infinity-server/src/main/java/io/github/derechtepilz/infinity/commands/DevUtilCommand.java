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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.Reflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class DevUtilCommand {

	private DevUtilCommand() {}

	private static final Predicate<CommandSourceStack> requiresPlayer = (stack) -> stack.source.getBukkitSender(stack) instanceof Player;
	private static final Predicate<CommandSourceStack> requiresPlayerOperator = requiresPlayer.and((stack) -> stack.source.getBukkitSender(stack).isOp());

	public static void register() {
		Reflection.getDedicatedServer().getCommands().getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("devutil")
			.requires(requiresPlayerOperator)
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("removekey")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("*")
					.executes((ctx) -> {
						Player player = getPlayerSender(ctx);
						List<NamespacedKey> namespacedKeyList = new ArrayList<>();
						for (Keys key : Keys.values()) {
							namespacedKeyList.add(key.get());
						}
						namespacedKeyList.removeIf(key -> !player.getPersistentDataContainer().has(key));
						List<Keys> keysToRemove = new ArrayList<>();
						for (NamespacedKey namespace : namespacedKeyList) {
							if (Keys.fromNamespacedKey(namespace).isState()) {
								keysToRemove.add(Keys.fromNamespacedKey(namespace));
							}
						}
						player.sendMessage(Component.text().content("Removed keys:").color(NamedTextColor.RED));
						for (Keys key : keysToRemove) {
							Keys.removeKey(player, key);
							player.sendMessage(Component.text().content("- " + key.get()));
						}
						return 1;
					})
				)
				.then(RequiredArgumentBuilder.<CommandSourceStack, ResourceLocation>argument("key", ResourceLocationArgument.id())
					.suggests((ctx, builder) -> {
						Player player = getPlayerSender(ctx);
						List<NamespacedKey> namespacedKeyList = new ArrayList<>();
						for (Keys key : Keys.values()) {
							namespacedKeyList.add(key.get());
							if (!key.isState()) {
								namespacedKeyList.remove(key.get());
							}
						}
						namespacedKeyList.removeIf(key -> !player.getPersistentDataContainer().has(key));
						List<NamespacedKey> suggestedList = new ArrayList<>();
						for (NamespacedKey namespace : namespacedKeyList) {
							if (namespace.asString().startsWith(builder.getRemaining())) {
								suggestedList.add(namespace);
							}
						}
						String[] namespacedKeys = suggestedList.stream().map(NamespacedKey::toString).toArray(String[]::new);
						Suggestions suggestions;
						SuggestionsBuilder keySuggestions = builder.add(builder);
						for (String suggestion : namespacedKeys) {
							if (suggestion.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
								keySuggestions = builder.suggest(suggestion);
							}
						}
						suggestions = keySuggestions.build();
						return CompletableFuture.completedFuture(suggestions);
					})
					.executes(ctx -> {
						ResourceLocation resource = ResourceLocationArgument.getId(ctx, "key");
						NamespacedKey key = NamespacedKey.fromString(resource.getNamespace() + ":" + resource.getPath());
						Player player = getPlayerSender(ctx);
						try {
							Keys.removeKey(player, Keys.fromNamespacedKey(key));
							player.sendMessage(Component.text().content("Removed key '" + key.asString() + "' successfully! Undefined behaviour ahead (maybe!)").color(NamedTextColor.RED).build());
						} catch (IllegalStateException e) {
							player.sendMessage(Component.text().content(e.getMessage()).color(NamedTextColor.RED));
						}
						return 1;
					})
				)
			)
		);
	}

	private static Player getPlayerSender(CommandContext<CommandSourceStack> ctx) {
		return (Player) ctx.getSource().getBukkitSender();
	}

}
