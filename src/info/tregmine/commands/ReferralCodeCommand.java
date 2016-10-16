package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.ChatColor;

public class ReferralCodeCommand extends AbstractCommand{
	
	public ReferralCodeCommand(Tregmine tregmine, String command) {
		super(tregmine, command);
	}
	
	public boolean handlePlayer(TregminePlayer sender, String[] args){
		if(sender.getRank().canHaveReferralCode()){
			sender.sendStringMessage(ChatColor.YELLOW + "Your referral code is " + sender.getId());
		}else{
			sender.sendStringMessage(ChatColor.RED + "Please verify your account before proceeding.");
		}
		return true;
	}
}
