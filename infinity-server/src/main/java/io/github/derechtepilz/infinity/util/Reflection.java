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
			return (DedicatedServer) Class.forName(getCraftBukkitClass("CraftServer")).cast(Bukkit.getServer());
		} catch (ClassNotFoundException e) {
			return null;
		}
    }

	private static String getCraftBukkitClass(String name) {
		return CRAFTBUKKIT_PACKAGE + "." + name;
	}

}
