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

package io.github.derechtepilz.infinity.gamemode.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.infinity.Infinity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class InventorySerializer {

	private InventorySerializer() {}

	public static String serialize(Player player) {
		try {
			PlayerInventory playerInventory = player.getInventory();
			Inventory playerEnderChest = player.getEnderChest();

			// Serialize player inventory and ender chest
			String playerInventoryData = serializeInventory(playerInventory);
			String playerEnderChestData = serializeInventory(playerEnderChest);

			// Convert data into a Json string
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("inventory", playerInventoryData);
			jsonObject.addProperty("enderChest", playerEnderChestData);
			String jsonString = new Gson().toJson(jsonObject);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);
			bukkitOutputStream.write(jsonString.getBytes());
			bukkitOutputStream.close();

			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was an error while serializing inventory and ender chest. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

	public static List<ItemStack[]> deserialize(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			JsonObject jsonObject = JsonParser.parseString(new String(dataInput.readAllBytes())).getAsJsonObject();
			dataInput.close();
			ItemStack[] inventoryData = deserializeInventory(jsonObject.get("inventory").getAsString());
			ItemStack[] enderChest = deserializeInventory(jsonObject.get("enderChest").getAsString());
			return new ArrayList<>(List.of(inventoryData, enderChest));
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was an error while deserializing inventory and ender chest. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

	private static String serializeInventory(Inventory inventory) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(inventory.getSize());

			// Save every element in the list
			for (int i = 0; i < inventory.getSize(); i++) {
				dataOutput.writeObject(inventory.getItem(i).serializeAsBytes());
			}

			// Serialize that array
			dataOutput.close();
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while serializing inventory and ender chest. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

	private static ItemStack[] deserializeInventory(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];
			for (int i = 0; i < items.length; i++) {
				byte[] stack = (byte[]) dataInput.readObject();
				if (stack != null) {
					items[i] = ItemStack.deserializeBytes(stack);
				} else {
					items[i] = new ItemStack(Material.AIR);
				}
			}
			dataInput.close();
			return items;
		} catch (IOException | ClassNotFoundException e) {
			Infinity.getInstance().getLogger().severe("There was an error while deserializing inventory and ender chest. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

}
