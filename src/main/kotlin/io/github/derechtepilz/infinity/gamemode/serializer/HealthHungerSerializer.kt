package io.github.derechtepilz.infinity.gamemode.serializer

import org.bukkit.entity.Player
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.util.*

object HealthHungerSerializer {

	@JvmStatic
	fun serialize(player: Player): String {
		val outputStream = ByteArrayOutputStream()
		val bukkitOutputStream = BukkitObjectOutputStream(outputStream)
		bukkitOutputStream.writeDouble(player.health)
		bukkitOutputStream.writeInt(player.foodLevel)
		bukkitOutputStream.writeFloat(player.saturation)
		bukkitOutputStream.close()
		return Base64.getEncoder().encodeToString(outputStream.toByteArray())
	}

	@JvmStatic
	fun deserialize(data: String): MutableList<Any> {
		val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(data))
		val bukkitInputStream = BukkitObjectInputStream(inputStream)
		val health = bukkitInputStream.readDouble()
		val foodLevel = bukkitInputStream.readInt()
		val saturation = bukkitInputStream.readFloat()
		return mutableListOf(health, foodLevel, saturation)
	}

}