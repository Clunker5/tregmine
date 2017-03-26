package info.tregmine.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.Warp;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IWarpDAO;

public class CreateWarpCommand extends AbstractCommand {
	public CreateWarpCommand(Tregmine tregmine) {
		super(tregmine, "createwarp");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 1) {
			return false;
		}
		if (!player.getRank().canCreateWarps()) {
			return true;
		}

		String name = args[0];
		if (name.equalsIgnoreCase("irl")) {
			player.sendStringMessage(ChatColor.RED + "Warp already exists!");
			return true;
		}
		try (IContext ctx = tregmine.createContext()) {
			IWarpDAO warpDAO = ctx.getWarpDAO();

			Warp foundWarp = warpDAO.getWarp(args[0], tregmine.getServer());
			if (foundWarp != null) {
				player.sendStringMessage(ChatColor.RED + "Warp already exists!");
				return true;
			}

			Location loc = player.getLocation();
			warpDAO.insertWarp(name, loc);

			player.sendStringMessage(ChatColor.GREEN + "Warp " + args[0] + " created");
			LOGGER.info("WARPCREATE: " + args[0] + " by " + player.getName());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}
}