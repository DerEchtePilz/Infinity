package io.github.derechtepilz.infinity.data;

import io.github.derechtepilz.infinity.Infinity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class InfinityData extends Data implements GamemodeData {

	@Override
	public Map<UUID, String> getInventoryData() {
		return Collections.unmodifiableMap(inventoryData);
	}

	@Override
	public Map<UUID, String> getExperienceData() {
		return Collections.unmodifiableMap(experienceData);
	}

	@Override
	public Map<UUID, String> getHealthHungerData() {
		return Collections.unmodifiableMap(healthHungerData);
	}

	@Override
	public Map<UUID, String> getPotionEffectData() {
		return Collections.unmodifiableMap(potionEffectData);
	}

	@Override
	public void setInventoryData(UUID player, String data) {
		inventoryData.put(player, data);
	}

	@Override
	public void setExperienceData(UUID player, String data) {
		experienceData.put(player, data);
	}

	@Override
	public void setHealthHungerData(UUID player, String data) {
		healthHungerData.put(player, data);
	}

	@Override
	public void setPotionEffectData(UUID player, String data) {
		potionEffectData.put(player, data);
	}

	@Override
	public MinecraftData getOtherData() {
		return Infinity.getInstance().getMinecraftData();
	}

	@Override
	public BufferedWriter getWriter() {
		return super.getWriter("infinity-data");
	}

	@Override
	public BufferedReader getReader() {
		return super.getReader("infinity-data");
	}

}
