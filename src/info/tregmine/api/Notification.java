package info.tregmine.api;

import org.bukkit.Sound;

/**
 * Represents a notification to send to a player
 * <p>
 * This is called at different times.
 * 
 * <p>
 * Ex: When a player receives a message, they receive Notification.MESSAGE
 * @author Robert Catron
 *
 */
public enum Notification {
	NONE(null),//Place holder for just a message
	BLESS(Sound.BLOCK_CHEST_CLOSE), //to keep it related to chests ect..
	COMMAND_FAIL(Sound.UI_BUTTON_CLICK),
	MESSAGE(Sound.ENTITY_PLAYER_LEVELUP),
	RANK_UP(Sound.ENTITY_TNT_PRIMED),
	RARE_DROP(Sound.ENTITY_ITEM_PICKUP),
	SUMMON(Sound.BLOCK_PORTAL_TRAVEL),
	WARP(Sound.ENTITY_ENDERMEN_TELEPORT);
	
	
	private final Sound sound;
	
	private Notification(Sound sound)
	{
		this.sound = sound;
	}
	
	/**
	 * @return The sound of the notification
	 */
	public Sound getSound() {
		return sound;
	}
}
