package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.math.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;

import static org.bukkit.ChatColor.YELLOW;

public class NukeCommand extends AbstractCommand {
    public NukeCommand(Tregmine tregmine) {
        super(tregmine, "nuke");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        if (!player.getRank().canNuke()) {
            return true;
        }

        int distance;
        try {
            distance = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            distance = 160;
        } catch (ArrayIndexOutOfBoundsException e) {
            distance = 160;
        }

        player.sendMessage(YELLOW + "You nuked all mobs within " + distance + " meters");
        player.sendMessage(YELLOW + "say /nuke <number> to select a larger or smaller distance");

        Location loc = player.getLocation();
        for (Entity ent : player.getWorld().getLivingEntities()) {
            if (MathUtil.calcDistance2d(loc, ent.getLocation()) > distance) {
                continue;
            }

            if (ent instanceof Monster) {
                Monster mob = (Monster) ent;
                mob.remove();
            } else if (ent instanceof Animals) {
                Animals animal = (Animals) ent;
                animal.remove();
            } else if (ent instanceof Slime) {
                Slime slime = (Slime) ent;
                slime.remove();
            } else if (ent instanceof EnderDragon) {
                EnderDragon dragon = (EnderDragon) ent;
                dragon.remove();
            } else if (ent instanceof LivingEntity && !(ent instanceof Player)) {
                ent.remove();
            }
        }

        return true;
    }
}
