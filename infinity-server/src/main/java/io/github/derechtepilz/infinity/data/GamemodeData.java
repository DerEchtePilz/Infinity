package io.github.derechtepilz.infinity.data;

import java.util.Map;
import java.util.UUID;

public interface GamemodeData {

	Map<UUID, String> getInventoryData();

	Map<UUID, String> getExperienceData();

	Map<UUID, String> getHealthHungerData();

	Map<UUID, String> getPotionEffectData();

	void setInventoryData(UUID player, String data);

	void setExperienceData(UUID player, String data);

	void setHealthHungerData(UUID player, String data);

	void setPotionEffectData(UUID player, String data);

	GamemodeData getOtherData();


}
