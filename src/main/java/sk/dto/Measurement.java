package sk.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import sk.enums.MeasurementType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;

@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Measurement implements Comparable<Measurement> {
    MeasurementType measurementType;
    Instant timestamp;
    Instant nearestFiveMinutesForward;
    BigDecimal value;

    public Measurement() {
        measurementType = null;
        timestamp = null;
        nearestFiveMinutesForward = null;
        value = null;
    }

    public Measurement(MeasurementType measurementType, Instant timestamp, BigDecimal value) {
        this.measurementType = measurementType;
        this.timestamp = timestamp;
        this.nearestFiveMinutesForward = null;
        this.value = value;
    }

    @Override
    public int compareTo(Measurement other) {
        return Comparator.comparing
                        (
                                Measurement::getNearestFiveMinutesForward,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                .compare(this, other);
    }


}
