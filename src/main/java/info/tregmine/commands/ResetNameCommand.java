package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.GenericPlayer.Property;
import org.bukkit.ChatColor;

public class ResetNameCommand extends AbstractCommand {
    public ResetNameCommand(Tregmine tregmine) {
        super(tregmine, "rname");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        player.setNickname(null);
        player.sendMessage(ChatColor.GREEN + "Your name has been reset.");
        player.removeProperty(Property.NICKNAME);
        return true;
    }
}
