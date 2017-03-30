package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Lag;
import org.bukkit.ChatColor;

public class TpsCommand extends AbstractCommand {
    public TpsCommand(Tregmine tregmine) {
        super(tregmine, "ttps");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        double tps = Lag.getTPS();
        double lagPercentage = Math.round((1.0D - tps / 20.0D) * 100.0D);
        if (isTpsGood(tps)) {
            player.sendMessage(ChatColor.GREEN + "Server TPS: " + tps);
        }
        if (!isTpsGood(tps)) {
            player.sendMessage(ChatColor.RED + "Server TPS: " + tps);
        }
        player.sendMessage(ChatColor.BLUE + "Lag Percentage: " + lagPercentage);
        return true;
    }

    private boolean isTpsGood(double giveMeYourTPS) {
        return giveMeYourTPS >= 17.0D;
    }
}
