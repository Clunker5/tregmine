package info.tregmine.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import info.tregmine.api.lore.Created;

public class CraftListener implements Listener{
	@EventHandler
	public void onCraft(PrepareItemCraftEvent event){
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(ItemStack a : event.getInventory().getContents()) {
            if(a.getType().equals(Material.AIR)) continue;
            items.add(a);
        }
		boolean illegal = false;
		for(ItemStack item : items){
			if(illegal == false){
				List<String> lore = item.getItemMeta().getLore();
				if(lore.contains(Created.SPAWNED.toColorString())){
					//Item is spawned
					ItemStack result = event.getInventory().getResult();
					ItemMeta meta = result.getItemMeta();
					List<String> newlore = meta.getLore();
					newlore.add(Created.SPAWNED.toColorString());
					result.setItemMeta(meta);
					event.getInventory().setResult(result);
					
				}
				else if(lore.contains(Created.CREATIVE.toColorString())){
					//Item is creative
					ItemStack result = event.getInventory().getResult();
					ItemMeta meta = result.getItemMeta();
					List<String> newlore = meta.getLore();
					newlore.add(Created.CREATIVE.toColorString());
					result.setItemMeta(meta);
					event.getInventory().setResult(result);
				}
				else{
					return;
				}
			}
		}
	}
}
