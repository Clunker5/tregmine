package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.lore.Created;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CraftListener implements Listener {
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        ItemStack[] stack = event.getInventory().getMatrix();
        for (ItemStack onestack : stack) {
            if (onestack == null)
                continue;
            ItemStack result = event.getInventory().getResult();
            if (result == null)
                continue;
            if (onestack.hasItemMeta()) {
                ItemMeta meta = onestack.getItemMeta();
                List<String> lore = meta.getLore();
                if (lore.contains(Created.CREATIVE.toColorString()) || lore.get(0).contains("CREATIVE")) {
                    result.getItemMeta().setLore(lore);
                }
                if (lore.contains(Created.SPAWNED.toColorString()) || lore.get(0).contains("SPAWNED")) {
                    result.getItemMeta().setLore(lore);
                }
                event.getInventory().setResult(result);
            }
        }
    }
}
