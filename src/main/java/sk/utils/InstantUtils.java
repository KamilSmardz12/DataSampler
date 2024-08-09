package sk.utils;

import java.time.Instant;

public class InstantUtils {
    private static final int SECONDS_IN_ONE_MIN = 60;
    private static final int FIVE_MIN = 5;

    public static Instant roundToNearestFiveMinutesBack(Instant timestamp) {
        long epochMinutes = timestamp.getEpochSecond() / SECONDS_IN_ONE_MIN;
        long roundedMinutesBack = (epochMinutes / FIVE_MIN) * FIVE_MIN;
        return Instant.ofEpochSecond(roundedMinutesBack * SECONDS_IN_ONE_MIN);
    }

    public static Instant roundToNearestFiveMinutesForward(Instant timestamp) {
        long epochSeconds = timestamp.getEpochSecond();
        long remainder = epochSeconds % (FIVE_MIN * SECONDS_IN_ONE_MIN);

        if (remainder == 0) {
            return timestamp;
        }

        long roundedEpochSeconds = epochSeconds + (FIVE_MIN * SECONDS_IN_ONE_MIN) - remainder;
        return Instant.ofEpochSecond(roundedEpochSeconds);
    }
}
