package info.tregmine.commands;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.Lag;
import info.tregmine.api.TregminePlayer;

public class TpsCommand extends AbstractCommand{
	public TpsCommand(Tregmine tregmine){
		super(tregmine, "tps");
	}

	private boolean isTpsGood(double giveMeYourTPS){
		if(giveMeYourTPS >= 17.0D){
			return true;
		}else{
			return false;
		}
	}
	public boolean handlePlayer(TregminePlayer player, String[] args){
		if(args.length != 0){
			player.sendStringMessage(ChatColor.RED + "You didn't need to specify args, but okay...");
		}
		double tps = Lag.getTPS();
		double lagPercentage = Math.round((1.0D - tps / 20.0D) * 100.0D);
		if(isTpsGood(tps)){player.sendStringMessage(ChatColor.GREEN + "Server TPS: " + tps);}
		if(!isTpsGood(tps)){player.sendStringMessage(ChatColor.RED + "Server TPS: " + tps);}
		player.sendStringMessage(ChatColor.BLUE + "Lag Percentage: " + lagPercentage);
		return true;
	}
}
