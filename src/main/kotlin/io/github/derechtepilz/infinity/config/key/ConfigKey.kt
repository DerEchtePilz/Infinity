package io.github.derechtepilz.infinity.config.key

import com.google.gson.JsonObject

interface ConfigKey<T : ConfigKey<T>> {

	fun getBackupKey(): String

	fun toJson(): JsonObject

}