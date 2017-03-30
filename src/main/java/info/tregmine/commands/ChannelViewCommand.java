package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class ChannelViewCommand extends AbstractCommand {
    public ChannelViewCommand(Tregmine tregmine) {
        super(tregmine, "invlog");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (!player.getRank().canChannelView()) {
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Your ChannelView is set to "
                    + (player.hasFlag(GenericPlayer.Flags.CHANNEL_VIEW) ? "on" : "off") + ".");
            return true;
        }

        String state = args[0];

        if ("on".equalsIgnoreCase(state)) {
            player.setFlag(GenericPlayer.Flags.CHANNEL_VIEW);
            player.sendMessage(AQUA + "Channel View display is now turned on for you.");
        } else if ("off".equalsIgnoreCase(state)) {
            player.removeFlag(GenericPlayer.Flags.CHANNEL_VIEW);
            player.sendMessage(AQUA + "Channel View display is now turned off for you.");
        } else if ("status".equalsIgnoreCase(state)) {
            player.sendMessage("Your Channel View display is set to "
                    + (player.hasFlag(GenericPlayer.Flags.CHANNEL_VIEW) ? "on" : "off") + ".");
        } else {
            player.sendMessage(
                    RED + "The commands are /channelview on, /channelview off and /channelview status.");
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
