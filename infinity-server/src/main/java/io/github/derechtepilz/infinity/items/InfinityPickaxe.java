package io.github.derechtepilz.infinity.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfinityPickaxe extends InfinityItem {

    public static final String ITEM_ID = "infinity_pickaxe";
    public static final String ITEM_NAME = "Infinity Pickaxe";

    public InfinityPickaxe(Rarity rarity) {
        super(Material.matchMaterial(rarity.asTool().asPickaxe()), rarity);
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
        lore.add(Component.text("Cobblestone Drops: ")
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false)
            .append(Component.text(rarity.asTool().modifier())
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false)
            )
        );
        lore.add(Component.text("Basalt Drops: ")
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false)
            .append(Component.text(rarity.asTool().modifier())
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false)
            )
        );
        lore.add(Component.empty());
        lore.add(rarity.rarityString());
        return lore;
    }

    @Override
    public String getId() {
        return ITEM_ID;
    }

    @Override
    public void applyType() {
        setType(Material.matchMaterial(rarity.asTool().asPickaxe()));
    }

    @Override
    public boolean isUpgradable() {
        return true;
    }

    @Override
    public @NotNull InfinityPickaxe clone() {
        InfinityPickaxe infinityPickaxe = new InfinityPickaxe(rarity);
        infinityPickaxe.setItemMeta(this.getItemMeta());
        return infinityPickaxe;
    }

}
