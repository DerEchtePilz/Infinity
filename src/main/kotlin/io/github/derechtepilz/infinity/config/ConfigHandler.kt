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

	@JvmStatic
	fun loadConfig() {
		if (!checkDirectory()) {
			for (key in DEFAULT_MAP.keys) {
				activeConfig[key] = DEFAULT_MAP[key]!!
			}
			return
		}
		val configJson = readConfig()
		for (key in configJson.keySet()) {
			activeConfig[key] = configJson[key].asJsonObject
		}
	}

	@JvmStatic
	fun saveConfig() {
		writeConfig()
	}

	@JvmStatic
	fun resetConfig() {
		for (key in DEFAULT_MAP.keys) {
			activeConfig[key] = DEFAULT_MAP[key]!!
		}
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

	private fun checkDirectory(): Boolean {
		val directory = File("./infinity/config")
		return directory.exists()
	}

	private fun checkConfig(): Boolean {
		val directory = File("./infinity/config")
		val config = File(directory, "config.json")
		return config.exists()
	}

	private fun getDirectory(): File {
		val directory = File("./infinity/config")
		if (!checkDirectory()) {
			directory.mkdirs()
			return directory
		}
		return directory
	}

	private fun getConfig(): File {
		val config = File(getDirectory(), "config.json")
		if (!checkConfig()) {
			config.createNewFile()
			return config
		}
		return config
	}

	private fun getReader(): BufferedReader {
		return BufferedReader(FileReader(getConfig()))
	}

	private fun getWriter(): BufferedWriter {
		return BufferedWriter(FileWriter(getConfig()))
	}

	private fun readConfig(): JsonObject {
		var line: String?
		val builder = StringBuilder()
		val reader = getReader()
		while (reader.readLine().also { line = it } != null) {
			builder.append(line)
		}
		reader.close()
		return JsonParser.parseString(builder.toString()).asJsonObject
	}

	private fun writeConfig() {
		val configWriter = getWriter()
		configWriter.write(createJson())
		configWriter.close()
	}

	private fun createJson(): String {
		val configJson = JsonObject()
		for (key in activeConfig.keys) {
			configJson.add(key, activeConfig[key]!!)
		}
		return GsonBuilder().setPrettyPrinting().create().toJson(configJson)
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