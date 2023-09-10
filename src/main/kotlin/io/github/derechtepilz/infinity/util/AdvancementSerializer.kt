package io.github.derechtepilz.infinity.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.advancement.Advancement
import org.bukkit.entity.Player
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

object AdvancementSerializer {

    @JvmStatic
    fun serializeAdvancements(player: Player): String {
        val advancementsDone: MutableList<NamespacedKey> = mutableListOf()
        val criteriaDone: MutableMap<NamespacedKey, MutableList<String>> = mutableMapOf()

        // Iterate over every advancement and save the player's progress on every advancement
        val advancementIterator = Bukkit.advancementIterator()
        while (advancementIterator.hasNext()) {
            val advancement: Advancement = advancementIterator.next()
            val progress = player.getAdvancementProgress(advancement)
            if (progress.isDone) {
                advancementsDone.add(advancement.key)
            }
            val advancementCriteriaDone: MutableList<String> = mutableListOf()
            for (criteria in progress.awardedCriteria) {
                advancementCriteriaDone.add(criteria)
            }
            criteriaDone[advancement.key] = advancementCriteriaDone
        }

        // Save advancement information in a Json format not connected to the player
        val advancementObject = JsonObject()
        val advancementsDoneArray = JsonArray()
        val criteriaDoneArray = JsonArray()
        for (key in advancementsDone) {
            val advancementDone = JsonObject()
            advancementDone.addProperty("advancementKey", key.asString())
            advancementsDoneArray.add(advancementDone)
        }
        for (key in criteriaDone.keys) {
            val advancementProgress = JsonObject()
            val advancementCriteriaDone = JsonArray()
            for (criteria in criteriaDone[key]!!) {
                val advancementCriterion = JsonObject()
                advancementCriterion.addProperty("criterion", criteria)
                advancementCriteriaDone.add(advancementCriterion)
            }
            advancementProgress.add(key.asString(), advancementCriteriaDone)
            criteriaDoneArray.add(advancementProgress)
        }
        advancementObject.add("advancementsDone", advancementsDoneArray)
        advancementObject.add("criteriaDone", criteriaDoneArray)

        // Convert Json into Base64
        val jsonString = GsonBuilder().setPrettyPrinting().create().toJson(advancementObject)
        val jsonStringAsBase64 = Base64.getEncoder().encode(jsonString.encodeToByteArray())

        // Write to ObjectOutputStream
        val outputStream = ByteArrayOutputStream()
        outputStream.write(jsonStringAsBase64)
        outputStream.close()

        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    @JvmStatic
    fun deserializeAdvancements(data: String): Array<Any> {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val base64JsonString = inputStream.readAllBytes()
        val jsonString = String(Base64.getDecoder().decode(base64JsonString))

        val advancementObject = JsonParser.parseString(jsonString).asJsonObject
        val advancementsDoneArray = advancementObject.get("advancementsDone").asJsonArray
        val criteriaDoneArray = advancementObject.get("criteriaDone").asJsonArray

        val advancementsDone: MutableList<NamespacedKey> = mutableListOf()
        val criteriaDone: MutableMap<NamespacedKey, MutableList<String>> = mutableMapOf()

        for (i in 0 until advancementsDoneArray.size()) {
            val advancementDoneObject = advancementsDoneArray[i].asJsonObject
            val advancementKey = advancementDoneObject.get("advancementKey").asString
            val advancementNamespace = NamespacedKey.fromString(advancementKey, null)!!
            advancementsDone.add(advancementNamespace)
        }
        for (i in 0 until criteriaDoneArray.size()) {
            val advancementCriteria: MutableList<String> = mutableListOf()
            val advancementProgress = criteriaDoneArray[i].asJsonObject
            var advancementNamespace: NamespacedKey? = null
            for (key in advancementProgress.keySet()) {
                advancementNamespace = NamespacedKey.fromString(key, null)!!
                val advancementCriteriaDoneArray = advancementProgress[key].asJsonArray
                for (j in 0 until advancementCriteriaDoneArray.size()) {
                    val advancementCriterionDone = advancementCriteriaDoneArray[j].asJsonObject
                    val criterion = advancementCriterionDone["criterion"].asString
                    advancementCriteria.add(criterion)
                }
            }
            criteriaDone[advancementNamespace!!] = advancementCriteria
        }

        return arrayOf(advancementsDone, criteriaDone)
    }

}