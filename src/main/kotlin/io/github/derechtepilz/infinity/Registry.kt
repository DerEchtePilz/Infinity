package io.github.derechtepilz.infinity

import io.github.derechtepilz.infinity.items.InfinityItem
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.Nullable

class Registry {

    object Item {

        private val registeredItemIds: MutableSet<String> = mutableSetOf()
        private val ITEM_REGISTRY: MutableMap<String, InfinityItem> = mutableMapOf()
        private val MULTI_ITEM_REGISTRY: MutableMap<String, MutableSet<InfinityItem>> = mutableMapOf()

        fun register(itemId: String, infinityItem: InfinityItem) {
            if (registeredItemIds.contains(itemId)) {
                val registeredVariations = if (MULTI_ITEM_REGISTRY.containsKey(itemId)) MULTI_ITEM_REGISTRY[itemId]!! else mutableSetOf()
                if (ITEM_REGISTRY.containsKey(itemId)) {
                    // Potentially should be added to the MULTI_ITEM_REGISTRY
                    val item = ITEM_REGISTRY[itemId]!!
                    if (infinityItem == item) {
                        // Item has already been registered, do nothing
                        return
                    }
                    // Remove item from the ITEM_REGISTRY
                    ITEM_REGISTRY.remove(itemId)

                    // Register the item from the ITEM_REGISTRY
                    registeredVariations.add(item)
                }

                // Register the item in the MULTI_ITEM_REGISTRY
                registeredVariations.add(infinityItem)
                MULTI_ITEM_REGISTRY[itemId] = registeredVariations
                return
            }
            ITEM_REGISTRY[itemId] = infinityItem
            registeredItemIds.add(itemId)
        }

        private fun getItem(itemId: String): InfinityItem {
            return ITEM_REGISTRY[itemId]!!.clone()
        }

        /**
         * Returns an item with the given id and the given variation id
         *
         * This item can be safely modified
         */
        @Nullable
        fun getItem(itemId: String, variation: Int): InfinityItem? {
            if (!MULTI_ITEM_REGISTRY.containsKey(itemId)) {
                return getItem(itemId)
            }
            var item: InfinityItem? = null
            MULTI_ITEM_REGISTRY.keys.forEach { id ->
                if (itemId == id) {
                    val set: MutableSet<InfinityItem> = MULTI_ITEM_REGISTRY[itemId]!!
                    set.forEach { infinityItem ->
                        val meta = infinityItem.itemMeta
                        val variationId: Int = meta.persistentDataContainer[InfinityItem.VARIATION_ID, PersistentDataType.INTEGER]!!
                        if (variationId == variation) {
                            item = infinityItem
                        }
                    }
                }
            }
            return item?.clone()
        }

        fun getItemIds(): Array<String> {
            val itemIds: MutableSet<String> = mutableSetOf()
            itemIds.addAll(ITEM_REGISTRY.keys)
            itemIds.addAll(MULTI_ITEM_REGISTRY.keys)
            return itemIds.toTypedArray().clone()
        }

        fun getAllItems(): Array<InfinityItem> {
            val infinityItems: MutableList<InfinityItem> = mutableListOf()
            ITEM_REGISTRY.keys.forEach { itemId -> infinityItems.add(ITEM_REGISTRY[itemId]!!) }
            MULTI_ITEM_REGISTRY.keys.forEach { itemId -> infinityItems.addAll(MULTI_ITEM_REGISTRY[itemId]!!) }
            return infinityItems.toTypedArray().clone()
        }

    }

}