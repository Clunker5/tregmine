package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Notification;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;

import static org.bukkit.ChatColor.AQUA;

public class BlessCommand extends AbstractCommand {
    public BlessCommand(Tregmine tregmine) {
        super(tregmine, "bless");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (!player.getRank().canBless()) {
            return true;
        }

        List<GenericPlayer> candidates = tregmine.matchPlayer(args[0]);
        if (candidates.size() != 1) {
            player.sendNotification(Notification.COMMAND_FAIL, new TextComponent(ChatColor.RED + "No player found"));
            return false;
        }

        GenericPlayer candidate = candidates.get(0);
        player.sendMessage(new TextComponent(AQUA + "You will bless following " + "blocks to "),
                candidate.getChatName(), new TextComponent("."));
        player.setBlessTarget(candidate.getId());

        return true;
    }
}
