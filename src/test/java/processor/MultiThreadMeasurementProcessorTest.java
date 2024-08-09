package processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.dto.Measurement;
import sk.enums.MeasurementType;
import sk.interfaces.MeasurementProcessor;
import sk.processor.MultiThreadMeasurementProcessor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MultiThreadMeasurementProcessorTest {

    private MeasurementProcessor measurementProcessor;

    @BeforeEach
    void init() {
        measurementProcessor = new MultiThreadMeasurementProcessor(10);
    }

    @Test
    public void testProcessMeasurements() {
        List<Measurement> measurements = Arrays.asList(
                new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:04:45Z"), new BigDecimal("35.79")),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:01:18Z"), new BigDecimal("98.78")),
                new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:09:07Z"), new BigDecimal("35.01")),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:03:34Z"), new BigDecimal("96.49")),
                new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:02:01Z"), new BigDecimal("35.82")),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:05:00Z"), new BigDecimal("97.17")),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:05:01Z"), new BigDecimal("95.08"))
        );

        Instant startOfSampling = Instant.parse("2017-01-03T10:00:00Z");

        Map<MeasurementType, List<Measurement>> result = measurementProcessor.process(startOfSampling, measurements);


        Measurement expectedTemp1 = new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:04:45Z"), Instant.parse("2017-01-03T10:05:00Z"), new BigDecimal("35.79"));
        Measurement expectedTemp2 = new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:09:07Z"), Instant.parse("2017-01-03T10:10:00Z"), new BigDecimal("35.01"));
        Measurement expectedSpo2_1 = new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:05:00Z"), Instant.parse("2017-01-03T10:05:00Z"), new BigDecimal("97.17"));
        Measurement expectedSpo2_2 = new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:05:01Z"), Instant.parse("2017-01-03T10:10:00Z"), new BigDecimal("95.08"));

        result.get(MeasurementType.TEMP).forEach(System.out::println);
        result.get(MeasurementType.SPO2).forEach(System.out::println);
        assertAll(
                () -> assertEquals(Arrays.asList(expectedTemp1, expectedTemp2), result.get(MeasurementType.TEMP)),
                () -> assertEquals(Arrays.asList(expectedSpo2_1, expectedSpo2_2), result.get(MeasurementType.SPO2))
        );
    }

    @Test
    public void testEmptyInput() {
        List<Measurement> measurements = Collections.emptyList();

        Instant startOfSampling = Instant.parse("2017-01-03T10:00:00Z");
        Map<MeasurementType, List<Measurement>> result = measurementProcessor.process(startOfSampling, measurements);

        assertTrue(result.isEmpty());
    }
}
