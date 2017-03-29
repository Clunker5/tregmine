package info.tregmine.discord;

import info.tregmine.Tregmine;
import info.tregmine.discord.entities.TregmineEmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DiscordUtil {

    private HashMap<Integer, Object> container = new HashMap<Integer, Object>();

    public DiscordUtil sendDestructiveMessage(MessageChannel channel, String message) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Message sent = channel.sendMessage(message).complete();
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sent.delete().complete();
                return;
            }
        });
        t.start();
        return this;
    }

    public DiscordUtil sendDestructiveMessage(MessageChannel channel, Message message) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Message sent = channel.sendMessage(message).complete();
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sent.delete().complete();
                return;
            }
        });
        t.start();
        return this;
    }

    public DiscordUtil sendDestructiveMessage(MessageChannel channel, MessageEmbed embed) {
        Thread t = new Thread(new Runnable() {
            public void run() {

                Message sent = channel.sendMessage(embed).complete();
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sent.delete().complete();
                return;
            }
        });
        t.start();
        return this;
    }

    public DiscordUtil sendDestructiveMessageWithDuration(MessageChannel channel, MessageEmbed embed, int duration) {
        Thread t = new Thread(new Runnable() {
            public void run() {

                Message sent = channel.sendMessage(embed).complete();
                try {
                    Thread.sleep(duration * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sent.delete().complete();
                return;
            }
        });
        t.start();
        return this;
    }

    public DiscordUtil sendDestructiveMessageWithDuration(MessageChannel channel, Message message, int duration) {
        Thread t = new Thread(new Runnable() {
            public void run() {

                Message sent = channel.sendMessage(message).complete();
                try {
                    Thread.sleep(duration * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sent.delete().complete();
                return;
            }
        });
        t.start();
        return this;
    }

    public void sendDestructiveMessage(MessageChannel channel, String message, int seconds) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Message sent = channel.sendMessage(message).complete();
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sent.delete().complete();
                return;
            }
        });
        t.start();
    }

    public Message sendDestructiveMessage(MessageChannel channel, Message message, int seconds) {
        RestAction<Message> sent = channel.sendMessage(message);
        final int id = ThreadLocalRandom.current().nextInt(1931, 49284 + 1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                container.put(id, sent.complete());
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ((Message) container.get(id)).delete().complete();
                return;
            }
        });
        t.start();
        return (Message) container.get(id);
    }

    public Message sendDestructiveMessage(MessageChannel channel, MessageEmbed embed, int seconds) {
        RestAction<Message> sent = channel.sendMessage(embed);
        final int id = ThreadLocalRandom.current().nextInt(49286, 69284 + 1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                container.put(id, sent.complete());
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ((Message) container.get(id)).delete().complete();
            }
        });
        t.start();
        return (Message) container.get(id);
    }

    public DiscordUtil flagDestructive(Message message) {
        if (!canManage(message))
            return this;
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                message.delete().complete();
                return;
            }
        });
        t.start();
        return this;
    }


    public DiscordUtil badNumber(Message message, int minimum, int maximum) {
        sendDestructiveMessage(message.getChannel(), TregmineEmbedBuilder.errorEmbedForUser("Bad Number", "Please enter a number between " + minimum + "and " + maximum + ".", message.getAuthor()), 10);
        return this;
    }

    public DiscordUtil badNumber(Message message, int number, boolean isMaximum) {
        String appendage = "";

        if (isMaximum) {
            appendage += "below " + number;
        } else {
            appendage += "above " + number;
        }
        sendDestructiveMessage(message.getChannel(), TregmineEmbedBuilder.errorEmbedForUser("Bad Number", "Please enter a number " + appendage + ".", message.getAuthor()), 10);
        return this;
    }

    public DiscordUtil flagDestructive(Message message, int seconds) {
        if (!canManage(message))
            return this;
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                message.delete().complete();
                return;
            }
        });
        t.start();
        return this;
    }

    public boolean canManage(Message message) {
        boolean isPrivate = message.getChannelType() == ChannelType.PRIVATE;
        boolean isSelf = message.getAuthor().getId() == DiscordSRV.selfUser.getId();
        return !isPrivate || isSelf;
    }

}
