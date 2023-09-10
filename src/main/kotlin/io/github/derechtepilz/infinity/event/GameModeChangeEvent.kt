package io.github.derechtepilz.infinity.event

import io.github.derechtepilz.infinity.gamemode.Gamemode
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a player changes the [Gamemode]
 */
class GameModeChangeEvent(val player: Player, val previousGamemode: Gamemode, val newGamemode: Gamemode) : Event() {

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}