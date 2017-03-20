package info.tregmine.discord;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class DiscordUtil {
	
	public static void sendDestructiveMessage(MessageChannel channel, String message){
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
	}
	
	public static void sendDestructiveMessage(MessageChannel channel, MessageEmbed embed){
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
	}
	
	public static void sendDestructiveMessage(MessageChannel channel, String message, int seconds){
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
	
	public static void sendDestructiveMessage(MessageChannel channel, MessageEmbed embed, int seconds){
		Thread t = new Thread(new Runnable() {
		    public void run() {
				Message sent = channel.sendMessage(embed).complete();
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
	
	public static void flagDestructive(Message message){
		Thread t = new Thread(new Runnable() {
			public void run(){
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				message.delete().complete();
				return;
			}
		});
		t.start();
	}

	public static void flagDestructive(Message message, int seconds){
		Thread t = new Thread(new Runnable() {
			public void run(){
				try{
					Thread.sleep(seconds * 1000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				message.delete().complete();
				return;
			}
		});
		t.start();
	}

}
