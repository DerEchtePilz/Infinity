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

package io.github.derechtepilz.infinity.world;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener;
import io.github.derechtepilz.infinity.structure.StructureLoader;
import io.github.derechtepilz.infinity.util.Keys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class WorldCarver {

	public static class LobbyCarver {

		public LobbyCarver(World world) {
			if (!(world.key().equals(Keys.WORLD_LOBBY.get()))) {
				throw new IllegalArgumentException("World 'infinity:lobby' expected but received '" + world.key() + "'");
			}
			// Load structure if there's no block at 0 100 0
			boolean canLoadStructure = world.getBlockAt(0, 100, 0).getType() == Material.AIR;
			if (canLoadStructure) {
				new StructureLoader(world.getKey().getKey(), Infinity.getInstance().getResource("lobby/lobby_spawn.json"));
			}

			/*
			 *  8 101 9 -> Travel to Minecraft
			 *  6 101 9 -> Travel to Home dimension
			 * -5 101 9 -> Class selection info
			 * -6 101 9 -> Select first class
			 * -7 101 9 -> Switch class warning
			 * -8 101 9 -> Switch class
			 */
			// Place signs
			Block travelToMinecraft = world.getBlockAt(8, 101, 9);
			travelToMinecraft.setType(Material.CHERRY_WALL_SIGN);
			Sign travelToMinecraftState = (Sign) travelToMinecraft.getState();
			travelToMinecraftState.getPersistentDataContainer().set(Keys.SIGN_TAG_MINECRAFT_TELEPORT.get(), PersistentDataType.STRING, "minecraftTeleport");
			travelToMinecraftState.update();

			Block travelToHomeDimension = world.getBlockAt(6, 101, 9);
			travelToHomeDimension.setType(Material.CHERRY_WALL_SIGN);
			Sign travelToHomeDimensionState = (Sign) travelToHomeDimension.getState();
			travelToHomeDimensionState.getPersistentDataContainer().set(Keys.SIGN_TAG_HOME_DIMENSION_TELEPORT.get(), PersistentDataType.STRING, "homeDimensionTeleport");
			travelToHomeDimensionState.update();

			Block classSelectionInfo = world.getBlockAt(-5, 101, 9);
			classSelectionInfo.setType(Material.CHERRY_WALL_SIGN);

			Block selectFirstClass = world.getBlockAt(-6, 101, 9);
			selectFirstClass.setType(Material.CHERRY_WALL_SIGN);
			Sign selectFirstClassState = (Sign) selectFirstClass.getState();
			selectFirstClassState.getPersistentDataContainer().set(Keys.SIGN_TAG_SELECT_CLASS.get(), PersistentDataType.STRING, "selectClass");
			selectFirstClassState.update();

			Block switchClassWarning = world.getBlockAt(-7, 101, 9);
			switchClassWarning.setType(Material.CHERRY_WALL_SIGN);

			Block switchClass = world.getBlockAt(-8, 101, 9);
			switchClass.setType(Material.CHERRY_WALL_SIGN);
			Sign switchClassState = (Sign) switchClass.getState();
			switchClassState.getPersistentDataContainer().set(Keys.SIGN_TAG_SWITCH_CLASS.get(), PersistentDataType.STRING, "switchClass");
			switchClassState.update();

			applyText(travelToMinecraft.getState(), new Component[]{
					Component.empty(),
					Component.text("Travel to:"),
					Component.text("Minecraft").color(NamedTextColor.GREEN),
					Component.empty()
				}
			);

			applyText(travelToHomeDimension.getState(), new Component[]{
					Component.empty(),
					Component.text().content("Travel to:").build(),
					Component.text().content("[Home]").build(),
					Component.empty()
				}
			);

			applyText(classSelectionInfo.getState(), new Component[]{
					Component.text().content("Class selection:").decorate(TextDecoration.UNDERLINED).build(),
					Component.text().content("Select:")
						.append(Component.text().content(" "))
						.append(Component.text().content("left-click").color(NamedTextColor.GREEN))
						.build(),
					Component.text().content("Cycle:")
						.append(Component.text().content(" "))
						.append(Component.text().content("right-click").color(NamedTextColor.GREEN))
						.build(),
					Component.empty()
				}
			);

			applyText(selectFirstClass.getState(), new Component[]{
					Component.text().content("Select class:").decorate(TextDecoration.UNDERLINED).build(),
					Component.empty(),
					Component.text().content("[Select class...]").color(NamedTextColor.GREEN).build(),
					Component.empty()
				}
			);

			applyText(switchClassWarning.getState(), new Component[]{
					Component.text().content("!! Warning !!").color(NamedTextColor.DARK_RED).decorate(TextDecoration.UNDERLINED).build(),
					Component.text().content("Switching class").build(),
					Component.text().content("will reset your").build(),
					Component.text().content("profile!!").build()
				}
			);

			applyText(switchClass.getState(), new Component[]{
					Component.text().content("Switch class:").decorate(TextDecoration.UNDERLINED).build(),
					Component.empty(),
					Component.text().content("[Select class...]").color(NamedTextColor.GREEN).build(),
					Component.empty()
				}
			);

		}

		public static void setupPlayerSignWithDelay(Player player) {
			SignListener.INSTANCE.getHomeDimension().get(player.getUniqueId()).loadFor(player, true);
			SignListener.INSTANCE.getClassSelection().get(player.getUniqueId()).loadFor(player, true);
			SignListener.INSTANCE.getSwitchClassSelection().get(player.getUniqueId()).loadFor(player, true);
		}

		private void applyText(BlockState sign, Component[] content) {
			Sign signState = (Sign) sign;
			for (int i = 0; i < content.length; i++) {
				signState.getSide(Side.FRONT).line(i, content[i]);
			}
			signState.update();
		}

	}

	public static class SkyCarver {

		public SkyCarver(World world) {
			if (!(world.key().equals(Keys.WORLD_SKY.get()))) {
				throw new IllegalArgumentException("World 'infinity:sky' expected but received '" + world.key() + "'");
			}
			// Load structure if there's no block at 0 100 0
			boolean canLoadStructure = world.getBlockAt(0, 100, 0).getType() == Material.AIR;
			if (canLoadStructure) {
				new StructureLoader(world.getKey().getKey(), Infinity.getInstance().getResource("sky/sky_spawn.json"));
			}
		}

	}

	public static class StoneCarver {

		public StoneCarver(World world) {
			if (!(world.key().equals(Keys.WORLD_STONE.get()))) {
				throw new IllegalArgumentException("World 'infinity:stone' expected but received '${world.key}'");
			}
			// Load structure if there's a block at 0 101 0
			boolean canLoadStructure = world.getBlockAt(0, 101, 0).getType() != Material.AIR;
			if (canLoadStructure) {
				new StructureLoader(world.getKey().getKey(), Infinity.getInstance().getResource("stone/stone_spawn.json"));
			}
		}

	}

	public static class NetherCarver {

		public NetherCarver(World world) {
			if (!(world.key().equals(Keys.WORLD_NETHER.get()))) {
				throw new IllegalArgumentException("World 'infinity:nether' expected but received '${world.key}'");
			}
			// Load structure if there's lava at 0 100 0
			boolean canLoadStructure = world.getBlockAt(0, 101, 0).getType() != Material.LAVA;
			if (canLoadStructure) {
				new StructureLoader(world.getKey().getKey(), Infinity.getInstance().getResource("nether/nether_spawn.json"));
			}
		}

	}

}
