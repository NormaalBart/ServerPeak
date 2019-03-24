package me.bartvv.serverpeak.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.RequiredArgsConstructor;
import me.bartvv.serverpeak.ServerPeak;

@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

	private final ServerPeak serverPeak;

	@EventHandler
	public void on( PlayerQuitEvent e ) {
		this.serverPeak.getPeakManager().getPeak().handleQuit( e.getPlayer() );
	}

}
