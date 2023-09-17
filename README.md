# Infinity

## Disclaimer

Infinity is still under heavy development. It is not ready to be played. If you choose to build it yourself, please be aware of the fact that any version that does not have a major version of at least `1` may receive changes that you are not informed about thus breaking stuff you've done so far.

Upon Infinity reaching its first official release, every change that affects the story **directly** is announced before and at release.

That may include certain requirements being added which always will be retroactive among other changes. This is nothing I am deciding now and I am putting a lot of thought into the first official release to make sure the story is solid and nothing needs to happen retrocactively.

## Introduction

Infinity is a Minecraft plugin for Paper servers. It adds a custom-made story game to Minecraft.

When installing this plugin, you have two options:
- You can play Minecraft like normal and enjoy the `Overworld`, `Nether` and `End` the base game gives you
- You can also play Infinity and use the worlds this gamemode provides

You always have the option to switch which gamemode you want to play. Minecraft and Infinity will be handled separately and you will have
- separate inventories and ender chests
- separate levels
- separate spawn points
- separate chats (although this may change depending on feedback)
- separate health and hunger points
- separate potion effects

This allows you to play Infinity without having to worry that your progress in Minecraft will be lost. Similarly, you can also play Minecraft without your Infinity progress being lost.

This feature is the most dangerous feature this plugin offers because it relies on saving player progress for two game modes in one map. The one that is currently loaded represents the game mode the player currently plays.
The one that is currently saved is the one that the player does not play currently.

In order to ensure that players do not lose their progress on either game mode, a system will soon get implemented that backups progress on both game modes at a configurable rate. I suggest keeping the default though.

Nevertheless, this plugin his highly vulnerable and may not support the `/reload confirm` command added by Bukkit/Spigot/Paper. If you run into problems, please load a backup and restart the server.

To keep your player's progress safe, also <u>never</u> crash your server. This **will** destroy the progress of all players for the game mode they are not playing at that time.

**Due to a few technical problems, you will not be able to gain advancements while playing Infinity. If you find a solution, please open an [issue](https://github.com/DerEchtePilz/Infinity/issues/new) or a [PR](https://github.com/DerEchtePilz/Infinity/compare).**

## General concept

Due to Infinity's nature as a story game, I do not want to go into too much detail. If there's anything that you think should go into here, please open an issue.

### License

This project is licensed under the terms of the GNU General Public License version 3 (GPLv3). You can find it [here](./LICENSE).
