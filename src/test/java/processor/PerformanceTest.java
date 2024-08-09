package processor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import sk.MeasurementApplication;
import sk.dto.Measurement;
import sk.dto.MeasurementRequest;
import sk.enums.MeasurementType;
import sk.processor.MultiThreadMeasurementProcessor;
import sk.processor.SingleThreadMeasurementProcessor;
import sk.service.MeasurementService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MeasurementApplication.class)
public class PerformanceTest {

    private static final int DATA_SET_SIZE = 60_000_000; // Liczba rekordów w danych testowych
    private static List<Measurement> measurements;

    @BeforeAll
    static void init() {
        measurements = generateLargeDataSet();
    }

    private static List<Measurement> generateLargeDataSet() {
        List<Measurement> measurements = new ArrayList<>();
        for (int i = 0; i < DATA_SET_SIZE; i++) {
            MeasurementType type = (i % 2 == 0) ? MeasurementType.TEMP : MeasurementType.SPO2;
            Instant timestamp = Instant.now().minusSeconds(i * 60L);
            BigDecimal value = new BigDecimal("36.5").add(new BigDecimal(i % 10));
            measurements.add(new Measurement(type, timestamp, value));
        }
        return measurements;
    }

    @Test
    public void testSingleThreadPerformance() {
        MeasurementService singleThreadService = new MeasurementService(new SingleThreadMeasurementProcessor());
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Map<MeasurementType, List<Measurement>> result = singleThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Single-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testMultiThreadPerformance() {
        MeasurementService multiThreadService = new MeasurementService(new MultiThreadMeasurementProcessor(10));
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Map<MeasurementType, List<Measurement>> result = multiThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Multi-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }
}
