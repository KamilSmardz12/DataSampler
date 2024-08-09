package sk.services;

import org.springframework.stereotype.Service;
import sk.dto.Measurement;
import sk.dto.MeasurementRequest;
import sk.dto.MeasurementResponse;
import sk.dto.Response;
import sk.enums.MeasurementType;
import sk.interfaces.MeasurementProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeasurementService {

    private final MeasurementProcessor measurementProcessor;

    public MeasurementService(MeasurementProcessor measurementProcessor) {
        this.measurementProcessor = measurementProcessor;
    }

    public Response processMeasurements(MeasurementRequest measurementRequest) {
        Instant start = measurementRequest.getStartOfSampling() != null
                ? Instant.parse(measurementRequest.getStartOfSampling())
                : Instant.now();

        Map<MeasurementType, List<Measurement>> processed = measurementProcessor.process(start, measurementRequest.getMeasurements());

        Map<MeasurementType, List<MeasurementResponse>> response = processed.entrySet().stream()
                .collect(
                        Collectors.toMap
                                (
                                        Map.Entry::getKey,
                                        entry -> entry.getValue().stream()
                                                .map(measurement -> new MeasurementResponse(
                                                                measurement.getNearestFiveMinutesForward(),
                                                                measurement.getValue(),
                                                                measurement.getMeasurementType()
                                                        )
                                                )
                                                .sorted()
                                                .collect(Collectors.toList())
                                )
                );


        return new Response(response);
    }

}
