package info.tregmine.commands;

import java.util.logging.Logger;

import info.tregmine.api.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public abstract class AbstractCommand implements CommandExecutor
{
    protected final Logger LOGGER = Logger.getLogger("Minecraft");

    protected Tregmine tregmine;
    protected String command;

    private Rank lowestRank = null;

    protected AbstractCommand(Tregmine tregmine, String command)
    {
        this.tregmine = tregmine;
        this.command = command;
    }

    protected AbstractCommand(Tregmine tregmine, String command, Rank lowestRank)
    {
        this(tregmine, command);
        this.lowestRank = lowestRank;
    }

    public String getName()
    {
        return command;
    }

    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
        return false;
    }

    public boolean handleOther(Server server, String[] args)
    {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args)
    {
        if (sender instanceof Player) {
            TregminePlayer player = tregmine.getPlayer((Player) sender);
            if (!player.getRank().canUseCommands()) {
                player.sendMessage(ChatColor.RED + "Please complete setup before " +
                        "continuing.");
                return true;
            }

            if (this.lowestRank != null) {
                if (player.getRank().compareTo(this.lowestRank) < 0) {
                    player.sendError("You do not have access to that command.");
                    return true;
                }
            }

            return handlePlayer(player, args);
        }

        return handleOther(sender.getServer(), args);
    }
}
