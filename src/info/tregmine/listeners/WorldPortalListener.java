package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

import info.tregmine.events.TregminePortalEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class WorldPortalListener implements Listener{
    private Tregmine plugin;
    
    World vanillaWorld = plugin.getVanillaWorld();
	World vanillaEndWorld = plugin.getVanillaEnd();
	World vanillaNether = plugin.getVanillaNether();
	World mainWorld = plugin.getServer().getWorld("world");
	World mainWorldNether = plugin.getServer().getWorld("world_nether");
	World mainWorldEnd = plugin.getServer().getWorld("world_the_end");
    
    public WorldPortalListener(Tregmine instance)
    {
        this.plugin = instance;
    }
    
    private String prefix(boolean error){
    	if(error){
    		return ChatColor.RED + "[PORTAL] ";
    	}else{
    		return ChatColor.AQUA + "[PORTAL] ";
    	}
    }
    
    @EventHandler
    public void portalHandler(PlayerPortalEvent event){
    	
    	TeleportCause cause = event.getCause();
    	TregminePlayer player = plugin.getPlayer(event.getPlayer());
    	if(cause != TeleportCause.NETHER_PORTAL && cause != TeleportCause.END_PORTAL && cause != TeleportCause.END_GATEWAY){
    		return;
    	}
    	event.setCancelled(true);
    	switch(cause){
    	case NETHER_PORTAL:
			if(player.getWorld() == vanillaWorld){
				//Send to nether
				Location loc = vanillaNether.getSpawnLocation();
				player.teleportWithHorse(loc);
			}else if(player.getWorld() == vanillaNether){
				//Send home
				Location loc = vanillaWorld.getSpawnLocation();
				player.teleportWithHorse(loc);
			}else if(player.getWorld() == mainWorld){
				//Send to nether
				Location loc = mainWorldNether.getSpawnLocation();
				player.teleportWithHorse(loc);
			}else if(player.getWorld() == mainWorldNether){
				//Send home
				Location loc = mainWorld.getSpawnLocation();
				player.teleportWithHorse(loc);
			}else{
				//This player shouldn't do that
    			player.sendStringMessage(this.prefix(true) + "You are in an illegal world for that portal; Please contact an admin for assistance.");
				break;
			}
    	case END_PORTAL:
    		if(player.getWorld() == vanillaWorld){
    			//Send to vanilla end
    			Location loc = vanillaEndWorld.getSpawnLocation();
    			player.teleportWithHorse(loc);
    		}else if(player.getWorld() == mainWorld){
    			//Send to end
    			Location loc = mainWorldEnd.getSpawnLocation();
    			player.teleportWithHorse(loc);
    		}else{
    			//This player shouldn't do that
    			player.sendStringMessage(this.prefix(true) + "You are in an illegal world for that portal; Please contact an admin for assistance.");
				break;
    		}
    	case END_GATEWAY:
    		if(player.getWorld() == vanillaEndWorld){
    			//Send to vanilla home
    			Location loc = vanillaWorld.getSpawnLocation();
    			player.teleportWithHorse(loc);
    		}else if(player.getWorld() == mainWorldEnd){
    			//Send home
    			Location loc = mainWorld.getSpawnLocation();
    			player.teleportWithHorse(loc);
    		}else{
    			//This player shouldn't do that
    			player.sendStringMessage(this.prefix(true) + "You are in an illegal world for that portal; Please contact an admin for assistance.");
				break;
    		}
    	}
    	
    }
    
//    
//    @EventHandler
//    public void portalHandler(PlayerMoveEvent event)
//    {
//        final TregminePlayer player = plugin.getPlayer(event.getPlayer());
//        Block under = player.getLocation().subtract(0, 1, 0).getBlock();
//        Block in = event.getTo().getBlock();
//        
//        // Simply add another line changing frame, under, world and name to add a new portal! (Similar to end portal)
//    }
//
//    public void handlePortal(TregminePlayer player, Material underType, Material frame, World newWorld, String worldName, Block in, Block under)
//    {
//        if (under.getType() != underType || !in.isLiquid()) {
//            return;
//        }
//
//        if (  !(frameCheck(player, -1, 3, -1, 3, frame) ||
//                frameCheck(player, -1, 3, -2, 2, frame) ||
//                frameCheck(player, -1, 3, -3, 1, frame) ||
//                frameCheck(player, -2, 2, -1, 3, frame) ||
//                frameCheck(player, -2, 2, -2, 2, frame) ||
//                frameCheck(player, -2, 2, -3, 1, frame) ||
//                frameCheck(player, -3, 1, -1, 3, frame) ||
//                frameCheck(player, -3, 1, -2, 2, frame) ||
//                frameCheck(player, -3, 1, -3, 1, frame))) {
//            return;
//        }
//
//        if (player.getWorld().getName().equalsIgnoreCase(newWorld.getName())) {
//			plugin.getServer().getPluginManager().callEvent(new TregminePortalEvent(player.getWorld(), plugin.getServer().getWorld("world"), player));
//            player.teleportWithHorse(plugin.getServer().getWorld("world").getSpawnLocation());
//            player.sendStringMessage(ChatColor.GOLD + "[PORTAL] " + ChatColor.GREEN + "Teleporting to main world!");
//        } else {
//			plugin.getServer().getPluginManager().callEvent(new TregminePortalEvent(player.getWorld(), newWorld, player));
//            player.teleportWithHorse(newWorld.getSpawnLocation());
//            player.sendStringMessage(ChatColor.GOLD + "[PORTAL] " + ChatColor.GREEN + "Teleporting to " + worldName + " world!");
//        }
//        player.setFireTicks(0);
//    }
//    
//    public boolean frameCheck(TregminePlayer p, int x1, int x2, int z1, int z2, Material portal)
//    {
//        if(     p.getLocation().add(x1, 0, 0).getBlock().getType().equals(portal) && 
//                p.getLocation().add(0, 0, z1).getBlock().getType().equals(portal) && 
//                p.getLocation().add(x2, 0, 0).getBlock().getType().equals(portal) && 
//                p.getLocation().add(0, 0, z2).getBlock().getType().equals(portal)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}
