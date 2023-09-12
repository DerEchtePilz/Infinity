package io.github.derechtepilz.infinity

object ConfigHandler {

	private val configMap: MutableMap<String, Double> = mutableMapOf()

	@JvmStatic
	fun loadConfig(infinity: Infinity) {
		infinity.saveDefaultConfig()
		val config = infinity.config
		configMap[ConfigKeys.PLAYER_DATA_BACKUP_INTERVAL.key()] = config.getDouble(ConfigKeys.PLAYER_DATA_BACKUP_INTERVAL.key())
	}

	@JvmStatic
	operator fun set(key: ConfigKeys, value: Double) {
		configMap[key.key()] = value
	}

	@JvmStatic
	operator fun get(key: ConfigKeys): Double {
		return configMap[key.key()]!!
	}

	@JvmStatic
	fun saveConfig(infinity: Infinity) {
		val config = infinity.config
		for (s in configMap.keys) {
			config.set(s, configMap[s]!!)
		}
		infinity.saveConfig()
	}

}

enum class ConfigKeys(private val key: String) {
	PLAYER_DATA_BACKUP_INTERVAL("player-data-backup-interval");

	fun key(): String = key
}