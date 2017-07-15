package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

/**
 * Created by eric on 7/11/17.
 */
public class InternalCommand extends AbstractCommand {
    public InternalCommand(Tregmine plugin) {
        super(plugin, "internal", Tregmine.PermissionDefinitions.ADMIN_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String args[]) {
        if (args.length == 0) {
            error(player, "/" + this.command + " <flags>");
            return true;
        }
        if (args[0].equalsIgnoreCase("flags")) {
            if (args.length == 1) {
                error(player, "/" + this.command + " <flags> <username>");
                return true;
            }
            List<GenericPlayer> targets = this.tregmine.matchPlayer(args[1]);
            GenericPlayer target = null;
            if (targets.size() == 0 || targets.size() >= 2) {
                player.sendMessage("Could not find a player by the name of " + args[1]);
                return true;
            } else target = targets.get(0);
            if (args.length == 2 || args[2].equalsIgnoreCase("list")) {
                player.sendMessage(new TextComponent("Flags for "), target.getChatName());
                player.sendMessage(ChatColor.AQUA + target.getFlags().toString());
            }
            if (args.length >= 4) {
                if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
                    GenericPlayer.Flags flag = null;
                    try {
                        flag = GenericPlayer.Flags.valueOf(args[3].toUpperCase());
                    } catch (Exception e) {
                        error(player, e.getMessage());
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("add")) {
                        if (target.hasFlag(flag)) {
                            error(player, "Player already has " + flag);
                            return true;
                        }
                        target.setFlag(flag);
                        player.sendMessage(ChatColor.GREEN + "Gave player " + flag + " flag.");
                    } else {
                        if (!target.hasFlag(flag)) {
                            error(player, "Player does not have " + flag);
                            return true;
                        }
                        target.removeFlag(flag);
                        player.sendMessage(ChatColor.GREEN + "Removed flag " + flag + " from player");
                    }
                    try {
                        IContext context = this.tregmine.createContext();
                        context.getPlayerDAO().updatePlayer(target);
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }
}
