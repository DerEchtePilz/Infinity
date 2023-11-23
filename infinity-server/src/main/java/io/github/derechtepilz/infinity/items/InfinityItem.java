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

package io.github.derechtepilz.infinity.items;

import io.github.derechtepilz.infinity.Infinity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class InfinityItem extends ItemStack {

	public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(Infinity.NAME, "item_id");
	public static final NamespacedKey RARITY_KEY = new NamespacedKey(Infinity.NAME, "rarity");
	public static final NamespacedKey VARIATION_ID = new NamespacedKey(Infinity.NAME, "variation");

	protected Rarity rarity;
	protected final int variationId;

	public InfinityItem(Material material, Rarity rarity) {
		this(material, rarity, -1);
	}

	public InfinityItem(Material material, Rarity rarity, int variationId) {
		super(material);
		this.rarity = rarity;
		this.variationId = variationId;
	}

	private void applyCommonMeta() {
		ItemMeta meta = getItemMeta();
		meta.displayName(displayName());
		meta.lore(lore());
		meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, getId());
		meta.getPersistentDataContainer().set(RARITY_KEY, PersistentDataType.STRING, rarity.rarityId());
		meta.getPersistentDataContainer().set(VARIATION_ID, PersistentDataType.INTEGER, variationId);
		setItemMeta(meta);
	}

	public InfinityItem updateRarityTo(Rarity newRarity) {
		rarity = newRarity;
		applyType();
		applyCommonMeta();
		applyMeta();
		return this;
	}

	public InfinityItem upgradeItem(Rarity currentRarity) {
		rarity = currentRarity.nextRarity();
		applyType();
		applyCommonMeta();
		applyMeta();
		return this;
	}

	public InfinityItem downgradeItem(Rarity currentRarity) {
		rarity = currentRarity.previousRarity();
		applyType();
		applyCommonMeta();
		applyMeta();
		return this;
	}

	@Override
	public abstract @NotNull Component displayName();

	public abstract void applyMeta();

	@Override
	public abstract List<Component> lore();

	public abstract String getId();

	/**
	 * Only used for updating the item
	 * <p>
	 * May also be used to apply different types for items with variation id's greater than 0
	 */
	public abstract void applyType();

	public abstract boolean isUpgradable();

	@Override
	public abstract @NotNull InfinityItem clone();

}
