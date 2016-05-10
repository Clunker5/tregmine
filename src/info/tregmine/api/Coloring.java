package info.tregmine.api;

import org.bukkit.ChatColor;

public class Coloring {
	
	public String reverseChatColor(String text, String prefix){
		String manipulate = text;
		String base = "\u00A7";
		manipulate = manipulate.replace(base, prefix);
		return manipulate;
	}
	
	
	
}
