package me.bartvv.serverpeak.object;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import lombok.Builder;
import lombok.Getter;
import me.bartvv.serverpeak.DateUtil;

@Getter
@Builder
public class Peak {

	private Date date, peak;
	@Builder.Default
	private List< UUID > joinedPlayers = Lists.newArrayList();
	@Builder.Default
	private List< Integer > averagePlayers = Lists.newArrayList();
	private int playersInPeak;

	public Map< String, Object > serialize() {
		Map< String, Object > map = Maps.newHashMap();
		map.put( "date", this.date.getTime() );
		map.put( "peak", this.peak.getTime() );
		map.put( "joinedPlayers", this.joinedPlayers.stream().map( UUID::toString ).collect( Collectors.toList() ) );
		map.put( "AveragePlayers", this.averagePlayers );
		map.put( "playersInPeak", this.playersInPeak );
		return map;
	}

	public static Peak deserialize( JsonObject jsonObject ) {
		Date date = DateUtil.parseDate( jsonObject.get( "date" ).getAsLong() );
		Date peak = new Date( jsonObject.get( "peak" ).getAsLong() );
		List< UUID > joinedPlayers = Lists.newArrayList();
		jsonObject.get( "joinedPlayers" ).getAsJsonArray()
				.forEach( jsonElement -> joinedPlayers.add( UUID.fromString( jsonElement.getAsString() ) ) );
		int playersInPeak = jsonObject.get( "playersInPeak" ).getAsInt();
		List< Integer > averagePlayers = Lists.newArrayList();
		jsonObject.get( "AveragePlayers" ).getAsJsonArray()
				.forEach( jsonElement -> averagePlayers.add( jsonElement.getAsInt() ) );
		return Peak.builder().date( date ).peak( peak ).joinedPlayers( joinedPlayers ).playersInPeak( playersInPeak )
				.averagePlayers( averagePlayers ).build();
	}

	public void handleJoin( Player player ) {
		if ( !this.joinedPlayers.contains( player.getUniqueId() ) )
			this.joinedPlayers.add( player.getUniqueId() );
		if ( Bukkit.getOnlinePlayers().size() <= this.playersInPeak )
			return;
		this.peak = new Date( System.currentTimeMillis() );
		this.playersInPeak = Bukkit.getOnlinePlayers().size();
	}

	public String getAveragePlayersAsString() {
		double total = this.averagePlayers.stream().mapToInt( i -> i ).sum();
		double size = this.averagePlayers.size();
		return this.averagePlayers.isEmpty() ? Integer.toString( Bukkit.getOnlinePlayers().size() )
				: String.format( "%.2f", total / size );
	}

}
