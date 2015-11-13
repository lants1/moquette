package org.eclipse.moquette.fce.common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class FceTimeUtils {
	
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
