package io.github.derechtepilz.infinity.backup;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.data.GamemodeData;
import io.github.derechtepilz.infinity.data.InfinityData;
import io.github.derechtepilz.infinity.data.MinecraftData;
import io.github.derechtepilz.infinity.data.PlayerData;
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerDataHandler {

	private final MinecraftData backupMinecraftData = new MinecraftData();
	private final InfinityData backupInfinityData = new InfinityData();
	private final PlayerData backupPlayerData = new PlayerData();

	public PlayerDataHandler() {
		copyData(Infinity.getInstance().getMinecraftData(), backupMinecraftData);
		copyData(Infinity.getInstance().getInfinityData(), backupInfinityData);
	}

	public void createBackup() {
		updateGamemodeData(Infinity.getInstance().getMinecraftPlayerList(), backupMinecraftData);
		updateGamemodeData(Infinity.getInstance().getInfinityPlayerList(), backupInfinityData);

		backupMinecraftData.saveData(backupMinecraftData.getWriter("backup"));
		backupInfinityData.saveData(backupInfinityData.getWriter("backup"));
		backupPlayerData.saveData(backupPlayerData.getWriter("backup"));
	}

	private void updateGamemodeData(List<UUID> playerList, GamemodeData backupData) {
		for (UUID uuid : playerList) {
			if (Bukkit.getPlayer(uuid) == null) continue;
			Player player = Bukkit.getPlayer(uuid);
			backupData.setInventoryData(uuid, InventorySerializer.serialize(player));
			backupData.setExperienceData(uuid, ExperienceSerializer.serialize(player));
			backupData.setHealthHungerData(uuid, HealthHungerSerializer.serialize(player));
			backupData.setPotionEffectData(uuid, EffectSerializer.serialize(player));
		}
	}

	private void copyData(GamemodeData data, GamemodeData toData) {
		for (UUID uuid : data.getInventoryData().keySet()) {
			toData.setInventoryData(uuid, data.getInventoryData().get(uuid));
		}
		for (UUID uuid : data.getExperienceData().keySet()) {
			toData.setExperienceData(uuid, data.getExperienceData().get(uuid));
		}
		for (UUID uuid : data.getHealthHungerData().keySet()) {
			toData.setInventoryData(uuid, data.getHealthHungerData().get(uuid));
		}
		for (UUID uuid : data.getPotionEffectData().keySet()) {
			toData.setPotionEffectData(uuid, data.getPotionEffectData().get(uuid));
		}
	}

}
