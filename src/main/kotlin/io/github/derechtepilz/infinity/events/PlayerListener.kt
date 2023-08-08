package io.github.derechtepilz.infinity.events

import io.github.derechtepilz.infinity.Infinity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class PlayerListener(private val plugin: Infinity) : Listener {

    private val mm: MiniMessage = MiniMessage.miniMessage()
    private val infinityComponent = mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>")

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        if (event.player.world.isEqualToAny(Bukkit.getWorld("world")!!, Bukkit.getWorld("world_nether")!!, Bukkit.getWorld("world_the_end")!!)) {
            player.sendMessage(Component.text("Want to play ")
                .color(NamedTextColor.YELLOW)
                .append(infinityComponent)
                .append(Component.text("? Click ").color(NamedTextColor.YELLOW))
                .append(Component.text("[here]")
                    .color(NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.runCommand("/infinity gamemode infinity"))
                    .hoverEvent(Component.text("Click to play ")
                        .color(NamedTextColor.YELLOW)
                        .append(infinityComponent)
                    )
                )
                .append(Component.text(" to play!").color(NamedTextColor.YELLOW))
            )
            player.sendMessage(Component.text("Set default gamemode: ")
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(NamedTextColor.GRAY)
                .append(Component.text("[").color(NamedTextColor.WHITE))
                .append(Component.text("Minecraft")
                    .color(NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.runCommand("/infinity defaultgamemode minecraft"))
                    .hoverEvent(Component.text("Set your default gamemode to ")
                        .color(NamedTextColor.GRAY)
                        .append(Component.text("Minecraft").color(NamedTextColor.GREEN))
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("This will make you join one of the default Minecraft worlds:").color(NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("minecraft:overworld").color(NamedTextColor.GREEN))
                        .append(Component.text(", ").color(NamedTextColor.GRAY))
                        .append(Component.text("minecraft:the_nether").color(NamedTextColor.RED))
                        .append(Component.text(", ").color(NamedTextColor.GRAY))
                        .append(Component.text("minecraft:the_end").color(NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("This makes you join ").color(NamedTextColor.GRAY))
                        .append(Component.text("minecraft:overworld").color(NamedTextColor.GREEN))
                        .append(Component.text(" if you joined").color(NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(infinityComponent)
                        .append(Component.text(" in a previous session."))
                    )
                )
                .append(Component.text("]").color(NamedTextColor.WHITE))
                .append(Component.text(" [").color(NamedTextColor.WHITE))
                .append(Component.text("Infinity")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .clickEvent(ClickEvent.runCommand("/infinity defaultgamemode infinity"))
                    .hoverEvent(Component.text("Set your default gamemode to ")
                        .color(NamedTextColor.GRAY)
                        .append(Component.text("Infinity").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("This will make you join one of the ").color(NamedTextColor.GRAY))
                        .append(infinityComponent)
                        .append(Component.text(" worlds: ").color(NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("infinity:lobby").color(NamedTextColor.WHITE))
                        .append(Component.text(", ").color(NamedTextColor.GRAY))
                        .append(Component.text("infinity:sky").color(NamedTextColor.AQUA))
                        .append(Component.text(", ").color(NamedTextColor.GRAY))
                        .append(Component.text("infinity:stone").color(NamedTextColor.YELLOW))
                        .append(Component.text(", ").color(NamedTextColor.GRAY))
                        .append(Component.text("infinity:nether").color(NamedTextColor.RED))
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("This makes you join ").color(NamedTextColor.GRAY))
                        .append(Component.text("infinity:lobby").color(NamedTextColor.WHITE))
                        .append(Component.text(" if you joined").color(NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("Minecraft").color(NamedTextColor.GREEN))
                        .append(Component.text(" in a previous session."))
                    )
                )
                .append(Component.text("]").color(NamedTextColor.WHITE))
            )
        }
    }

}

fun World.isEqualToAny(vararg worlds: World): Boolean {
    for (world in worlds) {
        if (world == this) {
            return true
        }
    }
    return false
}
