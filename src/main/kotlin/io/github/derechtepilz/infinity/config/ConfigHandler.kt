package io.github.derechtepilz.infinity.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.derechtepilz.infinity.config.key.PlayerDataBackupKey
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.StringBuilder

object ConfigHandler {

	private val DEFAULT_MAP: Map<String, JsonObject> = mapOf(
		PlayerDataBackupKey.BACKUP_KEY to ConfigElements.PLAYER_DATA_BACKUP_INTERVAL.getElement()
	)

	private val activeConfig: MutableMap<String, JsonObject> = mutableMapOf()
	private val configCache: MutableMap<String, List<Any>> = mutableMapOf()

	@JvmStatic
	fun loadConfig() {
		val configDirectory = File("./infinity/config")
		if (!configDirectory.exists()) {
			for (key in DEFAULT_MAP.keys) {
				activeConfig[key] = DEFAULT_MAP[key]!!
			}
			return
		}
		val configFile = File(configDirectory, "config.json")
		val reader = BufferedReader(FileReader(configFile))
		val builder = StringBuilder()
		var line: String?
		while (reader.readLine().also { line = it } != null) {
			builder.append(line)
		}
		reader.close()
		val configJson = JsonParser.parseString(builder.toString()).asJsonObject
		for (key in configJson.keySet()) {
			activeConfig[key] = configJson[key].asJsonObject
		}
	}

	@JvmStatic
	fun saveConfig() {
		val configDirectory = File("./infinity/config")
		if (!configDirectory.exists()) {
			configDirectory.mkdirs()
		}
		val configFile = File(configDirectory, "config.json")
		if (!configFile.exists()) {
			configFile.createNewFile()
		}
		val configJson = JsonObject()
		for (key in activeConfig.keys) {
			configJson.add(key, activeConfig[key]!!)
		}
		val configJsonString = GsonBuilder().setPrettyPrinting().create().toJson(configJson)

		val configWriter = BufferedWriter(FileWriter(configFile))
		configWriter.write(configJsonString)
		configWriter.close()
	}

	@JvmStatic
	fun getBackupInterval(): PlayerDataBackupKey {
		val backupJson = activeConfig[PlayerDataBackupKey.BACKUP_KEY]!!
		val interval = backupJson["interval"].asInt
		var timeUnit = ""

		val timeUnitArray = backupJson["timeUnit"].asJsonArray
		var isActiveFound: Boolean
		for (i in 0 until timeUnitArray.size()) {
			val timeUnitConfig = timeUnitArray[i].asJsonObject
			isActiveFound = timeUnitConfig["active"].asBoolean
			if (isActiveFound) {
				timeUnit = timeUnitConfig["timeUnit"].asString
			}
		}

		return PlayerDataBackupKey(interval, timeUnit)
	}

	@JvmStatic
	fun setBackupInterval(backupInterval: PlayerDataBackupKey) {
		activeConfig[backupInterval.getBackupKey()] = backupInterval.toJson()
	}

	@JvmStatic
	fun resetConfig() {
		for (key in DEFAULT_MAP.keys) {
			activeConfig[key] = DEFAULT_MAP[key]!!
		}
	}

}

/**
 * Used for default values
 */
private enum class ConfigElements {
	PLAYER_DATA_BACKUP_INTERVAL;

	fun getElement(): JsonObject {
		return when (this) {
			PLAYER_DATA_BACKUP_INTERVAL -> {
				val dataBackup = JsonObject()
				dataBackup.addProperty("interval", 10)

				val timeUnit = JsonArray()
				val timeUnitSeconds = JsonObject()
				timeUnitSeconds.addProperty("active", false)
				timeUnitSeconds.addProperty("timeUnit", "seconds")

				val timeUnitMinutes = JsonObject()
				timeUnitMinutes.addProperty("active", true)
				timeUnitMinutes.addProperty("timeUnit", "minutes")

				val timeUnitHours = JsonObject()
				timeUnitHours.addProperty("active", false)
				timeUnitHours.addProperty("timeUnit", "hours")

				timeUnit.add(timeUnitSeconds)
				timeUnit.add(timeUnitMinutes)
				timeUnit.add(timeUnitHours)

				dataBackup.add("timeUnit", timeUnit)

				dataBackup
			}
		}
	}

}