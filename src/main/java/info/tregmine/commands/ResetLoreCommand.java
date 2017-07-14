package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ResetLoreCommand extends AbstractCommand {
    Tregmine plugin;
    GenericPlayer player;

    public ResetLoreCommand(Tregmine instance) {
        super(instance, "resetlore", Tregmine.PermissionDefinitions.SENIOR_REQUIRED);
        plugin = instance;
    }

    @Override
    public boolean handlePlayer(GenericPlayer sender, String[] args) {
        if (sender.getWorld().getName().equalsIgnoreCase("vanilla") || sender.isInVanillaWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        player = sender;
        Inventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();
        for (ItemStack item : contents) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<String>();
                lore.add("Flags reset by: " + player.getName());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        player.sendMessage(ChatColor.GOLD + "Any items that had a lore have lost their lore.");
        return true;
    }
}
