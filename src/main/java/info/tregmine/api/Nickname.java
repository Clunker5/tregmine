package info.tregmine.api;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Nickname {

    // Nickname
    private String nickname;
    private ChatColor color = ChatColor.WHITE;

    // Special nickname characteristics
    private List<ChatColor> formatting = new ArrayList<>();

    public Nickname(String nickname) {
        this.nickname = nickname;
    }

    public Nickname(String nickname, ChatColor color) {
        this(nickname);
        this.color = color;
    }

    public Nickname(String nickname, ChatColor color, List<ChatColor> formatting) {
        this(nickname, color);
        this.formatting = formatting;
    }

    public static Nickname fromSQL(String json) {
        return fromSQL(new JSONObject(json));
    }

    public static Nickname fromSQL(JSONObject json) {
        List<ChatColor> formatting = new ArrayList<>();
        for (String format : json.getString("formatting").split(" ")) {
            if (format.isEmpty())
                continue;
            formatting.add(ChatColor.valueOf(format));
        }
        return new Nickname(json.getString("nickname"), ChatColor.valueOf(json.getString("color")), formatting);
    }

    public void addFormatting(ChatColor format) {
        if (this.formatting.contains(format)) {
            return;
        }
        this.formatting.add(format);
    }

    public ChatColor getColor() {
        return this.color;
    }

    public void setColor(ChatColor setcolor) {
        this.color = setcolor;
    }

    public List<ChatColor> getFormatting() {
        return this.formatting;
    }

    public void setFormatting(List<ChatColor> format) {
        this.formatting = format;
    }

    public String getFormattingCF() {
        StringBuilder returns = new StringBuilder();
        for (Object color : this.formatting.toArray()) {
            ChatColor clr = (ChatColor) color;
            returns.append(clr);
        }
        return returns.toString();
    }

    public String getNickname() {
        return (this.color != null ? this.color : "") + this.getFormattingCF() + this.nickname;
    }

    public String getNicknamePlaintext() {
        return this.nickname;
    }

    private String formattingToJSON() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (ChatColor color : formatting) {
            builder.append((first ? "" : " ") + color.name());
            first = false;
        }
        return builder.toString();
    }

    public String toSQL() {
        return new JSONObject()
                .put("color", this.color.name())
                .put("formatting", formattingToJSON())
                .put("nickname", this.nickname)
                .toString();
    }

}
