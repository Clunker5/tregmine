package com.scarsz.discordsrv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class Lag implements Runnable {
	
	private static int TICK_COUNT= 0;
	private static long[] TICKS= new long[600];
	private Tregmine tregmine = null;
	
	public void initializeTregmine(){
		PluginManager pluginMgm = Bukkit.getServer().getPluginManager();
        if (tregmine == null) {
            Plugin mainPlugin = pluginMgm.getPlugin("tregmine");
            if (mainPlugin != null) {
                tregmine = (Tregmine)mainPlugin;
            }
        }
	}

	public static String getTPSString()
	{
		String tps = Double.toString(getTPS());
		return tps.length() > 4 ? tps.substring(0, 4) : tps;
	}
	
	private static double getTPS()
	{
		return getTPS(100);
	}
	
	private static double getTPS(int ticks)
	{
		if (TICK_COUNT < ticks) return 20.0D;
		int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
		long elapsed = System.currentTimeMillis() - TICKS[target];
		return ticks / (elapsed / 1000.0D);
	}

	public void run()
	{
		TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
		TICK_COUNT+= 1;
		if(tregmine == null){
			initializeTregmine();
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			TregminePlayer dplayer = tregmine.getPlayer(player);
			if(dplayer.isAfk() && !dplayer.alertedAfk()){
				DiscordSRV.sendMessage(DiscordSRV.chatChannel, dplayer.getChatName() + " is now AFK.");
				dplayer.setAlerted(true);
			}else if(!dplayer.isAfk() && dplayer.alertedAfk()){
				DiscordSRV.sendMessage(DiscordSRV.chatChannel, dplayer.getChatName() + " is no longer AFK.");
				dplayer.setAlerted(false);
			}
		}
		
	}
	
}