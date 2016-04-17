package info.tregmine.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class KillCommand extends AbstractCommand{
	Tregmine t;
	public KillCommand(Tregmine instance){
		super(instance, "kill");
		t = instance;
	}
	public boolean handlePlayer(TregminePlayer player, String[] args){
		if(!player.getIsAdmin()){
			player.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
			return true;
		}
		if(args.length != 1){
			player.sendMessage(ChatColor.RED + "Invalid arguments - Use /kill player");
			return true;
		}
		if(!player.isOp()){
			player.sendMessage(ChatColor.RED + "You don't have permission to kill people!");
		}
		if(player.getWorld().getName() == "vanilla"){
			player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		List<TregminePlayer> candidates = tregmine.matchPlayer(args[0]);
		if(candidates.size() != 1){
			player.sendMessage(ChatColor.RED + "That player does not exist!");
		}
		TregminePlayer victim = candidates.get(0);
		if(victim.getWorld().getName() == "vanilla"){
			player.sendMessage(ChatColor.RED + "Cannot kill a player in the vanilla world!");
			return true;
		}
		if(victim.getGameMode() == GameMode.CREATIVE){
			player.sendMessage(ChatColor.RED + "Cannot kill someone in creative!");
			return true;
		}
		player.sendMessage(ChatColor.RED + "Killing " + victim.getChatName() + ChatColor.RED + "...");
		victim.setHealth(0);
		return true;
	}
}
