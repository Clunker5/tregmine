package info.tregmine.discord.threads;

import java.io.File;

import info.tregmine.discord.DiscordSRV;

public class ServerLogWatcherHelper extends Thread {

	private DiscordSRV srv;

	public ServerLogWatcherHelper(DiscordSRV discord) {
		this.srv = discord;
	}

	@Override
	public void run() {
		double currentSize = new File(new File(new File(".").getAbsolutePath() + "/logs/latest.log").getAbsolutePath())
				.getTotalSpace();

		while (!interrupted()) {
			File logFile = new File(new File(new File(".").getAbsolutePath() + "/logs/latest.log").getAbsolutePath());

			if (logFile.getTotalSpace() < currentSize)
				this.srv.startServerLogWatcher();
			else
				currentSize = logFile.getTotalSpace();

			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
