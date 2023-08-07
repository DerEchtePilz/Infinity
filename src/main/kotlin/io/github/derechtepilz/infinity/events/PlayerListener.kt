package io.github.derechtepilz.infinity.events

import io.github.derechtepilz.infinity.Infinity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
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

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        if (event.player.world.isEqualToAny(Bukkit.getWorld("world")!!, Bukkit.getWorld("world_nether")!!, Bukkit.getWorld("world_the_end")!!)) {
            player.sendMessage(Component.text("Want to play ")
                .color(NamedTextColor.YELLOW)
                .append(mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>"))
                .append(Component.text("? Click ").color(NamedTextColor.YELLOW))
                .append(Component.text("[here]")
                    .color(NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.runCommand("infinity teleport infinity:lobby 0 101 0"))
                    .hoverEvent(Component.text("Click to play ")
                        .color(NamedTextColor.YELLOW)
                        .append(mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>"))
                    )
                )
                .append(Component.text(" to play!").color(NamedTextColor.YELLOW))
            )
            plugin.getGamemodeInventory().openInventory(event.player)
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
