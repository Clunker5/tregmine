package info.tregmine.discord.entities;

import java.awt.Color;

public enum EmbedAlertType {
	STATUS_UPDATE("Server Status Update"), SAY("Message from the admins"), CONF_CODE(
			"Enter the following command in Minecraft:"), PING("Pong!", Color.CYAN);

	private String niceName;
	private Color color = null;

	private EmbedAlertType(String s) {
		this.niceName = s;
	}
	
	private EmbedAlertType(String s, Color c){
		this.niceName = s;
		this.color = c;
	}

	public String getDisplayName() {
		return this.niceName;
	}
	
	public Color getColor(){
		return this.color;
	}
}
