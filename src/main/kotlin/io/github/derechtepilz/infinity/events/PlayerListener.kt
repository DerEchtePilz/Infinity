package io.github.derechtepilz.infinity.events

import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class PlayerListener(private val plugin: Infinity) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (event.player.world.isEqualToAny(Bukkit.getWorld("world")!!, Bukkit.getWorld("world_nether")!!, Bukkit.getWorld("world_the_end")!!)) {
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
