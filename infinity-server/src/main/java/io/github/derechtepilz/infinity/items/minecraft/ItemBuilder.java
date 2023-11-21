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
