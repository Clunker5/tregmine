package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import net.md_5.bungee.api.ChatColor;

public class ReferralCodeCommand extends AbstractCommand {

    public ReferralCodeCommand(Tregmine tregmine, String command) {
        super(tregmine, command, Tregmine.PermissionDefinitions.TOURIST_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer sender, String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "Your referral code is " + sender.getId());
        return true;
    }
}
