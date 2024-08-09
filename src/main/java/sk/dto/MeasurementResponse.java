package sk.dto;

import sk.enums.MeasurementType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;

public record MeasurementResponse
        (
                Instant nearestFiveMinutesForward,
                BigDecimal value,
                MeasurementType measurementType
        ) implements Comparable<MeasurementResponse> {

    @Override
    public int compareTo(MeasurementResponse other) {
        return Comparator.comparing
                        (
                                MeasurementResponse::nearestFiveMinutesForward,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                .compare(this, other);
    }


}
