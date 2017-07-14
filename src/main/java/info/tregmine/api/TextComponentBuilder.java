package info.tregmine.api;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

/**
 * Created by eric on 7/13/17.
 */
public class TextComponentBuilder {
    private TextComponent textComponent;

    public TextComponentBuilder(TextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public TextComponentBuilder(String text) {
        this.textComponent = new TextComponent(text);
    }

    public TextComponentBuilder(String text, ChatColor color) {
        this(text);
        this.textComponent.setColor(color);
    }

    public TextComponent build() {
        return this.textComponent;
    }

    public String getText() {
        return this.textComponent.getText();
    }

    public TextComponentBuilder setText(String text) {
        this.textComponent.setText(text);
        return this;
    }

    public ChatColor getColor() {
        return this.textComponent.getColor();
    }

    public TextComponentBuilder setColor(ChatColor color) {
        this.textComponent.setColor(color);
        return this;
    }

    public Boolean getBold() {
        return this.textComponent.isBold();
    }

    public TextComponentBuilder setBold(Boolean bold) {
        this.textComponent.setBold(bold);
        return this;
    }

    public Boolean getItalic() {
        return this.textComponent.isItalic();
    }

    public TextComponentBuilder setItalic(Boolean italic) {
        this.textComponent.setItalic(italic);
        return this;
    }

    public Boolean getUnderlined() {
        return this.textComponent.isUnderlined();
    }

    public TextComponentBuilder setUnderlined(Boolean underlined) {
        this.textComponent.setUnderlined(underlined);
        return this;
    }

    public Boolean getStrikethrough() {
        return this.textComponent.isStrikethrough();
    }

    public TextComponentBuilder setStrikethrough(Boolean strikethrough) {
        this.textComponent.setStrikethrough(strikethrough);
        return this;
    }

    public Boolean getObfuscated() {
        return this.textComponent.isObfuscated();
    }

    public TextComponentBuilder setObfuscated(Boolean obfuscated) {
        this.textComponent.setObfuscated(obfuscated);
        return this;
    }

    public String getInsertion() {
        return this.textComponent.getInsertion();
    }

    public TextComponentBuilder setInsertion(String insertion) {
        this.textComponent.setInsertion(insertion);
        return this;
    }

    public List<BaseComponent> getExtra() {
        return this.textComponent.getExtra();
    }

    public TextComponentBuilder setExtra(List<BaseComponent> extra) {
        this.textComponent.setExtra(extra);
        return this;
    }

    public ClickEvent getClickEvent() {
        return this.textComponent.getClickEvent();
    }

    public TextComponentBuilder setClickEvent(ClickEvent clickEvent) {
        this.textComponent.setClickEvent(clickEvent);
        return this;
    }

    public HoverEvent getHoverEvent() {
        return this.textComponent.getHoverEvent();
    }

    public TextComponentBuilder setHoverEvent(HoverEvent hoverEvent) {
        this.textComponent.setHoverEvent(hoverEvent);
        return this;
    }
}
