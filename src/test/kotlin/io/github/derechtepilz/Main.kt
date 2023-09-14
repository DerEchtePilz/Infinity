package io.github.derechtepilz

import io.github.derechtepilz.infinity.Infinity
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

open class Main : JavaPlugin {

	override fun onEnable() {
		Infinity.loadItems()
	}

	override fun onDisable() {

	}


	// MockBukkit constructors
	constructor() : super()
	constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File) : super(loader, description, dataFolder, file)
}
