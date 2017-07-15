package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class KillCommand extends AbstractCommand {
    Tregmine t;

    public KillCommand(Tregmine instance) {
        super(instance, "kill", Tregmine.PermissionDefinitions.SENIOR_REQUIRED, true);
        t = instance;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length != 1) {
            error(player, "Invalid arguments - Use /kill player");
            return true;
        }
        if (player.getWorld().getName() == "vanilla") {
            error(player, "You cannot use that command in this world!");
            return true;
        }
        List<GenericPlayer> candidates = tregmine.matchPlayer(args[0]);
        if (candidates.size() != 1) {
            error(player, "That player does not exist!");
        }
        GenericPlayer victim = candidates.get(0);
        if (victim.getWorld().getName() == "vanilla") {
            error(player, "Cannot kill a player in the vanilla world!");
            return true;
        }
        if (victim.getGameMode() == GameMode.CREATIVE) {
            error(player, "Cannot kill someone in creative!");
            return true;
        }
        error(player, "Killing " + victim.getName() + "...");
        player.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 10000));
        victim.setDeathCause("adminkilled");
        return true;
    }
}
