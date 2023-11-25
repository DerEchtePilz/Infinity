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

package io.github.derechtepilz.infinity.gamemode.gameclass;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.github.derechtepilz.infinity.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;

public class SignState {

	public enum HomeDimensionState implements State<HomeDimensionState> {

		UNSET("01"),
		SKY("02"),
		STONE("03"),
		NETHER("04");

		private final String value;
		private final Location homeTeleportLocation = new Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get()), 6.0, 101.0, 9.0);

		HomeDimensionState(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public void loadFor(Player player, boolean delay) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity.getInstance(), () -> {
				loadFor(player);
			}, (delay) ? 10 : 0);
		}

		@Override
		public HomeDimensionState getNext() {
			throw new UnsupportedOperationException("HomeDimensionState does not support cycling through dimensions!");
		}

		@Override
		public TextComponent asString() {
			return Component.text().content(StringUtil.normalize(this)).build();
		}

		public void loadFor(Player player) {
			Sign signState = (Sign) Material.CHERRY_WALL_SIGN.createBlockData().createBlockState();
			SignSide side = signState.getSide(Side.FRONT);
			side.line(0, Component.empty());
			side.line(1, Component.text("Travel to:"));
			switch (this) {
				case UNSET ->
					side.line(2, Component.text().content("Home unknown").color(NamedTextColor.DARK_RED).build());
				case SKY, STONE, NETHER -> side.line(2, GameClass.Dimension.valueOf(this.name()).get());
			}
			side.line(3, Component.empty());
			player.sendBlockUpdate(homeTeleportLocation, signState);
		}

		public static void loadState(Player player, HomeDimensionState state) {
			state.loadFor(player, true);
		}

		public static HomeDimensionState getByValue(String value) {
			if (value.equals(UNSET.value)) return UNSET;
			if (value.equals(SKY.value)) return SKY;
			if (value.equals(STONE.value)) return STONE;
			return NETHER;
		}

	}

	public enum ClassSelectionState implements State<ClassSelectionState> {

		NO_CLASS_SELECTED("01"),
		AIRBORN("02"),
		STONEBORN("03"),
		LAVABORN("04"),
		CLASS_SELECTED("05");

		private String value;
		private final Location classSelectionLocation = new Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get()), -6.0, 101.0, 9.0);

		ClassSelectionState(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public void loadFor(Player player, boolean delay) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity.getInstance(), () -> {
				loadFor(player);
			}, (delay) ? 10 : 0);
		}

		@Override
		public ClassSelectionState getNext() {
			return switch (this) {
				case NO_CLASS_SELECTED, LAVABORN -> AIRBORN;
				case AIRBORN -> STONEBORN;
				case STONEBORN -> LAVABORN;
				case CLASS_SELECTED -> CLASS_SELECTED;
			};
		}

		@Override
		public TextComponent asString() {
			return GameClass.valueOf(this.name()).get();
		}

		public void loadFor(Player player) {
			Sign signState = (Sign) Material.CHERRY_WALL_SIGN.createBlockData().createBlockState();
			SignSide side = signState.getSide(Side.FRONT);
			switch (this) {
				case NO_CLASS_SELECTED, AIRBORN, STONEBORN, LAVABORN -> {
					side.line(0, Component.text().content("Select class:").decoration(TextDecoration.UNDERLINED, true).build());
					side.line(1, Component.empty());
					side.line(2, GameClass.valueOf(this.name()).get());
				}
				case CLASS_SELECTED -> {
					side.line(0, Component.empty());
					side.line(1, Component.text().content("Class selected:").color(NamedTextColor.GOLD).build());
					side.line(2, PlayerUtil.getGameClass(player));
				}
			}
			side.line(3, Component.empty());
			player.sendBlockUpdate(classSelectionLocation, signState);
		}

		public static void loadState(Player player, ClassSelectionState state) {
			state.loadFor(player, true);
		}

		public static ClassSelectionState getByValue(String value) {
			if (value.equals(NO_CLASS_SELECTED.value)) return NO_CLASS_SELECTED;
			if (value.equals(AIRBORN.value)) return AIRBORN;
			if (value.equals(STONEBORN.value)) return STONEBORN;
			if (value.equals(LAVABORN.value)) return LAVABORN;
			return CLASS_SELECTED;
		}

	}

	public enum ClassSwitchingState implements State<ClassSwitchingState> {

		NO_CLASS_SELECTED("01"),
		AIRBORN("02"),
		STONEBORN("03"),
		LAVABORN("04");

		private final String value;
		private final Location classSwitchingLocation = new Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get()), -8.0, 101.0, 9.0);

		ClassSwitchingState(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public void loadFor(Player player, boolean delay) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Infinity.getInstance(), () -> {
				loadFor(player);
			}, (delay) ? 10 : 0);
		}

		@Override
		public ClassSwitchingState getNext() {
			return switch (this) {
				case NO_CLASS_SELECTED, LAVABORN -> AIRBORN;
				case AIRBORN -> STONEBORN;
				case STONEBORN -> LAVABORN;
			};
		}

		@Override
		public TextComponent asString() {
			return GameClass.valueOf(this.name()).get();
		}

		public void loadFor(Player player) {
			Sign signState = (Sign) Material.CHERRY_WALL_SIGN.createBlockData().createBlockState();
			SignSide side = signState.getSide(Side.FRONT);
			switch (this) {
				case NO_CLASS_SELECTED, AIRBORN, STONEBORN, LAVABORN -> {
					side.line(0, Component.text().content("Switch class:").decoration(TextDecoration.UNDERLINED, true).build());
					side.line(1, Component.empty());
					side.line(2, GameClass.valueOf(this.name()).get());
					side.line(3, Component.empty());
				}
			}
			player.sendBlockUpdate(classSwitchingLocation, signState);
		}

		public static void loadState(Player player, ClassSwitchingState state) {
			state.loadFor(player, true);
		}

		public static ClassSwitchingState getByValue(String value) {
			if (value.equals(NO_CLASS_SELECTED.value)) return NO_CLASS_SELECTED;
			if (value.equals(AIRBORN.value)) return AIRBORN;
			if (value.equals(STONEBORN.value)) return STONEBORN;
			return LAVABORN;
		}

	}

	public interface State<T> {

		default void loadFor(Player player) {
			loadFor(player, false);
		}

		void loadFor(Player player, boolean delay);

		T getNext();

		TextComponent asString();

	}

}
