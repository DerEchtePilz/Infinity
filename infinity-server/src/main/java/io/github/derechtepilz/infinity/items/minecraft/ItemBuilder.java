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

package io.github.derechtepilz.infinity.items.minecraft;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ItemBuilder {

	private final ItemStack itemStack;
	private final ItemMeta meta;

	public ItemBuilder(Material material) {
		itemStack = new ItemStack(material);
		meta = itemStack.getItemMeta();
	}

	public ItemBuilder setName(Component displayName) {
		meta.displayName(displayName);
		return this;
	}

	public ItemBuilder setLore(List<Component> lore) {
		meta.lore(lore);
		return this;
	}

	public <T> ItemBuilder setTag(NamespacedKey key, T value, PersistentDataType<T, T> type) {
		meta.getPersistentDataContainer().set(key, type, value);
		return this;
	}

	public ItemStack build() {
		itemStack.setItemMeta(meta);
		return itemStack;
	}

}
