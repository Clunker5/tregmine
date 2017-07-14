package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Warp;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IWarpDAO;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class CreateWarpCommand extends AbstractCommand {
    public CreateWarpCommand(Tregmine tregmine) {
        super(tregmine, "createwarp", Tregmine.PermissionDefinitions.ADMIN_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length != 1) {
            return false;
        }

        String name = args[0];
        if (name.equalsIgnoreCase("irl")) {
            player.sendMessage(ChatColor.RED + "Warp already exists!");
            return true;
        }
        try (IContext ctx = tregmine.createContext()) {
            IWarpDAO warpDAO = ctx.getWarpDAO();

            Warp foundWarp = warpDAO.getWarp(args[0], tregmine.getServer());
            if (foundWarp != null) {
                player.sendMessage(ChatColor.RED + "Warp already exists!");
                return true;
            }

            Location loc = player.getLocation();
            warpDAO.insertWarp(name, loc);

            player.sendMessage(ChatColor.GREEN + "Warp " + args[0] + " created");
            LOGGER.info("WARPCREATE: " + args[0] + " by " + player.getName());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
