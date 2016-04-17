package info.tregmine.commands;

import static org.bukkit.ChatColor.GREEN;

import java.util.List;

import org.bukkit.*;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class SendToCommand extends AbstractCommand
{
    public SendToCommand(Tregmine tregmine)
    {
        super(tregmine, "sendto");
    }

    @Override
    public boolean handlePlayer(TregminePlayer player, String[] args)
    {
    	if (args.length != 2) {
            return false;
        }
    	if(args[1] == "vanilla" && player.getWorld().getName() == "world"){
    		List<TregminePlayer> candidates = tregmine.matchPlayer(args[0]);
            if (candidates.size() != 1) {
                return true;
            }
    		TregminePlayer victim = candidates.get(0);
    		if(victim.getGameMode() == GameMode.CREATIVE){
    			victim.setGameMode(GameMode.SURVIVAL);
    		}
            Server server = tregmine.getServer();
            World world = server.getWorld(args[1]);
            Location cpspawn = world.getSpawnLocation();
            victim.teleportWithHorse(cpspawn);
            if(victim.getGameMode() == GameMode.CREATIVE){
    			victim.setGameMode(GameMode.SURVIVAL);
    		}
            return true;
    	}else if(args[1] == "world" && player.getWorld().getName() == "vanilla"){
    		List<TregminePlayer> candidates = tregmine.matchPlayer(args[0]);
            if (candidates.size() != 1) {
                return true;
            }
    		TregminePlayer victim = candidates.get(0);
            Server server = tregmine.getServer();
            World world = server.getWorld(args[1]);
            Location cpspawn = world.getSpawnLocation();
            victim.teleportWithHorse(cpspawn);
            return true;
    	}
        if (!player.getRank().canSendPeopleToOtherWorlds()) {
            return true;
        }

        

        List<TregminePlayer> candidates = tregmine.matchPlayer(args[0]);
        if (candidates.size() != 1) {
            // TODO: List users
            return true;
        }

        TregminePlayer victim = candidates.get(0);
        Server server = tregmine.getServer();
        World world = server.getWorld(args[1]);
        if (world == null) {
        	player.sendMessage(ChatColor.RED + "That world does not exist.");
            return true;
        }

        Location cpspawn = world.getSpawnLocation();
        victim.teleportWithHorse(cpspawn);

        return true;
    }
}
