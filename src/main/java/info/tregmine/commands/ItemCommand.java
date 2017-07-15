package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.lore.Created;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class ItemCommand extends AbstractCommand {
    public ItemCommand(Tregmine tregmine) {
        super(tregmine, "item", Tregmine.PermissionDefinitions.ADMIN_REQUIRED, true);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String param = args[0].toUpperCase();

        Material material;

        try {
            material = Material.getMaterial(param);
        } catch (NullPointerException ne) {
            return false;
        }

        if (material == null) {
            player.sendMessage(DARK_AQUA + "Sorry, the specified item was not found.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            amount = 1;
        } catch (NumberFormatException e) {
            amount = 1;
        }

        int data;
        try {
            data = Integer.parseInt(args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            data = 0;
        } catch (NumberFormatException e) {
            data = 0;
        }

        ItemStack item = new ItemStack(material, amount, (byte) data);
        if (item.getType() == Material.MONSTER_EGG || item.getType() == Material.NAME_TAG) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        lore.add(Created.SPAWNED.toColorString());
        lore.add(WHITE + "by: " + player.getName());
        lore.add(WHITE + "Value: " + MAGIC + "0000" + RESET + WHITE + " Treg");
        meta.setLore(lore);
        item.setItemMeta(meta);

        PlayerInventory inv = player.getInventory();
        inv.addItem(item);

        String materialName = material.toString();
        player.sendMessage("You received " + amount + " of " + DARK_AQUA + materialName.toLowerCase() + ".");
        LOGGER.info(player.getName() + " SPAWNED " + amount + ":" + materialName);

        return true;
    }
}
