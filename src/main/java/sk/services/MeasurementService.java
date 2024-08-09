package sk.service;

import org.springframework.stereotype.Service;
import sk.dto.Measurement;
import sk.dto.MeasurementRequest;
import sk.enums.MeasurementType;
import sk.interfaces.MeasurementProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class MeasurementService {

    private final MeasurementProcessor measurementProcessor;

    public MeasurementService(MeasurementProcessor measurementProcessor) {
        this.measurementProcessor = measurementProcessor;
    }

    public Map<MeasurementType, List<Measurement>> processMeasurements( MeasurementRequest measurementRequest) {
        Instant start = measurementRequest.getStartOfSampling() != null
                ? Instant.parse(measurementRequest.getStartOfSampling())
                : Instant.now();

        return measurementProcessor.process(start, measurementRequest.getMeasurements());
    }
}
