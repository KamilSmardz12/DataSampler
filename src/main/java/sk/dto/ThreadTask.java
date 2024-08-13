package sk.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ThreadTask(List<Measurement> measurements) {
    public ThreadTask processMeasurements(final Function<List<Measurement>, Measurement> map) {
        List<Measurement> proceeded = this
                .measurements()
                .stream()
                .collect(Collectors.groupingBy(Measurement::getNearestFiveMinutesForward))
                .values()
                .stream()
                .map(map)
                .sorted()
                .collect(Collectors.toList());

        return new ThreadTask(proceeded);
    }
}
