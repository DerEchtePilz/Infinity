package io.github.derechtepilz.infinity.util;

import io.github.derechtepilz.infinity.Infinity;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public enum Keys {

    /* GENERAL KEYS START */
    WORLD_LOBBY(new NamespacedKey(Infinity.NAME, "lobby")),
    WORLD_SKY(new NamespacedKey(Infinity.NAME, "sky")),
    WORLD_STONE(new NamespacedKey(Infinity.NAME, "stone")),
    WORLD_NETHER(new NamespacedKey(Infinity.NAME, "nether")),
    DEFAULT_GAMEMODE(new NamespacedKey(Infinity.NAME, "default_gamemode")),
    /* GENERAL KEYS END */

    /*
     * GAME STATE KEYS START
     *
     * These keys are dynamically added and/or removed during server runtime.
     * They may represent a permission being added to a player or enable a player to use or see specific items in menus and/or the game
     */
    STORY_STARTED(new NamespacedKey(Infinity.NAME, "story_started"), true),
    INTRODUCTION_SEQUENCE(new NamespacedKey(Infinity.NAME, "introduction_sequence"), true),
    INTRODUCTION_SEQUENCE_COMPLETED(new NamespacedKey(Infinity.NAME, "introduction_sequence_completed"), true),
    GAMEMODE_SWITCH_ENABLED(new NamespacedKey(Infinity.NAME, "gamemode_switch_enabled"), true),
    /* GAME STATE KEYS END */

    /* SWITCH GAMEMODE KEYS START */
    SWITCH_GAMEMODE_LAST_WORLD(new NamespacedKey(Infinity.NAME, "sg_last_world_key")),
    SWITCH_GAMEMODE_LAST_X(new NamespacedKey(Infinity.NAME, "sg_last_pos_x")),
    SWITCH_GAMEMODE_LAST_Y(new NamespacedKey(Infinity.NAME, "sg_last_pos_y")),
    SWITCH_GAMEMODE_LAST_Z(new NamespacedKey(Infinity.NAME, "sg_last_pos_z")),
    SWITCH_GAMEMODE_LAST_YAW(new NamespacedKey(Infinity.NAME, "sg_last_yaw")),
    SWITCH_GAMEMODE_LAST_PITCH(new NamespacedKey(Infinity.NAME, "sg_last_pitch")),
    /* SWITCH GAMEMODE KEYS START */

    /* SIGN RELATED KEYS START */
    SIGN_TAG_MINECRAFT_TELEPORT(new NamespacedKey(Infinity.NAME, "st_teleport_to_minecraft")),
    SIGN_TAG_HOME_DIMENSION_TELEPORT(new NamespacedKey(Infinity.NAME, "st_teleport_to_home")),
    SIGN_TAG_SELECT_CLASS(new NamespacedKey(Infinity.NAME, "st_select_class")),
    SIGN_TAG_SWITCH_CLASS(new NamespacedKey(Infinity.NAME, "st_switch_class")),
    SIGN_STATE_HOME_DIMENSION(new NamespacedKey(Infinity.NAME, "ss_home_dimension")),
    SIGN_STATE_SELECT_CLASS(new NamespacedKey(Infinity.NAME, "ss_select_class")),
    SIGN_STATE_SWITCH_CLASS(new NamespacedKey(Infinity.NAME, "ss_switch_class")),
    /* SIGN RELATED KEYS START */

    /* DEATH AND RESPAWN KEYS START */
    DEATH_RESPAWN_MC_WORLD(new NamespacedKey(Infinity.NAME, "mc_respawn_world")),
    DEATH_RESPAWN_MC_POS_X(new NamespacedKey(Infinity.NAME, "mc_respawn_pos_x")),
    DEATH_RESPAWN_MC_POS_Y(new NamespacedKey(Infinity.NAME, "mc_respawn_pos_y")),
    DEATH_RESPAWN_MC_POS_Z(new NamespacedKey(Infinity.NAME, "mc_respawn_pos_z")),
    DEATH_RESPAWN_INFINITY_WORLD(new NamespacedKey(Infinity.NAME, "infinity_respawn_world")),
    DEATH_RESPAWN_INFINITY_POS_X(new NamespacedKey(Infinity.NAME, "infinity_respawn_pos_x")),
    DEATH_RESPAWN_INFINITY_POS_Y(new NamespacedKey(Infinity.NAME, "infinity_respawn_pos_y")),
    DEATH_RESPAWN_INFINITY_POS_Z(new NamespacedKey(Infinity.NAME, "infinity_respawn_pos_z"));
    /* DEATH AND RESPAWN KEYS START */

    private final NamespacedKey namespace;
    private final boolean isState;

    Keys(NamespacedKey namespace) {
        this(namespace, false);
    }

    Keys(NamespacedKey namespace, boolean isState) {
        this.namespace = namespace;
        this.isState = isState;
    }

    public NamespacedKey get() {
        return namespace;
    }

    public static Keys fromNamespacedKey(NamespacedKey key) {
        for (Keys entry : Keys.values()) {
            if (entry.namespace == key) {
                return entry;
            }
        }
        return null;
    }

    public static void addKey(Player player, Keys key) {
        if (!key.isState) {
            throw new IllegalStateException("Cannot add non-state key. State keys have pre-defined values while a non-state key has gamemode-dependent values.");
        }
        player.getPersistentDataContainer().set(key.get(), PersistentDataType.BOOLEAN, true);
    }

    public static void removeKey(Player player, Keys key) {
        if (!key.isState) {
            throw new IllegalStateException("Cannot remove non-state key. State keys have pre-defined values while a non-state key has gamemode-dependent values.");
        }
        player.getPersistentDataContainer().remove(key.get());
    }

}
