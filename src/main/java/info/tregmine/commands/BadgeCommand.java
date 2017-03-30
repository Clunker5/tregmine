package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Badge;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.Map;

public class BadgeCommand extends AbstractCommand {
    public BadgeCommand(Tregmine tregmine) {
        super(tregmine, "badge");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String args[]) {
        if(args.length == 0){
            return false;
        }
        if (args.length == 1 && "list".equalsIgnoreCase(args[0])) {

            Map<Badge, Integer> badges = player.getBadges();
            if (badges.isEmpty()) {
                player.sendMessage(ChatColor.AQUA + "You currently have no badges!");
                return true;
            }

            for (Map.Entry<Badge, Integer> badge : badges.entrySet()) {
                player.sendMessage(ChatColor.AQUA + badge.getKey().name() + " - Level " + badge.getValue());
            }

        } else if (args.length == 2 && "list".equalsIgnoreCase(args[0])) {

            if (!player.getRank().canViewPlayersBadge()) {
                return true;
            }

            GenericPlayer target;
            try {
                target = tregmine.getPlayerOffline(args[0]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                target = null;
            }

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Could not find player: " + ChatColor.YELLOW + args[0]);
                return true;
            }
            Map<Badge, Integer> badges = target.getBadges();
            if (badges.isEmpty()) {

                player.getSpigot().sendMessage(target.getChatName(),
                        new TextComponent(ChatColor.AQUA + " currently has no badges!"));
                return true;
            }

            for (Map.Entry<Badge, Integer> badge : badges.entrySet()) {
                player.sendMessage(ChatColor.AQUA + badge.getKey().name() + " - Level " + badge.getValue());
            }

        } else if (args[0].equalsIgnoreCase("give") && args.length == 2) {
            if (!player.getRank().canGiveBadges()) {
                return true;
            }
            GenericPlayer target;
            try {
                target = tregmine.getPlayerOffline(args[1]);
            } catch (Exception e){
                player.sendMessage(ChatColor.RED + "Could not find player: " + ChatColor.YELLOW + args[0]);
                return true;
            }
            

        }
        return true;
    }
}
