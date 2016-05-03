package com.scarsz.discordsrv;

import com.scarsz.discordsrv.hooks.Essentials;
import org.bukkit.Bukkit;
import com.scarsz.discordsrv.hooks.PremiumVanish;
import com.scarsz.discordsrv.hooks.SuperVanish;
import com.scarsz.discordsrv.hooks.VanishNoPacket;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;

public class VanishedPlayerCheck {

    public static boolean checkPlayerIsVanished(String player, Tregmine plugin) {
        Boolean isVanished = false;

        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) isVanished = Essentials.isVanished(player) ? true : isVanished;
        if (Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) isVanished = PremiumVanish.isVanished(player) ? true : isVanished;
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish")) isVanished = SuperVanish.isVanished(player) ? true : isVanished;
        if (Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) isVanished = VanishNoPacket.isVanished(player) ? true : isVanished;
        TregminePlayer check = plugin.getPlayer(player);
        if(check.hasFlag(Flags.INVISIBLE)) isVanished = true;
        if (DiscordSRV.plugin.getConfig().getBoolean("PlayerVanishLookupReporting")) DiscordSRV.plugin.getLogger().info("Looking up vanish status for " + player + ": " + isVanished);
        return isVanished;
    }

}