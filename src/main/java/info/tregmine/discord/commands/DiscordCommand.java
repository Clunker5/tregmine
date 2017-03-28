package info.tregmine.discord.commands;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordSRV;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class DiscordCommand implements IDiscordCommand {

    @SuppressWarnings("unused")
    protected Tregmine plugin;
    protected DiscordSRV discord;
    private String name;
    private String syntax;
    private String description;

    public DiscordCommand(Tregmine tregmine, String command, String syntax, String description) {
        this.name = command;
        this.plugin = tregmine;
        this.discord = this.plugin.getDiscordSRV();
        this.syntax = syntax;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean handleExecution(Message message, String arguments) {
        return true;
    }

    @Override
    public List<String> rolesPermitted() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    public void invalidSyntax(Message fromMessage) {
        fromMessage.getChannel().sendMessage(TregmineEmbedBuilder.errorEmbedForUser("Invalid Syntax!", "You have an error in your syntax.\nSyntax: ```" + this.syntax + "```", fromMessage.getAuthor())).complete();
    }

    public String getSyntax() {
        return this.syntax;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

}
