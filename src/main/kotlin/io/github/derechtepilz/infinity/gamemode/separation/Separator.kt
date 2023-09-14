package io.github.derechtepilz.infinity.gamemode.separation

import java.util.UUID

/**
 * @param MinecraftObject The object that is used to serialize the data. Using a [UUID] is recommended
 * @param DeserializedType The object that should be returned after using the deserializer
 */
interface Separator<MinecraftObject, DeserializedType> {

	fun serialize(origin: MinecraftObject): String

	fun deserialize(data: String): DeserializedType

}