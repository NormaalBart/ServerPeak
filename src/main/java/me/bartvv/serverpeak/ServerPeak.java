package me.bartvv.serverpeak;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.bartvv.serverpeak.commands.Commandpeak;
import me.bartvv.serverpeak.listener.PlayerJoinListener;
import me.bartvv.serverpeak.listener.PlayerQuitListener;
import me.bartvv.serverpeak.manager.FileManager;
import me.bartvv.serverpeak.manager.PeakManager;

@Getter
public class ServerPeak extends JavaPlugin {

	private FileManager messages, config;
	private PeakManager peakManager;

	@Override
	public void onEnable() {
		this.messages = new FileManager( this, "messages.yml", -1, getDataFolder(), false );
		this.config = new FileManager( this, "config.yml", -1, getDataFolder(), false );
		this.peakManager = new PeakManager( this );

		this.peakManager.init();

		getServer().getPluginManager().registerEvents( new PlayerJoinListener( this ), this );
		getServer().getPluginManager().registerEvents( new PlayerQuitListener( this ), this );
		getCommand( "peak" ).setExecutor( new Commandpeak( this ).init() );
	}

	@Override
	public void onDisable() {
		this.peakManager.save();
	}

}
