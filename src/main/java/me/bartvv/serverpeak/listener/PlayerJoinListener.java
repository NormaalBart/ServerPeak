package me.bartvv.serverpeak.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.RequiredArgsConstructor;
import me.bartvv.serverpeak.ServerPeak;
import me.bartvv.serverpeak.object.Peak;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

	private final ServerPeak serverPeak;

	@EventHandler
	public void on( PlayerJoinEvent e ) {
		Peak peak = this.serverPeak.getPeakManager().getPeak();
		peak.handleJoin( e.getPlayer() );
	}

}
