package io.moquette.fce.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Utility class for time functions in fce.
 * 
 * @author lants1
 *
 */
public final class FceTimeUtil {
	
	private FceTimeUtil() {
	}
	
	public static long delayTo(int targetMin, int targetSec) {
		LocalDateTime localNow = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
		ZonedDateTime zonedNextTarget = zonedNow.withMinute(targetMin).withSecond(targetSec);
		if (zonedNow.compareTo(zonedNextTarget) > 0) {
			zonedNextTarget = zonedNextTarget.plusDays(1);
		}

		Duration duration = Duration.between(zonedNow, zonedNextTarget);
		return duration.getSeconds();
	}
}
