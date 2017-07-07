package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import org.bukkit.ChatColor;

import java.util.List;

public class FreezeCommand extends AbstractCommand {
    private Tregmine plugin;

    public FreezeCommand(Tregmine tregmine) {
        super(tregmine, "freeze");
        plugin = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.isInVanillaWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        if (player.getRank() != Rank.SENIOR_ADMIN) {
            player.sendMessage(ChatColor.RED + "You don't have permission to freeze players!");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid arguments! Use /freeze <player>");
            return true;
        }
        String raw = args[0];
        List<GenericPlayer> victims = plugin.matchPlayer(raw);
        if (victims.size() != 1) {
            player.sendMessage(ChatColor.RED + "That player is not online!");
            return true;
        }
        GenericPlayer victim = victims.get(0);
        if (player.isInVanillaWorld()) {
            player.sendMessage(
                    ChatColor.RED + "You cannot freeze that player because they are in the vanilla world!");
            return true;
        }
        boolean newValue = !victim.getFrozen();
        victim.setFrozen(newValue);
        String getState = (newValue ? "frozen" : "unfrozen");
        ChatColor color = (newValue ? ChatColor.RED : ChatColor.GREEN);
        player.sendMessage(victim.getName() + ChatColor.BLUE + " has been " + getState + ".");
        victim.sendMessage(color + "You have been " + getState + ".");
        return true;
    }
}
