package info.tregmine.discord.entities;

import java.awt.*;

public enum EmbedType {
    STATUS_UPDATE("Server Status Update"), SAY("Message from the admins"), CONF_CODE(
            "Enter the following command in Minecraft:"), PING("Pong!", Color.CYAN);

    private String niceName;
    private Color color = null;

    EmbedType(String s) {
        this.niceName = s;
    }

    EmbedType(String s, Color c) {
        this.niceName = s;
        this.color = c;
    }

    public String getTitle() {
        return this.niceName;
    }

    public Color getColor() {
        return this.color;
    }
}
