package io.github.derechtepilz.infinity.events

import io.github.derechtepilz.infinity.Infinity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class GameModeChangeListener(plugin: Infinity) : Listener {

    private val plugin: Infinity

    init {
        this.plugin = plugin
    }

    @EventHandler
    fun onGamemodeChange(event: PlayerChangedWorldEvent) {

    }

}