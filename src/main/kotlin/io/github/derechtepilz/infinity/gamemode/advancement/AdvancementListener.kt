package io.github.derechtepilz.infinity.gamemode.advancement

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.getGamemode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AdvancementListener : Listener {

    @EventHandler
    fun onGainCriterion(event: PlayerAdvancementCriterionGrantEvent) {
        val player = event.player
        if (player.getGamemode() == Gamemode.INFINITY) {
            event.isCancelled = true
        }
    }

}