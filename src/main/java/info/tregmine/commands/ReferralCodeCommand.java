package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import net.md_5.bungee.api.ChatColor;

public class ReferralCodeCommand extends AbstractCommand {

    public ReferralCodeCommand(Tregmine tregmine, String command) {
        super(tregmine, command);
    }

    @Override
    public boolean handlePlayer(GenericPlayer sender, String[] args) {
        if (sender.getRank().canHaveReferralCode()) {
            sender.sendMessage(ChatColor.YELLOW + "Your referral code is " + sender.getId());
        } else {
            sender.sendMessage(ChatColor.RED + "Please verify your account before proceeding.");
        }
        return true;
    }
}
