package info.tregmine.discord.threads;

import info.tregmine.Tregmine;
import info.tregmine.discord.DiscordSRV;

import java.io.*;

public class ServerLogWatcher extends Thread {

    private Tregmine plugin;
    private DiscordSRV srv;

    public ServerLogWatcher(DiscordSRV srv) {
        this.srv = srv;
        this.plugin = this.srv.getPlugin();
    }

    private String applyRegex(String input) {
        return input.replaceAll(this.plugin.getConfig().getString("discord.console-functionality.regex.filter"),
                this.plugin.getConfig().getString("discord.console-functionality.regex.replacement"));
    }

    private Boolean lineIsOk(String input) {
        return !input.replace(" ", "").replace("\n", "").isEmpty();
    }

    @Override
    public void run() {
        int rate = this.plugin.getConfig().getInt("discord.console-functionality.logging.refresh-rate");
        String message = "";

        FileReader fr = null;
        try {
            fr = new FileReader(new File(new File(".").getAbsolutePath() + "/logs/latest.log").getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);

        Boolean done = false;
        while (!done) {
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null)
                done = true;
        }

        while (!isInterrupted()) {
            try {
                if (this.srv.getConsoleChannel() == null)
                    return;

                String line = null;
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (line == null) {
                    if (message.length() > 0) {
                        if (message.length() > 2000)
                            message = message.substring(0, 1999);
                        sendMessage(message);
                        message = "";
                    }
                    try {
                        Thread.sleep(rate);
                    } catch (InterruptedException e) {
                    }
                    continue;
                } else {
                    for (String phrase : this.plugin.getConfig()
                            .getStringList("discord.console-functionality.blacklist.do-not-send"))
                        if (line.toLowerCase().contains(phrase.toLowerCase()))
                            continue;
                    if (message.length() + line.length() + 2 <= 2000 && line.length() > 0) {
                        if (lineIsOk(applyRegex(line)))
                            message += line + "\n";
                    } else {
                        sendMessage(message);
                        if (lineIsOk(applyRegex(line)))
                            message = line + "\n";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String input) {
        input = applyRegex(input);

        if (!input.replace(" ", "").replace("\n", "").isEmpty())
            this.srv.sendMessage(this.srv.getConsoleChannel(), input);
    }

}