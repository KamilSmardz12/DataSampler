package sk.processor;

import org.springframework.stereotype.Service;
import sk.dto.Measurement;
import sk.enums.MeasurementType;
import sk.exceptions.MeasurementHasNotBeenFound;
import sk.utils.MeasurementUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public abstract class CommonProcessor {

    protected List<Measurement> filterAndUpdateMeasurements(List<Measurement> unsampledMeasurements, Instant startOfSampling) {
        if (canProcess(unsampledMeasurements))
            return Collections.emptyList();

        List<Measurement> filteredMeasurements = unsampledMeasurements.stream()
                .filter(measurement -> measurement.getTimestamp().isAfter(startOfSampling))
                .collect(Collectors.toList());

        return MeasurementUtils.updateRoundedTimestampsForward(filteredMeasurements);
    }

    protected boolean canProcess(final List<Measurement> unsampledMeasurements) {
        return unsampledMeasurements == null || unsampledMeasurements.isEmpty();
    }

    protected Map<MeasurementType, List<Measurement>> groupAndProcessMeasurements(List<Measurement> measurements) {
        return measurements.stream()
                .collect(
                        Collectors.groupingBy
                                (
                                        Measurement::getMeasurementType,
                                        () -> new EnumMap<>(MeasurementType.class),
                                        Collectors.collectingAndThen
                                                (
                                                        Collectors.groupingBy(Measurement::getNearestFiveMinutesForward),
                                                        collectMeasurements()
                                                )
                                )
                );
    }

    private Function<Map<Instant, List<Measurement>>, List<Measurement>> collectMeasurements() {
        return measurements -> measurements.values()
                .stream()
                .map(findLastMeasurement())
                .sorted()
                .collect(Collectors.toList());
    }

    protected Function<List<Measurement>, Measurement> findLastMeasurement() {
        return m -> m.stream()
                .max(MeasurementUtils.compareByTimestamp())
                .orElseThrow(() -> new MeasurementHasNotBeenFound("Measurement not found in the given time interval"));
    }

}
