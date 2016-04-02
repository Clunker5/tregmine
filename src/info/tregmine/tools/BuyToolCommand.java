package info.tregmine.tools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.commands.AbstractCommand;

public class BuyToolCommand extends AbstractCommand{
	Tregmine plugin;
	public BuyToolCommand(Tregmine tregmine){
		super(tregmine, "buytool");
		plugin = tregmine;
	}
	public boolean handlePlayer(TregminePlayer player, String[] args){
		PlayerInventory inv = player.getInventory();
		Material price = Material.DIAMOND;
		int total = 16;
		int amount = inv.all(price).size();
		if(amount >= total){
			int removed = 0;
			while(removed != total){
			player.getInventory().removeItem(new ItemStack(price));
			removed += 1;
			}
			player.sendMessage(total + " " + price.name() + " have been taken from your inventory.");
		}else{
			player.sendMessage(ChatColor.RED + "You need " + total + " " + price.name() + " to use this command.");
		}
		return true;
	}
}
