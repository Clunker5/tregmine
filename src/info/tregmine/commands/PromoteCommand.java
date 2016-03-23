package info.tregmine.commands;

import static org.bukkit.ChatColor.*;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import info.tregmine.commands.NotifyCommand;


public class PromoteCommand extends AbstractCommand{
	
	public PromoteCommand(Tregmine tregmine) {
		super(tregmine, "promote");
	}
	
	public boolean handlePlayer(TregminePlayer player, String[] args){
		if(player.getRank() != Rank.SENIOR_ADMIN && !player.isOp()){
			player.nopermsMessage(true, "promote");
			return true;
		}
		//This player is a senior admin and is allowed to promote. Continue.
		if(args.length != 2){
			//Player didn't enter two arguments, terminate.
			player.sendMessage(RED + "You have entered an invalid amount of arguments. Please try again.");
			return true;
		}
		//The checks have finished, perform the command
		String possibleuser = args[0];
		String newrank = args[1];
		Rank rank = null;
		if(newrank.equalsIgnoreCase("settler")){
			rank = Rank.SETTLER;
		}else if(newrank.equalsIgnoreCase("resident")){
			rank = Rank.RESIDENT;
		}else if(newrank.equalsIgnoreCase("donator")){
			rank = Rank.DONATOR;
		}else if(newrank.equalsIgnoreCase("guardian")){
			rank = Rank.GUARDIAN;
		}else if(newrank.equalsIgnoreCase("builder")){
			rank = Rank.BUILDER;
		}else if(newrank.equalsIgnoreCase("coder")){
			rank = Rank.CODER;
		}else if(newrank.equalsIgnoreCase("junioradmin") || newrank.equalsIgnoreCase("junior_admin")){
			rank = Rank.JUNIOR_ADMIN;
		}else if(newrank.equalsIgnoreCase("senioradmin") || newrank.equalsIgnoreCase("senior_admin")){
			rank = Rank.SENIOR_ADMIN;
		}else{
			player.sendMessage(RED + "You have specified an invalid rank. Please try again.");
			return true;
		}
		List<TregminePlayer> candidate = tregmine.matchPlayer(possibleuser);
		if(candidate.size() != 1){
			player.sendMessage(RED + "The player specified was not found. Please try again.");
			return true;
		}
		TregminePlayer user = candidate.get(0);
		if(user.hasFlag(Flags.HARDWARNED)){
			//Players with a hardwarn cannot be promoted using this command. They must be promoted manually.
			player.sendMessage(RED + "The player specified has been hardwarned and is not eligible for promotion.");
			return true;
		}
		//Any other errors have now been checked and dealt with. Promote the user.
		user.setRank(rank);
		Bukkit.broadcastMessage("" + BLUE + ITALIC + user.getChatName() + RESET + GREEN + " has been promoted to " + RESET + BLUE + ITALIC + newrank + "!");
		return true;
	}

}
