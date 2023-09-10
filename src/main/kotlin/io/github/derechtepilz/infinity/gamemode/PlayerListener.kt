package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType

class PlayerListener(private val plugin: Infinity) : Listener {

    private val mm: MiniMessage = MiniMessage.miniMessage()
    private val infinityComponent = mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>")

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        if (!player.persistentDataContainer.has(plugin.getDefaultGamemode(), PersistentDataType.STRING)) {
            if (event.player.getGamemode() == Gamemode.MINECRAFT) {
                sendInfinitySuggestion(player)
            }
            sendDefaultGamemodeMessage(player)
        } else {
            sendResetDefaultGamemode(player)
        }
        sendTabListFooter(player, player.getGamemode())
        if (player.hasDefaultGamemode(plugin)) {
            val defaultGamemode = Gamemode.valueOf(player.persistentDataContainer.get(plugin.getDefaultGamemode(), PersistentDataType.STRING)!!.uppercase())
            if (defaultGamemode == player.getGamemode()) {
                return
            }
            player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN)
        }
    }

    private fun sendInfinitySuggestion(player: Player) {
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
    }

    private fun sendDefaultGamemodeMessage(player: Player) {
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
                    .append(Component.text("This will make ").color(NamedTextColor.GRAY))
                    .append(Component.text("Minecraft").color(NamedTextColor.GREEN))
                    .append(Component.text(" your default gamemode").color(NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.text("and makes you join the last ").color(NamedTextColor.GRAY))
                    .append(Component.text("Minecraft ").color(NamedTextColor.GREEN))
                    .append(Component.text("world").color(NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.text("you were in if you joined ").color(NamedTextColor.GRAY))
                    .append(infinityComponent)
                    .append(Component.newline())
                    .append(Component.text("in a previous session.").color(NamedTextColor.GRAY))
                )
            )
            .append(Component.text("]").color(NamedTextColor.WHITE))
            .append(Component.text(" [").color(NamedTextColor.WHITE))
            .append(infinityComponent
                .clickEvent(ClickEvent.runCommand("/infinity defaultgamemode infinity"))
                .hoverEvent(Component.text("Set your default gamemode to ")
                    .color(NamedTextColor.GRAY)
                    .append(infinityComponent)
                    .append(Component.newline())
                    .append(Component.newline())
                    .append(Component.text("This will make ").color(NamedTextColor.GRAY))
                    .append(infinityComponent)
                    .append(Component.text(" your default").color(NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.text("gamemode and makes you join the last ").color(NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(infinityComponent)
                    .append(Component.text(" world you were in if you").color(NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.text("joined ").color(NamedTextColor.GRAY))
                    .append(Component.text("Minecraft").color(NamedTextColor.GREEN))
                    .append(Component.text(" in a previous session.").color(NamedTextColor.GRAY))
                )
            )
            .append(Component.text("]").color(NamedTextColor.WHITE))
        )
    }

    private fun sendResetDefaultGamemode(player: Player) {
        player.sendMessage(Component.text("Reset default gamemode ")
            .color(NamedTextColor.GRAY)
            .append(Component.text("[here]")
                .color(NamedTextColor.GREEN)
                .clickEvent(ClickEvent.runCommand("/infinity defaultgamemode reset"))
            )
        )
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

fun sendTabListFooter(player: Player, gamemode: Gamemode) {
    when (gamemode) {
        Gamemode.MINECRAFT -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
            .color(NamedTextColor.GRAY)
            .append(Component.newline())
            .append(Component.text("/infinity gamemode infinity").color(NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Set your default gamemode by executing").color(NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("/infinity defaultgamemode <gamemode>").color(NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Reset your default gamemode by executing").color(NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("/infinity defaultgamemode reset").color(NamedTextColor.YELLOW))
        )
        Gamemode.INFINITY -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
            .color(NamedTextColor.GRAY)
            .append(Component.newline())
            .append(Component.text("/infinity gamemode minecraft").color(NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Set your default gamemode by executing").color(NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("/infinity defaultgamemode <gamemode>").color(NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Reset your default gamemode by executing").color(NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("/infinity defaultgamemode reset").color(NamedTextColor.YELLOW))
        )
        Gamemode.UNKNOWN -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
            .color(NamedTextColor.GRAY)
            .append(Component.newline())
            .append(Component.text("/infinity gamemode <gamemode>").color(NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Set your default gamemode by executing").color(NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("/infinity defaultgamemode <gamemode>").color(NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Reset your default gamemode by executing").color(NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("/infinity defaultgamemode reset").color(NamedTextColor.YELLOW))
        )
    }
}

fun Player.hasDefaultGamemode(infinity: Infinity): Boolean {
    return this.persistentDataContainer.has(infinity.getDefaultGamemode())
}