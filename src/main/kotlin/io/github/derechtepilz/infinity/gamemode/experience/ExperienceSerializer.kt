package io.github.derechtepilz.infinity.gamemode.experience

import com.google.common.base.Preconditions
import org.bukkit.entity.Player
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.util.Base64
import kotlin.jvm.internal.Intrinsics

object ExperienceSerializer {

	@JvmStatic
	fun serializeLevel(level: Int): String {
		val outputStream = ByteArrayOutputStream()
		val bukkitOutputStream = BukkitObjectOutputStream(outputStream)
		bukkitOutputStream.writeInt(level)
		bukkitOutputStream.close()
		return Base64.getEncoder().encodeToString(outputStream.toByteArray())
	}

	@JvmStatic
	fun serializeProgress(progress: Float): String {
		val outputStream = ByteArrayOutputStream()
		val bukkitOutputStream = BukkitObjectOutputStream(outputStream)
		bukkitOutputStream.writeFloat(progress)
		bukkitOutputStream.close()
		return Base64.getEncoder().encodeToString(outputStream.toByteArray())
	}

	@JvmStatic
	fun deserialize(data: String, dataClass: Class<*>): Any {
		Preconditions.checkArgument(dataClass.simpleName == "int" || dataClass.simpleName == "float")
		val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(data))
		val bukkitInputStream = BukkitObjectInputStream(inputStream)
		val experienceData = if (dataClass.simpleName == "int") bukkitInputStream.readInt() else bukkitInputStream.readFloat()
		bukkitInputStream.close()
		return experienceData
	}

}