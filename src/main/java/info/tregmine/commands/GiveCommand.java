package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.YELLOW;

public class GiveCommand extends AbstractCommand {
    public GiveCommand(Tregmine tregmine) {
        super(tregmine, "give", Tregmine.PermissionDefinitions.ADMIN_REQUIRED, true);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String pattern = args[0];
        String param = args[1].toUpperCase();

        List<GenericPlayer> candidates = tregmine.matchPlayer(pattern);
        if (candidates.size() != 1) {
            // TODO: error message
            return true;
        }

        GenericPlayer target = candidates.get(0);
        if (player.isInVanillaWorld()) {
            error(player, "That player is in the vanilla world!");
            return true;
        }
        Material material;
        try {
            material = Material.getMaterial(param);
        } catch (NullPointerException ne) {
            player.sendMessage(DARK_AQUA + "/give <name> <amount> <data>.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            amount = 1;
        }

        int data;
        try {
            data = Integer.parseInt(args[3]);
        } catch (Exception e) {
            data = 0;
        }
        ItemStack item = new ItemStack(material, amount, (byte) data);
        if (item.getType() == Material.MONSTER_EGG || item.getType() == Material.NAME_TAG) {
            return false;
        }

        PlayerInventory inv = target.getInventory();
        if (inv == null) {
            return true;
        }

        inv.addItem(item);

        String materialName = material.name();

        player.sendMessage("You gave " + amount + " of " + DARK_AQUA + materialName.toLowerCase() + " to "
                + target.getName() + ".");
        target.sendMessage(YELLOW + "You were gifted by the gods. Look in your " + "inventory!");
        LOGGER.info(player.getName() + " SPAWNED " + amount + ":" + materialName + "=>" + target.getName());

        return true;
    }
}
