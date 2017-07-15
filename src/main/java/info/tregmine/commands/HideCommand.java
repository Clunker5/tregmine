package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class HideCommand extends AbstractCommand {
    public HideCommand(Tregmine tregmine) {
        super(tregmine, "hide", Tregmine.PermissionDefinitions.CODER_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("Your Announcement prevention is set to "
                    + (player.hasFlag(GenericPlayer.Flags.HIDDEN_ANNOUNCEMENT) ? "on" : "off") + ".");
            return true;
        }

        String state = args[0];

        if ("on".equalsIgnoreCase(state)) {
            player.setFlag(GenericPlayer.Flags.HIDDEN_ANNOUNCEMENT);
            player.sendMessage(AQUA + "Announcement prevention is now turned on for you.");
        } else if ("off".equalsIgnoreCase(state)) {
            player.removeFlag(GenericPlayer.Flags.HIDDEN_ANNOUNCEMENT);
            player.sendMessage(AQUA + "Announcement prevention is now turned off for you.");
        } else if ("status".equalsIgnoreCase(state)) {
            player.sendMessage("Your Announcement prevention display is set to "
                    + (player.hasFlag(GenericPlayer.Flags.HIDDEN_ANNOUNCEMENT) ? "on" : "off") + ".");
        } else {
            error(player, "The commands are /hide on, /hide off and /hide status.");
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
