package io.github.derechtepilz.infinity.commands

import dev.jorel.commandapi.arguments.NamespacedKeyArgument
import dev.jorel.commandapi.arguments.SafeSuggestions
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.namespacedKeyArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.Keys.Companion.removeKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.IllegalStateException

class DevUtilCommand {

	fun register() {
		commandTree("devutil") {
			withRequirement { sender: CommandSender -> sender.isOp }
			literalArgument("removekey") {
				literalArgument("*") {
					playerExecutor { player, _ ->
						val namespacedKeyList: MutableList<NamespacedKey> = mutableListOf()
						for (key in Keys.entries) {
							namespacedKeyList.add(key.get())
						}
						namespacedKeyList.removeIf { key: NamespacedKey -> !player.persistentDataContainer.has(key) }
						val keysToRemove: MutableList<Keys> = mutableListOf()
						for (namespace in namespacedKeyList) {
							if (Keys.fromNamespacedKey(namespace)!!.isState) {
								keysToRemove.add(Keys.fromNamespacedKey(namespace)!!)
							}
						}
						player.sendMessage(Component.text().content("Removed keys:").color(NamedTextColor.RED))
						for (key in keysToRemove) {
							player.removeKey(key)
							player.sendMessage(Component.text().content("- ${key.get()}"))
						}
					}
				}
				namespacedKeyArgument("key") {
					(this as NamespacedKeyArgument).replaceSafeSuggestions(SafeSuggestions.suggest { info ->
						val namespacedKeyList: MutableList<NamespacedKey> = mutableListOf()
						for (key in Keys.entries) {
							namespacedKeyList.add(key.get())
						}
						namespacedKeyList.removeIf { key: NamespacedKey -> info.sender is Player && !(info.sender as Player).persistentDataContainer.has(key) }
						val suggestedList: MutableList<NamespacedKey> = mutableListOf()
						for (namespace in namespacedKeyList) {
							if (namespace.asString().startsWith(info.currentArg)) {
								suggestedList.add(namespace)
							}
						}

						return@suggest suggestedList.toTypedArray()
					})
					playerExecutor { player, args ->
						val keyToRemove: NamespacedKey = args["key"]!! as NamespacedKey
						try {
							player.removeKey(Keys.fromNamespacedKey(keyToRemove)!!)
							player.sendMessage(Component.text().content("Removed key '${keyToRemove.asString()}' successfully! Undefined behaviour ahead (maybe!)").color(NamedTextColor.RED).build())
						} catch (e: IllegalStateException) {
							player.sendMessage(Component.text().content(e.message!!).color(NamedTextColor.RED))
						}
					}
				}
			}
		}
	}

}