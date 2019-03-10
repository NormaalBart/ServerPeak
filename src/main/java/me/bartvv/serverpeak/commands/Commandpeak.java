package me.bartvv.serverpeak.commands;

import java.text.SimpleDateFormat;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.RequiredArgsConstructor;
import me.bartvv.serverpeak.ServerPeak;
import me.bartvv.serverpeak.object.Peak;

@RequiredArgsConstructor
public class Commandpeak implements CommandExecutor {

	private final ServerPeak serverPeak;
	private SimpleDateFormat date, peak;

	public Commandpeak init() {
		this.date = new SimpleDateFormat( this.serverPeak.getMessages().getString( "date-format" ) );
		this.peak = new SimpleDateFormat( this.serverPeak.getMessages().getString( "date-format-peak" ) );
		return this;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( !sender.hasPermission( "serverpeak.peak" ) ) {
			sender.sendMessage( this.serverPeak.getMessages().getString( "no-permission" ) );
			return true;
		}

		int lastPeak = 7;
		if ( args.length == 1 )
			try {
				lastPeak = Integer.parseInt( args[ 0 ] );
			} catch ( NumberFormatException nfe ) {
				sender.sendMessage( this.serverPeak.getMessages().getString( "no-number" ) );
				return true;
			}

		List< Peak > peaks = this.serverPeak.getPeakManager().getPeaks( lastPeak );

		StringBuilder formatBuilder = new StringBuilder();
		for ( Peak peak : peaks ) {
			for ( String format : this.serverPeak.getMessages().getStringList( "format" ) ) {
				formatBuilder.append( format.replace( "%date%", this.date.format( peak.getDate() ) )
						.replace( "%peak_total_joined%", peak.getJoinedPlayers().size() + "" )
						.replace( "%peak_players%", peak.getPlayersInPeak() + "" )
						.replace( "%peak_date%", this.peak.format( peak.getPeak() ) )
						.replace( "%peak_average_players%", peak.getAveragePlayersAsString() ) + "\n" );
			}
		}

		for ( String string : this.serverPeak.getMessages().getStringList( "peaks" ) ) {
			if ( string.contains( "%format%" ) )
				string = string.replace( "%format%", formatBuilder.toString() );

			sender.sendMessage( string );
		}
		return true;
	}

}
