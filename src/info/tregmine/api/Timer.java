//This timer is a butchered version of the Essentials plug-in timer, partial credit to Essentials and its creators
package info.tregmine.api;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import info.tregmine.Tregmine;

public class Timer implements Runnable{
	Tregmine t;
	private final long maxTime = 10 * 1000000;
	private transient long lastPoll = System.nanoTime();
	private int skip1 = 0;
	public Timer(Tregmine plugin){
		t = plugin;
	}
	
	@Override
	public void run(){
		final long startTime = System.nanoTime();
		
		long timeSpent = (startTime - lastPoll) / 1000;
		if(timeSpent == 0){
			timeSpent = 1;
		}
		lastPoll = startTime;
		int count = 0;
		for(TregminePlayer player : t.getOnlinePlayers()){
			count++;
			if(skip1 > 0){
				skip1--;
				continue;
			}
			if(count % 10 == 0){
				if(System.nanoTime() - startTime > maxTime / 2){
					skip1 = count - 1;
				}
			}
			try
			{
				if(player.getWorld().getName().equalsIgnoreCase("vanilla")){
					if(player.getGameMode() == GameMode.CREATIVE){
						player.setGameMode(GameMode.SURVIVAL);
						player.setAllowFlight(false);
						player.sendMessage(ChatColor.RED + "You cannot be in creative in this world.");
					}
				}
				player.checkActivity();
				
			}catch(Exception e){
				t.getLogger().log(Level.WARNING, "Timer Exception", e);
			}
		}
	}
}
