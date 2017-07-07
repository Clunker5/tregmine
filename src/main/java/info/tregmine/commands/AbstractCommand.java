package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.TregmineConsolePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public abstract class AbstractCommand implements CommandExecutor {
    protected final Logger LOGGER = Logger.getLogger("Minecraft");

    protected Tregmine tregmine;
    protected String command;

    protected AbstractCommand(Tregmine tregmine, String command) {
        this.tregmine = tregmine;
        this.command = command;
    }

    public String getName() {
        return command;
    }

    /**
     * This is the default console-method, which uses a slimmed-down TregminePlayer to function.
     * It is not recommended to leave this as the default, mostly due to the fact that many things
     * do not work with this compatibility layer due to the fact that this is not a real player.
     */
    public boolean handleOther(Server server, String[] args) {
        return handlePlayer(new TregmineConsolePlayer(this.tregmine), args);
    }

    public boolean handlePlayer(GenericPlayer player, String[] args) {
        return false;
    }

    public void insufficientPerms(GenericPlayer player) {
        player.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions for /" + this.command + ".");
    }

    public void invalidArguments(GenericPlayer player, String arguments) {
        player.sendMessage(ChatColor.RED + "Invalid arguments passed for /" + this.command + ".");
        player.sendMessage(ChatColor.RED + "The proper syntax is: " + ChatColor.GOLD + arguments);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            GenericPlayer player = tregmine.getPlayer((Player) sender);
            if (!player.getRank().canUseCommands()) {
                player.sendMessage(ChatColor.RED + "Please complete setup before " + "continuing.");
                return true;
            }

            return handlePlayer(player, args);
        }

        return handleOther(sender.getServer(), args);
    }

}
