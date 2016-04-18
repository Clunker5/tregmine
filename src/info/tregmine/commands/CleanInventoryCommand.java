package info.tregmine.commands;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class CleanInventoryCommand extends AbstractCommand
{
    public CleanInventoryCommand(Tregmine tregmine)
    {
        super(tregmine, "clean");
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
    	if(player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()){
			player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
        player.getInventory().clear();
        return true;
    }
}
