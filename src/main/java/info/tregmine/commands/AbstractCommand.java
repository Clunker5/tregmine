package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Logger;

public abstract class AbstractCommand implements CommandExecutor {
    public static final TextComponent PERMISSION_DENIED = new TextComponentBuilder("You do not have access to that command.").setColor(net.md_5.bungee.api.ChatColor.DARK_RED).setBold(true).build();
    protected final Logger LOGGER = Logger.getLogger("Minecraft");
    protected final Tregmine.PermissionDefinitions permissionDefinitions;
    private final TregmineConsolePlayer consolePlayer;
    protected Tregmine tregmine;
    protected String command;
    private final boolean vanillaBanned;

    protected AbstractCommand(Tregmine tregmine, String command) {
        this(tregmine, command, null);
    }

    protected AbstractCommand(Tregmine tregmine, String command, Tregmine.PermissionDefinitions permissionDefinitions) {
        this(tregmine, command, permissionDefinitions, false);
    }

    protected AbstractCommand(Tregmine tregmine, String command, Tregmine.PermissionDefinitions permissionDefinitions, boolean vanillaBanned) {
        this.tregmine = tregmine;
        this.command = command;
        this.permissionDefinitions = permissionDefinitions;
        this.consolePlayer = new TregmineConsolePlayer(this.tregmine);
        this.vanillaBanned = vanillaBanned;
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
        return handlePlayer(this.consolePlayer, args);
    }

    public boolean handlePlayer(GenericPlayer player, String[] args) {
        return false;
    }

    public boolean invalidArguments(GenericPlayer player, String arguments) {
        return error(player, "Usage: " + arguments);
    }

    protected static boolean error(GenericPlayer player, String message) {
        return error(player, new TextComponent(message));
    }

    protected static boolean error(GenericPlayer player, TextComponent... messages) {
        player.sendNotification(Notification.COMMAND_FAIL, new TextComponentBuilder(new TextComponent(messages)).setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA).setBold(true).build());
        return true;
    }
    protected boolean usage(GenericPlayer player, Command command) {
        return error(player, "Usage: " + this.tregmine.getCommand(this.command).getUsage());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GenericPlayer player = sender instanceof Player ? tregmine.getPlayer((Player) sender) : sender instanceof DiscordCommandSender ? (DiscordCommandSender) sender : this.consolePlayer;
        if (!(sender instanceof Player) && !(sender instanceof DiscordCommandSender)) {
            if (!this.handleOther(sender.getServer(), args)) return usage(player, command);
            return true;
        }
        if (this.vanillaBanned && player.isInVanillaWorld()) {
            return error(player, "You cannot use that command in this world!");
        }
        if (this.permissionDefinitions != null) {
            if (!Arrays.asList(this.permissionDefinitions.getPermissions()).contains(player.getRank()) && !player.isOp()) {
                return error(player, permissionDefinitions.getDeniedMessage());
            }
        }
        if (!player.getRank().canUseCommands()) {
                return error(player, "Please complete setup before " + "continuing.");
        }
        if (!handlePlayer(player, args)) return usage(player, command);
        return true;
    }

}
