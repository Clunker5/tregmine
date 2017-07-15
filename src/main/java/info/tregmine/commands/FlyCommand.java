package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import org.bukkit.ChatColor;

public class FlyCommand extends AbstractCommand {
    private Tregmine tregmine;

    public FlyCommand(Tregmine tregmine) {
        super(tregmine, "fly", Tregmine.PermissionDefinitions.DONATOR_REQUIRED, true);
        this.tregmine = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String args[]) {
        if (player.hasFlag(GenericPlayer.Flags.HARDWARNED) || player.hasFlag(GenericPlayer.Flags.SOFTWARNED)) {
            player.sendMessage("You are warned and are not allowed to fly.");
            player.setAllowFlight(false);
        }

        if (player.hasFlag(GenericPlayer.Flags.FLY_ENABLED)) {
            player.sendMessage(ChatColor.YELLOW + "Flying Disabled!");
            player.removeFlag(GenericPlayer.Flags.FLY_ENABLED);
            player.setAllowFlight(false);
        } else {

            player.sendMessage(ChatColor.YELLOW + "Flying Enabled!");
            player.setFlag(GenericPlayer.Flags.FLY_ENABLED);
            player.setAllowFlight(true);
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
