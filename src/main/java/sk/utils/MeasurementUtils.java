package sk.utils;

import sk.dto.Measurement;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MeasurementUtils {
    public static Comparator<Measurement> compareByTimestamp() {
        return Comparator.comparing(Measurement::getTimestamp);
    }

    public static  List<Measurement> updateRoundedTimestampsForward(Collection<Measurement> measurements) {
        return measurements
                .stream()
                .map(measurement -> new Measurement(
                        measurement.getMeasurementType(),
                        measurement.getTimestamp(),
                        InstantUtils.roundToNearestFiveMinutesForward(measurement.getTimestamp()),
                        measurement.getValue()
                ))
                .collect(Collectors.toList());
    }

}
