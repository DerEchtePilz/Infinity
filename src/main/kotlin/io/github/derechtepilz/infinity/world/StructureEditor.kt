package io.github.derechtepilz.infinity.world

import com.google.gson.JsonParser
import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.StringBuilder

class StructureEditor(structure: File) {

    private val structure: File

    init {
        this.structure = structure
    }

    fun deserializeStructureToLocations(): List<Location> {
        val structureLocations: MutableList<Location> = mutableListOf()
        val parent = structure.parentFile.path.split("\\")
        val worldName = parent[parent.size - 1]
        val world = Bukkit.getWorld(NamespacedKey(Infinity.NAME, worldName))!!

        val reader = BufferedReader(FileReader(structure))
        val builder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }
        reader.close()
        val structureArray = JsonParser.parseString(builder.toString()).asJsonArray
        for (i in 0 until structureArray.size()) {
            val blockLocationObject = structureArray[i].asJsonObject
            val locX = blockLocationObject.get("locX").asInt
            val locY = blockLocationObject.get("locY").asInt
            val locZ = blockLocationObject.get("locZ").asInt
            val blockLocation = Location(world, locX.toDouble(), locY.toDouble(), locZ.toDouble())
            structureLocations.add(blockLocation)
        }
        return structureLocations.toList()
    }

}