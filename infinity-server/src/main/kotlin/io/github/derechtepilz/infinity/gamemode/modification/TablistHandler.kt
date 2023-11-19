package io.github.derechtepilz.infinity.gamemode.modification

import io.github.derechtepilz.infinity.Infinity0
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.getGamemode
import io.github.derechtepilz.infinity.util.Reflection
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class TablistHandler : Listener {

	@EventHandler
	fun onWorldChange(event: PlayerChangedWorldEvent) {
		val previousGamemode = Gamemode.getFromKey(event.from.key)
		val currentGamemode = Gamemode.getFromKey(event.player.world.key)
		if (previousGamemode == currentGamemode) {
			return
		}
		val player = event.player
		if (currentGamemode == Gamemode.INFINITY) {
			Infinity0.INSTANCE.infinityPlayerList.add(player.uniqueId)
			Infinity0.INSTANCE.minecraftPlayerList.remove(player.uniqueId)
			updateTabList()
			sendBossbar(player, currentGamemode)
			return
		}
		if (currentGamemode == Gamemode.MINECRAFT) {
			Infinity0.INSTANCE.minecraftPlayerList.add(player.uniqueId)
			Infinity0.INSTANCE.infinityPlayerList.remove(player.uniqueId)
			updateTabList()
			sendBossbar(player, currentGamemode)
			return
		}
	}

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		if (event.player.getGamemode() == Gamemode.MINECRAFT) {
			Infinity0.INSTANCE.infinityPlayerList.remove(event.player.uniqueId)
			Infinity0.INSTANCE.minecraftPlayerList.add(event.player.uniqueId)
		}
		if (event.player.getGamemode() == Gamemode.INFINITY) {
			Infinity0.INSTANCE.minecraftPlayerList.remove(event.player.uniqueId)
			Infinity0.INSTANCE.infinityPlayerList.add(event.player.uniqueId)
		}

		updateTabList()
	}

	@EventHandler
	fun onQuit(event: PlayerQuitEvent) {
		Infinity0.INSTANCE.infinityPlayerList.remove(event.player.uniqueId)
		Infinity0.INSTANCE.minecraftPlayerList.remove(event.player.uniqueId)

		updateTabList()
	}

	private fun updateTabList() {
		Bukkit.getScheduler().runTaskLater(Infinity0.INSTANCE, Runnable {
			for (player in Bukkit.getOnlinePlayers()) {
				val packetsToSend: MutableList<Packet<*>> = mutableListOf()
				if (player.getGamemode() == Gamemode.INFINITY) {
					for (uuid in Infinity0.INSTANCE.infinityPlayerList) {
						val addPlayerToInfo: ClientboundPlayerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), true)
						packetsToSend.add(addPlayerToInfo)
					}
					for (uuid in Infinity0.INSTANCE.minecraftPlayerList) {
						val removePlayerFromInfo: ClientboundPlayerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), false)
						packetsToSend.add(removePlayerFromInfo)
					}
				} else {
					for (uuid in Infinity0.INSTANCE.minecraftPlayerList) {
						val addPlayerToInfo: ClientboundPlayerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), true)
						packetsToSend.add(addPlayerToInfo)
					}
					for (uuid in Infinity0.INSTANCE.infinityPlayerList) {
						val removePlayerFromInfo: ClientboundPlayerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), false)
						packetsToSend.add(removePlayerFromInfo)
					}
				}
				for (packet in packetsToSend) {
					Reflection.getServerPlayer(player.uniqueId).connection.send(packet)
				}
			}
		}, 20)
	}

	private fun sendBossbar(playerChanged: Player, newGamemode: Gamemode) {
		val bossbarChangedGamemode = BossBar.bossBar(Component.text().content(playerChanged.name)
			.color(NamedTextColor.GREEN)
			.append(Component.text().content(" now plays ").color(NamedTextColor.GRAY).build())
			.append(if (newGamemode == Gamemode.INFINITY) Infinity0.INSTANCE.infinityComponent else Component.text().content("Minecraft").color(NamedTextColor.GREEN)),
			0.0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS
		)

		val runnable = BossBarTimer(bossbarChangedGamemode)
		val taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Infinity0.INSTANCE, runnable, 0, 2)
		runnable.id = taskId
	}

	private class BossBarTimer(private val bossbarChangedGamemode: BossBar) : Runnable {

		var id: Int? = null

		var seconds = -0.1f
		var bossbarProgress = -0.02f // Progress should be at zero the first time the task runs
		override fun run() {
			seconds += 0.1f
			bossbarProgress += 0.02f

			if (seconds != 5.0f) {
				bossbarChangedGamemode.progress(if (bossbarProgress > 1.0f) 1.0f else bossbarProgress)
			}
			if (seconds >= 5.0f) {
				for (player in Bukkit.getOnlinePlayers()) {
					bossbarChangedGamemode.removeViewer(player)
				}
				Bukkit.getScheduler().cancelTask(id!!)
				return
			}
			for (player in Bukkit.getOnlinePlayers()) {
				bossbarChangedGamemode.addViewer(player)
			}
		}
	}

}