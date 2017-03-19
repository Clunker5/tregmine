package info.tregmine.discord.entities;

public enum EmbedAlertType {
	STATUS_UPDATE("Server Status Update"), SAY("Message from the admins"), CONF_CODE(
			"Enter the following command in Minecraft:");

	private String niceName;

	private EmbedAlertType(String s) {
		this.niceName = s;
	}

	public String getDisplayName() {
		return this.niceName;
	}
}
