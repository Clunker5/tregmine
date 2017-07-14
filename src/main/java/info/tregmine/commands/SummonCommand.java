package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class SummonCommand extends AbstractCommand {
    public SummonCommand(Tregmine tregmine) {
        super(tregmine, "summon");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName() == "vanilla") {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        if (args.length == 0) {
            return false;
        }

        String pattern = args[0];

        List<GenericPlayer> candidates = tregmine.matchPlayer(pattern);
        if (candidates.size() != 1) {
            player.sendMessage(RED + "Can't find user.");
        }

        GenericPlayer victim = candidates.get(0);
        if (victim.getWorld().getName() == "vanilla") {
            player.sendMessage(ChatColor.RED + "That player is in the vanilla world!");
            return true;
        }
        victim.setLastPos(victim.getLocation());

        // Mentors can summon their students, but nobody else. In those cases,
        // you need the canSummon-permission.
        if (victim != player.getStudent() && !player.getRank().canSummon()) {
            player.sendMessage(PERMISSION_DENIED);
            return true;
        }

        victim.setNoDamageTicks(200);

        victim.teleportWithHorse(player.getLocation());

        victim.sendMessage(player.decideVS(victim), new TextComponent(AQUA + " summoned you."));
        player.sendMessage(new TextComponent(AQUA + "You summoned "), victim.decideVS(player),
                new TextComponent(AQUA + " to yourself."));

        return true;
    }
}
