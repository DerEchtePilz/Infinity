package io.github.derechtepilz.infinity.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

class InfinityAxe(rarity: Rarity, variationId: Int) :
    InfinityItem(Material.matchMaterial(rarity.asTool().asAxe())!!, rarity, variationId) {

    companion object {
        const val ITEM_ID = "infinity_axe"
        const val ITEM_NAME = "Infinity Axe"
        const val VARIATIONS = 2
    }

    init {
        if (variationId <= -1) {
            throw IllegalArgumentException("Variation ID too low (must be 0 or 1)")
        }
        if (variationId >= 2) {
            throw IllegalArgumentException("Variation ID too high (must be 0 or 1)")
        }
    }

    override fun displayName(): Component = Component.text(ITEM_NAME).color(rarity.color()).decoration(TextDecoration.ITALIC, false)

    override fun applyMeta() {
        val meta = itemMeta
        meta.isUnbreakable = true
        itemMeta = meta
    }

    override fun lore(): List<Component> {
        val lore: MutableList<Component> = mutableListOf()
        when (variationId) {
            0 -> {
                lore.add(Component.text("Melon Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                )
                lore.add(Component.empty())
                lore.add(Component.text("(Farming Axe)")
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                )
            }

            1 -> {
                lore.add(Component.text("Oak Log Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                )
                lore.add(Component.text("Spruce Log Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                )
                lore.add(Component.text("Warped Stem Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                )
                lore.add(Component.text("Crimson Stem Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                )
                lore.add(Component.empty())
                lore.add(Component.text("(Foraging Axe)")
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                )
            }
        }
        lore.add(rarity.rarityString())
        return lore.toList()
    }

    override fun getId(): String = ITEM_ID

    override fun applyType() {
        type = Material.matchMaterial(rarity.asTool().asAxe())!!
    }

    override fun isUpgradable(): Boolean = true

    override fun clone(): InfinityItem {
        val infinityAxe = InfinityAxe(this.rarity, this.variationId)
        infinityAxe.itemMeta = this.itemMeta
        return infinityAxe
    }
}