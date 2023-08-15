package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.event.GameModeChangeEvent
import io.github.derechtepilz.infinity.util.sendTabListFooter
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class GameModeChangeListener(plugin: Infinity) : Listener {

    private val plugin: Infinity

    init {
        this.plugin = plugin
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        val from = event.from.key
        val current = event.player.world.key
        val previousGamemode = Gamemode.getFromKey(from)
        val currentGamemode = Gamemode.getFromKey(current)
        if (previousGamemode == currentGamemode) {
            return
        }
        Bukkit.getPluginManager().callEvent(GameModeChangeEvent(event.player, previousGamemode, currentGamemode))
    }

    @EventHandler
    fun onGamemodeChange(event: GameModeChangeEvent) {
        val player = event.player
        sendTabListFooter(player, player.getGamemode())
    }

}