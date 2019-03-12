package me.bartvv.serverpeak.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.map.LinkedMap;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.RequiredArgsConstructor;
import me.bartvv.serverpeak.DateUtil;
import me.bartvv.serverpeak.ServerPeak;
import me.bartvv.serverpeak.object.Peak;

@RequiredArgsConstructor
public class PeakManager {

	private final ServerPeak serverPeak;
	private LinkedMap< Date, Peak > peaks;

	public void init() {
		this.peaks = new LinkedMap<>();

		File file = new File( this.serverPeak.getDataFolder(), "data.json" );
		if ( file.exists() ) {
			JsonParser jsonParser = new JsonParser();
			try {
				Object obj = jsonParser.parse( new FileReader( file ) );
				if ( !( obj instanceof JsonObject ) )
					return;

				JsonObject jsonObject = ( JsonObject ) obj;
				for ( int i = 0; i < jsonObject.size(); i++ ) {
					JsonObject json = jsonObject.get( Integer.toString( i ) ).getAsJsonObject();
					Peak peak = Peak.deserialize( json );
					this.peaks.put( peak.getDate(), peak );
				}
			} catch ( JsonSyntaxException | JsonIOException | FileNotFoundException e ) {
				e.printStackTrace();
			}
		}

		new Timer( "ServerPeak Timer" ).schedule( new TimerTask() {

			private long lastSaved = System.currentTimeMillis();

			@Override
			public void run() {
				synchronized ( PeakManager.this.peaks ) {
					Date date = DateUtil.parseDate( System.currentTimeMillis() );
					if ( !PeakManager.this.peaks.containsKey( date ) ) {
						Peak peak = Peak.builder().date( date ).peak( date ).playersInPeak( 0 ).build();
						Bukkit.getOnlinePlayers().forEach( peak::handleJoin );
						PeakManager.this.peaks.put( date, peak );
					}

					Peak peak = PeakManager.this.peaks.get( date );
					peak.getAveragePlayers().add( Bukkit.getOnlinePlayers().size() );
				}
				if ( lastSaved + TimeUnit.HOURS.toMillis( 1 ) < System.currentTimeMillis() ) {
					PeakManager.this.save();
					this.lastSaved = System.currentTimeMillis();
				}
			}
		}, 0, TimeUnit.MINUTES.toMillis( 1 ) );
	}

	public Peak getPeak() {
		synchronized ( this.peaks ) {
			return this.peaks.get( this.peaks.lastKey() );
		}
	}

	@SuppressWarnings( "unchecked" )
	public void save() {
		File file = new File( this.serverPeak.getDataFolder(), "data.json" );
		file.delete();
		if ( this.peaks.isEmpty() )
			return;

		try {
			file.createNewFile();
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		synchronized ( this.peaks ) {
			JSONObject obj = new JSONObject();

			int i = 0;
			OrderedMapIterator< Date, Peak > iterator = this.peaks.mapIterator();
			while ( iterator.hasNext() ) {
				iterator.next();
				obj.put( i, iterator.getValue().serialize() );
				i++;
			}

			try ( FileWriter fileWriter = new FileWriter( file ) ) {
				fileWriter.write( obj.toJSONString() );
				fileWriter.flush();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}

	public List< Peak > getPeaks( int lastPeak ) {
		synchronized ( this.peaks ) {
			List< Date > peak = this.peaks.asList();
			return peak.subList( peak.size() - lastPeak < 0 ? 0 : peak.size() - lastPeak, peak.size() ).stream()
					.map( this.peaks::get ).collect( Collectors.toList() );
		}
	}

}
