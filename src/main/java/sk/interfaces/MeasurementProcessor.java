package sk.interfaces;

import sk.dto.Measurement;
import sk.enums.MeasurementType;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface MeasurementProcessor {
    Map<MeasurementType, List<Measurement>> process(List<Measurement> unsampledMeasurements);

    Map<MeasurementType, List<Measurement>> process(Instant startOfSampling, List<Measurement> unsampledMeasurements);

}
