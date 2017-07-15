package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Notification;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;

import static org.bukkit.ChatColor.AQUA;

public class BlessCommand extends AbstractCommand {
    public BlessCommand(Tregmine tregmine) {
        super(tregmine, "bless", Tregmine.PermissionDefinitions.STAFF_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        List<GenericPlayer> candidates = tregmine.matchPlayer(args[0]);
        if (candidates.size() != 1) {
            return error(player, "No player found");
        }

        GenericPlayer candidate = candidates.get(0);
        player.sendMessage(new TextComponent(AQUA + "You will bless following " + "blocks to "),
                candidate.getChatName(), new TextComponent("."));
        player.setBlessTarget(candidate.getId());

        return true;
    }
}
