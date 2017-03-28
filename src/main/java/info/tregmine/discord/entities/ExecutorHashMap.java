package info.tregmine.discord.entities;

import info.tregmine.discord.commands.IDiscordCommand;

import java.util.HashMap;

public class ExecutorHashMap<K, V> extends HashMap<String, IDiscordCommand> {

    private static final long serialVersionUID = -6285673644696823770L;

    public void addExecutor(IDiscordCommand command) {
        this.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            this.put(alias, command);
        }
    }

    public IDiscordCommand getExecutor(String command) {
        return this.get(command);
    }

}
