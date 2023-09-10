package io.github.derechtepilz.infinity.gamemode.inventory

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object InventorySerializer {

    fun serializePlayerInventory(playerInventory: PlayerInventory): String {
        // This contains contents, armor and offhand (contents are indexes 0 - 35, armor 36 - 39, offhand - 40)
        val items = playerInventory.contents
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeInt(items.size)
        for (item in items) {
            if (item != null) {
                dataOutput.writeObject(item.serializeAsBytes())
            } else {
                dataOutput.writeObject(null)
            }
        }
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    fun serializeInventory(inventory: Inventory): String {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        // Write the size of the inventory
        dataOutput.writeInt(inventory.size)

        // Save every element in the list
        for (i in 0 until inventory.size) {
            dataOutput.writeObject(inventory.getItem(i)?.serializeAsBytes())
        }

        // Serialize that array
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    fun deserializeToInventory(data: String): Array<ItemStack?> {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(dataInput.readInt())
        for (i in items.indices) {
            val stack = dataInput.readObject() as ByteArray?
            if (stack != null) {
                items[i] = ItemStack.deserializeBytes(stack)
            } else {
                items[i] = ItemStack(Material.AIR)
            }
        }
        dataInput.close()
        return items
    }

}