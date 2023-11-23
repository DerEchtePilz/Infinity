package io.github.derechtepilz.infinity.gamemode.modification;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.github.derechtepilz.infinity.util.Reflection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TablistHandler implements Listener {

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Gamemode previousGamemode = Gamemode.getFromKey(event.getFrom().getKey());
		Gamemode currentGamemode = Gamemode.getFromKey(event.getPlayer().getWorld().getKey());
		if (previousGamemode == currentGamemode) {
			return;
		}
		Player player = event.getPlayer();
		if (currentGamemode == Gamemode.INFINITY) {
			Infinity.getInstance().getInfinityPlayerList().add(player.getUniqueId());
			Infinity.getInstance().getMinecraftPlayerList().remove(player.getUniqueId());
			updateTabList();
			return;
		}
		if (currentGamemode == Gamemode.MINECRAFT) {
			Infinity.getInstance().getMinecraftPlayerList().add(player.getUniqueId());
			Infinity.getInstance().getInfinityPlayerList().remove(player.getUniqueId());
			updateTabList();
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (PlayerUtil.getGamemode(player) == Gamemode.INFINITY) {
			Infinity.getInstance().getInfinityPlayerList().add(player.getUniqueId());
			Infinity.getInstance().getMinecraftPlayerList().remove(player.getUniqueId());
		} else {
			Infinity.getInstance().getMinecraftPlayerList().add(player.getUniqueId());
			Infinity.getInstance().getInfinityPlayerList().remove(player.getUniqueId());
		}
		updateTabList();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Infinity.getInstance().getInfinityPlayerList().remove(player.getUniqueId());
		Infinity.getInstance().getMinecraftPlayerList().remove(player.getUniqueId());

		updateTabList();
	}

	private void updateTabList() {
		Bukkit.getScheduler().runTaskLater(Infinity.getInstance(), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				List<Packet<?>> packetsToSend = new ArrayList<>();
				if (PlayerUtil.getGamemode(player) == Gamemode.INFINITY) {
					for (UUID uuid : Infinity.getInstance().getInfinityPlayerList()) {
						ClientboundPlayerInfoUpdatePacket addPlayerToInfo = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), true);
						packetsToSend.add(addPlayerToInfo);
					}
					for (UUID uuid : Infinity.getInstance().getMinecraftPlayerList()) {
						ClientboundPlayerInfoUpdatePacket removePlayerFromInfo = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), false);
						packetsToSend.add(removePlayerFromInfo);
					}
				} else {
					for (UUID uuid : Infinity.getInstance().getMinecraftPlayerList()) {
						ClientboundPlayerInfoUpdatePacket addPlayerToInfo = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), true);
						packetsToSend.add(addPlayerToInfo);
					}
					for (UUID uuid : Infinity.getInstance().getInfinityPlayerList()) {
						ClientboundPlayerInfoUpdatePacket removePlayerFromInfo = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(Reflection.getServerPlayer(uuid), false);
						packetsToSend.add(removePlayerFromInfo);
					}
				}
				for (Packet<?> packet : packetsToSend) {
					Reflection.getServerPlayer(player.getUniqueId()).connection.send(packet);
				}
			}
		}, 20);
	}

}
