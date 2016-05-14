package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.ChatColor;

public class OWCommand extends AbstractCommand{
		Tregmine tregmine;
		TregminePlayer sender;
		public OWCommand(Tregmine inst){
			super(inst, "oldworld");
			this.tregmine = inst;
		}
		public boolean handlePlayer(TregminePlayer player, String[] args){
			if(!tregmine.hasSecondaryWorld()){
				player.sendStringMessage(ChatColor.RED + "There's no old world on this server!");
				return true;
			}
			if(player.getWorld() == tregmine.getSWorld()){
				player.gotoWorld(player.getPlayer(), tregmine.getServer().getWorld("world").getSpawnLocation(), ChatColor.YELLOW + "[ARCHIVE] You're back in the real world!", ChatColor.RED + "[ARCHIVE] Something bad happened; Try again or contact an admin for assistance.");
			}else if(player.getWorld() == tregmine.getServer().getWorld("world")){
				player.gotoWorld(player.getPlayer(), tregmine.getSWorld().getSpawnLocation(), ChatColor.YELLOW + "[ARCHIVE] Welcome to the past!", ChatColor.YELLOW + "[ARCHIVE] Something bad happened; Try again or contact an admin for assistance.");
			}else{
				player.sendStringMessage(ChatColor.RED + "You must be in the overworld of either the old world or the main world");
			}
			return true;
		}
}
