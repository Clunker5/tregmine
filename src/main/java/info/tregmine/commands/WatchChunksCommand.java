package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class WatchChunksCommand extends AbstractCommand {
    public WatchChunksCommand(Tregmine tregmine) {
        super(tregmine, "watchchunks");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("Your WatchChunks is set to "
                    + (player.hasFlag(GenericPlayer.Flags.WATCHING_CHUNKS) ? "on" : "off") + ".");
            return true;
        }

        String state = args[0];

        if ("on".equalsIgnoreCase(state)) {
            player.setFlag(GenericPlayer.Flags.WATCHING_CHUNKS);
            player.sendMessage(AQUA + "Watching Chunks is now turned on for you.");
        } else if ("off".equalsIgnoreCase(state)) {
            player.removeFlag(GenericPlayer.Flags.WATCHING_CHUNKS);
            player.sendMessage(AQUA + "Watching Chunks is now turned off for you.");
        } else if ("status".equalsIgnoreCase(state)) {
            player.sendMessage("Your Watching Chunks is set to "
                    + (player.hasFlag(GenericPlayer.Flags.WATCHING_CHUNKS) ? "on" : "off") + ".");
        } else {
            player.sendMessage(
                    RED + "The commands are /watchchunks on, /watchchunks off and /watchchunks status.");
        }

        try (IContext ctx = tregmine.createContext()) {
            IPlayerDAO playerDAO = ctx.getPlayerDAO();
            playerDAO.updatePlayer(player);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
