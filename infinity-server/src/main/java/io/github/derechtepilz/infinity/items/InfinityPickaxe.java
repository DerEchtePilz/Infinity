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
