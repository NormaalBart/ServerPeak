package me.bartvv.serverpeak.commands;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
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
		this.date = new SimpleDateFormat( this.serverPeak.getConfig().getString( "date-format" ) );
		this.peak = new SimpleDateFormat( this.serverPeak.getConfig().getString( "date-format-peak" ) );
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
						.replace( "%peak_date%", peak.getPeak() == null ? "none" : this.peak.format( peak.getPeak() ) )
						.replace( "%peak_average_players%", peak.getAveragePlayersAsString() )
						.replace( "%peak_end%",
								peak.getPeakEnd() == null ? "none: " : this.peak.format( peak.getPeakEnd() ) )
						.replace( "%peak_end_total_time%", formatTime( peak.getPeak(), peak.getPeakEnd() ) ) + "\n" );
			}
		}

		this.serverPeak.getMessages().getStringList( "peaks" ).stream()
				.map( string -> string.replace( "%format%", formatBuilder.toString() ) )
				.forEachOrdered( sender::sendMessage );
		return true;
	}

	public String formatTime( Date begin, Date end ) {
		if ( begin == null )
			return "none";
		Duration duration = Duration.between( begin.toInstant(), end.toInstant() );
		return DurationFormatUtils.formatDuration( duration.toMillis(),
				this.serverPeak.getConfig().getString( "format-begin-end" ), true );
	}

}
