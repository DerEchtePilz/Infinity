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

package io.github.derechtepilz.infinity.util

import io.github.derechtepilz.infinity.Infinity0
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

enum class Keys(private val namespace: NamespacedKey, val isState: Boolean = false) {

	/* GENERAL KEYS START */
	WORLD_LOBBY(NamespacedKey(Infinity0.NAME, "lobby")),
	WORLD_SKY(NamespacedKey(Infinity0.NAME, "sky")),
	WORLD_STONE(NamespacedKey(Infinity0.NAME, "stone")),
	WORLD_NETHER(NamespacedKey(Infinity0.NAME, "nether")),
	DEFAULT_GAMEMODE(NamespacedKey(Infinity0.NAME, "default_gamemode")),
	/* GENERAL KEYS END */

	/*
	 * GAME STATE KEYS START
	 *
	 * These keys are dynamically added and/or removed during server runtime.
	 * They may represent a permission being added to a player or enable a player to use or see specific items in menus and/or the game
	 */
	STORY_STARTED(NamespacedKey(Infinity0.NAME, "story_started"), true),
	INTRODUCTION_SEQUENCE(NamespacedKey(Infinity0.NAME, "introduction_sequence"), true),
	INTRODUCTION_SEQUENCE_COMPLETED(NamespacedKey(Infinity0.NAME, "introduction_sequence_completed"), true),
	GAMEMODE_SWITCH_ENABLED(NamespacedKey(Infinity0.NAME, "gamemode_switch_enabled"), true),
	/* GAME STATE KEYS END */

	/* SWITCH GAMEMODE KEYS START */
	SWITCH_GAMEMODE_LAST_WORLD(NamespacedKey(Infinity0.NAME, "sg_last_world_key")),
	SWITCH_GAMEMODE_LAST_X(NamespacedKey(Infinity0.NAME, "sg_last_pos_x")),
	SWITCH_GAMEMODE_LAST_Y(NamespacedKey(Infinity0.NAME, "sg_last_pos_y")),
	SWITCH_GAMEMODE_LAST_Z(NamespacedKey(Infinity0.NAME, "sg_last_pos_z")),
	SWITCH_GAMEMODE_LAST_YAW(NamespacedKey(Infinity0.NAME, "sg_last_yaw")),
	SWITCH_GAMEMODE_LAST_PITCH(NamespacedKey(Infinity0.NAME, "sg_last_pitch")),
	/* SWITCH GAMEMODE KEYS START */

	/* SIGN RELATED KEYS START */
	SIGN_TAG_MINECRAFT_TELEPORT(NamespacedKey(Infinity0.NAME, "st_teleport_to_minecraft")),
	SIGN_TAG_HOME_DIMENSION_TELEPORT(NamespacedKey(Infinity0.NAME, "st_teleport_to_home")),
	SIGN_TAG_SELECT_CLASS(NamespacedKey(Infinity0.NAME, "st_select_class")),
	SIGN_TAG_SWITCH_CLASS(NamespacedKey(Infinity0.NAME, "st_switch_class")),
	SIGN_STATE_HOME_DIMENSION(NamespacedKey(Infinity0.NAME, "ss_home_dimension")),
	SIGN_STATE_SELECT_CLASS(NamespacedKey(Infinity0.NAME, "ss_select_class")),
	SIGN_STATE_SWITCH_CLASS(NamespacedKey(Infinity0.NAME, "ss_switch_class")),
	/* SIGN RELATED KEYS START */

	/* DEATH AND RESPAWN KEYS START */
	DEATH_RESPAWN_MC_WORLD(NamespacedKey(Infinity0.NAME, "mc_respawn_world")),
	DEATH_RESPAWN_MC_POS_X(NamespacedKey(Infinity0.NAME, "mc_respawn_pos_x")),
	DEATH_RESPAWN_MC_POS_Y(NamespacedKey(Infinity0.NAME, "mc_respawn_pos_y")),
	DEATH_RESPAWN_MC_POS_Z(NamespacedKey(Infinity0.NAME, "mc_respawn_pos_z")),
	DEATH_RESPAWN_INFINITY_WORLD(NamespacedKey(Infinity0.NAME, "infinity_respawn_world")),
	DEATH_RESPAWN_INFINITY_POS_X(NamespacedKey(Infinity0.NAME, "infinity_respawn_pos_x")),
	DEATH_RESPAWN_INFINITY_POS_Y(NamespacedKey(Infinity0.NAME, "infinity_respawn_pos_y")),
	DEATH_RESPAWN_INFINITY_POS_Z(NamespacedKey(Infinity0.NAME, "infinity_respawn_pos_z"));
	/* DEATH AND RESPAWN KEYS START */

	fun get(): NamespacedKey {
		return namespace
	}

	companion object {
		@JvmStatic
		fun fromNamespacedKey(key: NamespacedKey): Keys? {
			for (entry in Keys.entries) {
				if (entry.namespace == key) {
					return entry
				}
			}
			return null
		}

		@JvmStatic
		fun Player.addKey(key: Keys) {
			if (!key.isState) {
				throw IllegalStateException("Cannot add non-state key. State keys have pre-defined values while a non-state key has gamemode-dependent values.")
			}
			this.persistentDataContainer.set(key.get(), PersistentDataType.BOOLEAN, true)
		}

		@JvmStatic
		fun Player.removeKey(key: Keys) {
			if (!key.isState) {
				throw IllegalStateException("Cannot remove non-state key. State keys have pre-defined values while a non-state key has gamemode-dependent values.")
			}
			this.persistentDataContainer.remove(key.get())
		}
	}

}