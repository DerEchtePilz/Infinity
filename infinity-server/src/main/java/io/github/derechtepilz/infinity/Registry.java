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

package io.github.derechtepilz.infinity;

import io.github.derechtepilz.infinity.items.InfinityItem;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Registry {

	public static class Item {

		private static final Set<String> registeredItemIds = new HashSet<>();
		private static final Map<String, InfinityItem> ITEM_REGISTRY = new HashMap<>();
		private static final Map<String, Set<InfinityItem>> MULTI_ITEM_REGISTRY = new HashMap<>();

		private Item() {
		}

		public static void register(String itemId, InfinityItem infinityItem) {
			if (registeredItemIds.contains(itemId)) {
				Set<InfinityItem> registeredVariations = (MULTI_ITEM_REGISTRY.containsKey(itemId)) ? MULTI_ITEM_REGISTRY.get(itemId) : new HashSet<>();
				if (ITEM_REGISTRY.containsKey(itemId)) {
					// Potentially should be added to the MULTI_ITEM_REGISTRY
					InfinityItem item = ITEM_REGISTRY.get(itemId);
					if (infinityItem.equals(item)) {
						// Item has already been registered, do nothing
						return;
					}
					// Remove the item from the ITEM_REGISTRY
					ITEM_REGISTRY.remove(itemId);

					// Register the item from the ITEM_REGISTRY
					registeredVariations.add(item);
				}
				// Register the item in the MULTI_ITEM_REGISTRY
				registeredVariations.add(infinityItem);
				MULTI_ITEM_REGISTRY.put(itemId, registeredVariations);
				return;
			}
			ITEM_REGISTRY.put(itemId, infinityItem);
			registeredItemIds.add(itemId);
		}

		private static InfinityItem getItem(String itemId) {
			return ITEM_REGISTRY.get(itemId).clone();
		}

		/**
		 * Returns an item with the given id and the given variation id
		 * <p>
		 * This item can be safely modified
		 */
		@SuppressWarnings("DataFlowIssue")
		@Nullable
		public static InfinityItem getItem(String itemId, int variation) {
			if (!MULTI_ITEM_REGISTRY.containsKey(itemId)) {
				return getItem(itemId);
			}
			InfinityItem item = null;
			for (String id : MULTI_ITEM_REGISTRY.keySet()) {
				if (itemId.equals(id)) {
					Set<InfinityItem> set = MULTI_ITEM_REGISTRY.get(itemId);
					for (InfinityItem infinityItem : set) {
						ItemMeta meta = infinityItem.getItemMeta();
						int variationId = meta.getPersistentDataContainer().get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER);
						if (variationId == variation) {
							item = infinityItem;
						}
					}
				}
			}
			return (item != null) ? item.clone() : null;
		}

		public static String[] getItemIds() {
			Set<String> itemIds = new HashSet<>();
			itemIds.addAll(ITEM_REGISTRY.keySet());
			itemIds.addAll(MULTI_ITEM_REGISTRY.keySet());
			return itemIds.toArray(new String[0]);
		}

		public static InfinityItem[] getAllItems() {
			List<InfinityItem> infinityItems = new ArrayList<>();
			ITEM_REGISTRY.keySet().forEach(itemId -> infinityItems.add(ITEM_REGISTRY.get(itemId)));
			MULTI_ITEM_REGISTRY.keySet().forEach(itemId -> infinityItems.addAll(MULTI_ITEM_REGISTRY.get(itemId)));
			return infinityItems.toArray(new InfinityItem[0]);
		}

	}


}
