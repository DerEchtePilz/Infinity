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

package io.github.derechtepilz.infinity.util;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Reflection {

	private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

	private Reflection() {}

	public static ServerPlayer getServerPlayer(UUID uuid) {
		try {
			return (ServerPlayer) Class.forName(getCraftBukkitClass("entity.CraftPlayer")).getMethod("getHandle").invoke(Bukkit.getPlayer(uuid));
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
	}

	public static DedicatedServer getDedicatedServer() {
		try {
			return (DedicatedServer) Class.forName(getCraftBukkitClass("CraftServer")).getDeclaredMethod("getServer").invoke(Bukkit.getServer());
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
    }

	private static String getCraftBukkitClass(String name) {
		return CRAFTBUKKIT_PACKAGE + "." + name;
	}

}
