/*
 *  Infinity - a Minecraft story-game for Paper servers
 *  Copyright (C) 2023  DerEchtePilz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.derechtepilz.infinity.gamemode.serializer

import org.bukkit.entity.Player
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object ExperienceSerializer {

	@JvmStatic
	fun serialize(player: Player): String {
		val outputStream = ByteArrayOutputStream()
		val bukkitOutputStream = BukkitObjectOutputStream(outputStream)
		bukkitOutputStream.writeInt(player.level)
		bukkitOutputStream.writeFloat(player.exp)
		bukkitOutputStream.close()
		return Base64.getEncoder().encodeToString(outputStream.toByteArray())
	}

	@JvmStatic
	fun deserialize(data: String): MutableList<Any> {
		val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(data))
		val bukkitInputStream = BukkitObjectInputStream(inputStream)
		val level = bukkitInputStream.readInt()
		val progress = bukkitInputStream.readFloat()
		bukkitInputStream.close()
		return mutableListOf(level, progress)
	}

}