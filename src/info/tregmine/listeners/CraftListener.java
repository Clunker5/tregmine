package info.tregmine.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import info.tregmine.Tregmine;
import info.tregmine.api.lore.Created;

public class CraftListener implements Listener{
	private Tregmine plugin;
    public CraftListener(Tregmine instance)
    {
        this.plugin = instance;
    }
	@EventHandler
	public void onCraft(PrepareItemCraftEvent event){
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(ItemStack a : event.getInventory().getContents()) {
            items.add(a);
        }
		boolean illegal = false;
	
		for(ItemStack item : items){
				ItemMeta iMeta = item.getItemMeta();
				List<String> lore = item.getItemMeta().getLore();
				for(String line : lore){
				Bukkit.broadcastMessage(line + "WORK WORK WORK");
				Bukkit.broadcastMessage(iMeta.toString());
				if(line.contains("SPAWNED")){
					//Item is spawned
					ItemStack result = event.getInventory().getResult();
					ItemMeta meta = result.getItemMeta();
					List<String> newlore = meta.getLore();
					newlore.add(Created.SPAWNED.toColorString());
					result.setItemMeta(meta);
					event.getInventory().setResult(result);
					illegal = true;
					return;
				}
				else if(line.contains("CREATIVE")){
					//Item is creative
					ItemStack result = event.getInventory().getResult();
					ItemMeta meta = result.getItemMeta();
					List<String> newlore = meta.getLore();
					newlore.add(Created.CREATIVE.toColorString());
					result.setItemMeta(meta);
					event.getInventory().setResult(result);
					illegal = true;
					return;
				}
				}
		}
	}
}
