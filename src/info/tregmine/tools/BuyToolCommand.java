package info.tregmine.tools;

import java.util.HashMap;

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
		Material price = Material.DIAMOND_BLOCK;
		int total = 16;
		int amount = 0;
		ItemStack inhand = inv.getItemInMainHand();
		amount = inhand.getAmount();
		if(amount >= total){
			
			ItemStack tool = null;
			switch (args[0]) {
            case "lumber":
                tool = ToolsRegistry.LumberAxe();
                break;
            case "vein":
                tool = ToolsRegistry.VeinMiner();
                break;
        }
			if(tool == null){
					player.sendMessage(ChatColor.RED + "Usage: /buytool <lumber/vein>");
					return true;
			}
			HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(tool);
	        
	        if (failedItems.size() > 0) {
	            player.sendMessage(ChatColor.RED + "You have a full inventory, Can't add tool!");
	            return true;
	        }
	        inhand.setAmount(amount - total);
			player.setItemInHand(inhand);
			player.sendMessage(total + " " + price.name() + " have been taken from your inventory.");
			player.sendMessage(ChatColor.GREEN + "Spawned in tool token successfully!");
			return true;
	        
		}else{
			player.sendMessage(ChatColor.RED + "You need " + total + " " + price.name() + " in your hand to use this command.");
		}
		return true;
	}
}
