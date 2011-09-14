package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.Zone;
import info.tregmine.quadtree.Point;
import info.tregmine.zones.Lot;
import info.tregmine.zones.ZoneWorld;
import info.tregmine.zones.ZonesPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ZoneBlockListener extends BlockListener 
{
    private final ZonesPlugin plugin;
    private final Tregmine tregmine;
	
    public ZoneBlockListener(ZonesPlugin instance) 
    {
        this.plugin = instance;
        this.tregmine = instance.tregmine;
    }

    public void onBlockBreak (BlockBreakEvent event) 
    {
    	TregminePlayer player = tregmine.getPlayer(event.getPlayer());
    	if (player.isAdmin()) {
    		return;
    	}
    	
    	ZoneWorld world = plugin.getWorld(player.getWorld());
    	if (world == null) {
    		return;
    	}
    	
    	Block block = event.getBlock();
    	Location location = block.getLocation();
    	Point pos = new Point(location.getBlockX(), location.getBlockZ());
    	
    	Zone currentZone = player.getCurrentZone();
    	if (currentZone == null || !currentZone.contains(world.getName(), pos)) {
    		currentZone = world.findZone(pos);
    		player.setCurrentZone(currentZone);
    	}
    	
    	if (currentZone != null) {
	    	Zone.Permission perm = currentZone.getUser(player.getName());
	    	
	    	Lot lot = world.findLot(pos);
	    	if (lot != null) {
	    		if (perm != Zone.Permission.Owner && player.getId() != lot.getUserId()) {
		    		player.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] " + 
		    				"You are not allowed to break blocks in lot " + lot.getName() + ".");
		    		event.setCancelled(true);
		    		return;
	    		}
	    	}
	    	
	    	// if everyone is allowed to build in this zone...
	    	if (currentZone.getDestroyDefault()) {
	    		// ...the only people that can't build are those that are banned
	    		if (perm != null && perm == Zone.Permission.Banned) {
		    		event.setCancelled(true);
		    		player.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] " + 
		    				"You are banned from " + currentZone.getName() + ".");	    			
	    		}
	    	} 
	    	// if this zone has limited building privileges...
	    	else {
	    		// ...we only allow builders and owners to make changes.
		    	if (perm == null || (perm != Zone.Permission.Maker && perm != Zone.Permission.Owner)) {
		    		player.setFireTicks(50);
		    		event.setCancelled(true);
		    		player.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] " + 
		    				"You are not allowed to break blocks in " + currentZone.getName() + ".");
		    	}
	    	}
    	}
    }
    
	public void onBlockPlace(BlockPlaceEvent event)
	{
    	TregminePlayer player = tregmine.getPlayer(event.getPlayer());
    	/*if (player.isAdmin()) {
    		return;
    	}*/
    	
    	ZoneWorld world = plugin.getWorld(player.getWorld());
    	if (world == null) {
    		return;
    	}
    	
    	Block block = event.getBlock();
    	Location location = block.getLocation();
    	Point pos = new Point(location.getBlockX(), location.getBlockZ());
    	
    	Zone currentZone = player.getCurrentZone();
    	if (currentZone == null || !currentZone.contains(world.getName(), pos)) {
    		currentZone = world.findZone(pos);
    		player.setCurrentZone(currentZone);
    	}
    	
    	if (currentZone != null) {
	    	Zone.Permission perm = currentZone.getUser(player.getName());
	    	
	    	Lot lot = world.findLot(pos);
	    	if (lot != null) {
	    		player.sendMessage("Block placed in lot " + lot.getName() + ".");
	    		if (perm != Zone.Permission.Owner && player.getId() != lot.getUserId()) {
		    		player.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] " + 
		    				"You are not allowed to break blocks in lot " + lot.getName() + ".");
		    		event.setCancelled(true);
		    		return;
	    		}
	    	}
	    	
	    	// if everyone is allowed to build in this zone...
	    	if (currentZone.getPlaceDefault()) {
	    		// ...the only people that can't build are those that are banned
	    		if (perm != null && perm == Zone.Permission.Banned) {
		    		event.setCancelled(true);
		    		player.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] " + 
		    				"You are banned from " + currentZone.getName() + ".");	    			
	    		}
	    	} 
	    	// if this zone has limited building privileges...
	    	else {
	    		// ...we only allow builders and owners to make changes.
		    	if (perm == null || (perm != Zone.Permission.Maker && perm != Zone.Permission.Owner)) {
		    		player.setFireTicks(50);
		    		event.setCancelled(true);
		    		player.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] " +
		    				"You are not allowed to place blocks in " + currentZone.getName() + ".");
		    	}
	    	}
    	}
	}
}
