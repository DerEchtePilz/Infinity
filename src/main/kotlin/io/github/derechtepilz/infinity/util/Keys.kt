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

import io.github.derechtepilz.infinity.Infinity
import org.bukkit.NamespacedKey

enum class Keys(private val namespace: NamespacedKey) {

	WORLD_LOBBY(NamespacedKey(Infinity.NAME, "lobby")),
	WORLD_SKY(NamespacedKey(Infinity.NAME, "sky")),
	WORLD_STONE(NamespacedKey(Infinity.NAME, "stone")),
	WORLD_NETHER(NamespacedKey(Infinity.NAME, "nether")),
	DEFAULT_GAMEMODE(NamespacedKey(Infinity.NAME, "default_gamemode")),
	SWITCH_GAMEMODE_LAST_WORLD(NamespacedKey(Infinity.NAME, "sg_last_world_key")),
	SWITCH_GAMEMODE_LAST_X(NamespacedKey(Infinity.NAME, "sg_last_pos_x")),
	SWITCH_GAMEMODE_LAST_Y(NamespacedKey(Infinity.NAME, "sg_last_pos_y")),
	SWITCH_GAMEMODE_LAST_Z(NamespacedKey(Infinity.NAME, "sg_last_pos_z")),
	SWITCH_GAMEMODE_LAST_YAW(NamespacedKey(Infinity.NAME, "sg_last_yaw")),
	SWITCH_GAMEMODE_LAST_PITCH(NamespacedKey(Infinity.NAME, "sg_last_pitch")),
	SIGN_TAG_MINECRAFT_TELEPORT(NamespacedKey(Infinity.NAME, "st_teleport_to_minecraft")),
	SIGN_TAG_HOME_DIMENSION_TELEPORT(NamespacedKey(Infinity.NAME, "st_teleport_to_home")),
	SIGN_TAG_SELECT_CLASS(NamespacedKey(Infinity.NAME, "st_select_class")),
	SIGN_TAG_SWITCH_CLASS(NamespacedKey(Infinity.NAME, "st_switch_class")),
	SIGN_STATE_HOME_DIMENSION(NamespacedKey(Infinity.NAME, "ss_home_dimension")),
	SIGN_STATE_SELECT_CLASS(NamespacedKey(Infinity.NAME, "ss_select_class")),
	SIGN_STATE_SWITCH_CLASS(NamespacedKey(Infinity.NAME, "ss_switch_class")),
	DEATH_RESPAWN_MC_WORLD(NamespacedKey(Infinity.NAME, "mc_respawn_world")),
	DEATH_RESPAWN_MC_POS_X(NamespacedKey(Infinity.NAME, "mc_respawn_pos_x")),
	DEATH_RESPAWN_MC_POS_Y(NamespacedKey(Infinity.NAME, "mc_respawn_pos_y")),
	DEATH_RESPAWN_MC_POS_Z(NamespacedKey(Infinity.NAME, "mc_respawn_pos_z")),
	DEATH_RESPAWN_INFINITY_WORLD(NamespacedKey(Infinity.NAME, "infinity_respawn_world")),
	DEATH_RESPAWN_INFINITY_POS_X(NamespacedKey(Infinity.NAME, "infinity_respawn_pos_x")),
	DEATH_RESPAWN_INFINITY_POS_Y(NamespacedKey(Infinity.NAME, "infinity_respawn_pos_y")),
	DEATH_RESPAWN_INFINITY_POS_Z(NamespacedKey(Infinity.NAME, "infinity_respawn_pos_z"));

	fun get(): NamespacedKey {
		return namespace
	}

}