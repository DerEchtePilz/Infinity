package io.github.derechtepilz.infinity.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfinityAxe extends InfinityItem {

    public static final String ITEM_ID = "infinity_axe";
    public static final String ITEM_NAME = "Infinity Axe";
    public static final int VARIATIONS = 2;

    public InfinityAxe(Rarity rarity, int variationId) {
        super(Material.matchMaterial(rarity.asTool().asAxe()), rarity, variationId);
        if (variationId <= -1) {
            throw new IllegalArgumentException("Variation ID too low (must be 0 or 1)");
        }
        if (variationId >= 2) {
            throw new IllegalArgumentException("Variation ID too high (must be 0 or 1)");
        }
    }

    @Override
    public @NotNull Component displayName() {
        return Component.text().content(ITEM_NAME).color(rarity.color()).decoration(TextDecoration.ITALIC, false).build();
    }

    @Override
    public void applyMeta() {
        ItemMeta meta = getItemMeta();
        meta.setUnbreakable(true);
        setItemMeta(meta);
    }

    @Override
    public List<Component> lore() {
        List<Component> lore = new ArrayList<>();
        switch (variationId) {
            case 0 -> {
                lore.add(Component.text("Melon Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                );
                lore.add(Component.empty());
                lore.add(Component.text("(Farming Axe)")
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                );
            }
            case 1 -> {
                lore.add(Component.text("Oak Log Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                );
                lore.add(Component.text("Spruce Log Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                );
                lore.add(Component.text("Warped Stem Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                );
                lore.add(Component.text("Crimson Stem Drops: ")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(rarity.asTool().modifier())
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                    )
                );
                lore.add(Component.empty());
                lore.add(Component.text("(Foraging Axe)")
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                );
            }
        }
        lore.add(rarity.rarityString());
        return lore;
    }

    @Override
    public String getId() {
        return ITEM_ID;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void applyType() {
        setType(Material.matchMaterial(rarity.asTool().asAxe()));
    }

    @Override
    public boolean isUpgradable() {
        return true;
    }

    @Override
    public @NotNull InfinityItem clone() {
        InfinityAxe infinityAxe = new InfinityAxe(this.rarity, this.variationId);
        infinityAxe.setItemMeta(this.getItemMeta());
        return infinityAxe;
    }

}
