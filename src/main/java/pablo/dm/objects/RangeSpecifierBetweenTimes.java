package pablo.dm.objects;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class RangeSpecifierBetweenTimes extends RangeSpecifierBase {
	public final String type="BETWEEN_TIMES";
	public int durationInMinutes;
	public String startTime;
	public String endTime;
	
	public RangeSpecifierBetweenTimes(ZonedDateTime start, ZonedDateTime end){
		DateTimeFormatter format = DateTimeFormatter.ISO_INSTANT;
		startTime=format.format(start.withZoneSameInstant(ZoneOffset.UTC));
		endTime=format.format(end.withZoneSameInstant(ZoneOffset.UTC));
		Duration p = Duration.between(start.toLocalDateTime(), end.toLocalDateTime());
		durationInMinutes=Math.abs(Math.round(p.getSeconds()/60));		
	}
}
