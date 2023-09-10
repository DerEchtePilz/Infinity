package io.github.derechtepilz.infinity.util

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.Gamemode
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

/********************
 *     GAMEMODE     *
 ********************/

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

/********************
 *     UTILITY      *
 ********************/

fun World.isEqualToAny(vararg worlds: World): Boolean {
    for (world in worlds) {
        if (world == this) {
            return true
        }
    }
    return false
}

fun String.capitalize(): String {
    return this.replaceFirstChar { firstChar -> if (firstChar.isLowerCase()) firstChar.titlecase(Locale.getDefault()) else firstChar.toString() }
}