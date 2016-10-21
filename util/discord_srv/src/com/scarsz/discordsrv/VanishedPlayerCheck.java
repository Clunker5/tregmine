package com.scarsz.discordsrv;

import org.bukkit.entity.Player;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;

public class VanishedPlayerCheck {

	public static boolean checkPlayerIsVanished(Player player, Tregmine plugin) {
		Boolean isVanished = false;
		TregminePlayer check = plugin.getPlayer(player);
		if (check.hasFlag(Flags.INVISIBLE))
			isVanished = true;
		if (DiscordSRV.plugin.getConfig().getBoolean("PlayerVanishLookupReporting"))
			DiscordSRV.plugin.getLogger().info("Looking up vanish status for " + player + ": " + isVanished);
		return isVanished;
	}

}