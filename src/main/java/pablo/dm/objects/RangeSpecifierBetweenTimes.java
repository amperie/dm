package pablo.dm.objects;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class RangeSpecifierBetweenTimes extends RangeSpecifierBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6913632253188372864L;
	public String startTime;
	public String endTime;
	
	public RangeSpecifierBetweenTimes(ZonedDateTime start, ZonedDateTime end){
		type="BETWEEN_TIMES";
		DateTimeFormatter format = DateTimeFormatter.ISO_INSTANT;
		startTime=format.format(start.withZoneSameInstant(ZoneOffset.UTC));
		endTime=format.format(end.withZoneSameInstant(ZoneOffset.UTC));
		Duration p = Duration.between(start.toLocalDateTime(), end.toLocalDateTime());
		durationInMinutes=Math.abs(Math.round(p.getSeconds()/60));		
	}
}
