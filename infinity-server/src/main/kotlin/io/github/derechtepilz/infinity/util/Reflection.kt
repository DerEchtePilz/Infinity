package io.github.derechtepilz.infinity.util

import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import java.util.UUID


object Reflection {

	private val CRAFTBUKKIT_PACKAGE = Bukkit.getServer().javaClass.getPackage().name

	@JvmStatic
	fun getServerPlayer(uuid: UUID): ServerPlayer {
		return Class.forName(getCraftBukkitClass("entity.CraftPlayer")).getMethod("getHandle").invoke(Bukkit.getPlayer(uuid)) as ServerPlayer
	}

	@JvmStatic
	fun getDedicatedServer() = Class.forName(getCraftBukkitClass("CraftServer")).cast(Bukkit.getServer()) as DedicatedServer

	private fun getCraftBukkitClass(name: String): String {
		return "$CRAFTBUKKIT_PACKAGE.$name"
	}

}