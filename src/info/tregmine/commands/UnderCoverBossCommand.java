package info.tregmine.commands;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.GuardianState;

public class UnderCoverBossCommand extends AbstractCommand{
	private Tregmine plugin;
	private String request;
	public UnderCoverBossCommand(Tregmine tregmine){
		super(tregmine, "undercoverboss");
		this.plugin = tregmine;
	}
	public boolean handlePlayer(TregminePlayer player, String[] args){
		if(player.getTrueRank() != Rank.SENIOR_ADMIN){
			player.nopermsMessage(false, "undercoverboss");
			return true;
		}
		if(args[0].isEmpty()){
			return false;
		}
		this.request = args[0];
		if(this.request.equalsIgnoreCase("resident")){
			player.setTemporaryRank(Rank.RESIDENT);
			player.setTemporaryChatName(Rank.RESIDENT.getColor() + player.getName());
			player.sendMessage(ChatColor.BLUE + "You have been switched to resident until you re-log.");
		}else if(this.request.equalsIgnoreCase("donator")){
			player.setTemporaryRank(Rank.DONATOR);
			player.setTemporaryChatName(Rank.DONATOR.getColor() + player.getName());
			player.sendMessage(ChatColor.BLUE + "You have been switched to donator until you re-log.");
		}else if(this.request.equalsIgnoreCase("guardian")){
			player.setTemporaryRank(Rank.GUARDIAN);
			player.setGuardianState(GuardianState.ACTIVE);
			player.setTemporaryChatName(Rank.GUARDIAN.getColor() + player.getName());
			player.sendMessage(ChatColor.BLUE + "You have been switched to guardian until you re-log.");
		}else{
			player.sendMessage(ChatColor.RED + "You can use ranks RESIDENT, DONATOR, GUARDIAN");
		}
		return true;
	}
}
