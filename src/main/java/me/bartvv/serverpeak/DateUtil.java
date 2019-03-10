package me.bartvv.serverpeak;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date parseDate( long millis ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( new Date( millis ) );
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ),
				calendar.get( Calendar.DAY_OF_MONTH ), 0, 0, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		return calendar.getTime();
	}

}
