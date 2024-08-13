package processor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import sk.MeasurementApplication;
import sk.dto.Measurement;
import sk.dto.MeasurementRequest;
import sk.dto.Response;
import sk.enums.MeasurementType;
import sk.processor.MultiThreadMeasurementProcessor;
import sk.processor.SingleThreadMeasurementProcessor;
import sk.services.MeasurementService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MeasurementApplication.class)
public class PerformanceTest {

    public static final int MAX_THREAD_POOL_SIZE = 2;
    private static final int DATA_SET_SIZE = 40_000_000;
    private static List<Measurement> measurements;

    @BeforeAll
    static void init() {
        measurements = generateLargeDataSet();
    }

    private static List<Measurement> generateLargeDataSet() {
        List<Measurement> measurements = new ArrayList<>();
        int countOfSpo2 = 0;

        for (int i = 0; i < DATA_SET_SIZE; i++) {
            MeasurementType type = (i % 2 == 0) ? MeasurementType.TEMP : MeasurementType.SPO2;
            countOfSpo2 += MeasurementType.SPO2.equals(type) ? 1 : 0;
            Instant timestamp = Instant.now().minusSeconds(i * 60L);
            BigDecimal value = new BigDecimal("36.5").add(new BigDecimal(i % 10));
            measurements.add(new Measurement(type, timestamp, value));
        }

        System.out.println("countOfSpo2: " + countOfSpo2);

        return measurements;
    }

    @Test
    public void testSingleThreadPerformance1() {
        MeasurementService singleThreadService = new MeasurementService(new SingleThreadMeasurementProcessor());
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = singleThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Single-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testMultiThreadPerformance1() {
        MeasurementService multiThreadService = new MeasurementService(new MultiThreadMeasurementProcessor(MAX_THREAD_POOL_SIZE));
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = multiThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Multi-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testSingleThreadPerformance2() {
        MeasurementService singleThreadService = new MeasurementService(new SingleThreadMeasurementProcessor());
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = singleThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Single-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testMultiThreadPerformance2() {
        MeasurementService multiThreadService = new MeasurementService(new MultiThreadMeasurementProcessor(MAX_THREAD_POOL_SIZE));
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = multiThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Multi-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testSingleThreadPerformance3() {
        MeasurementService singleThreadService = new MeasurementService(new SingleThreadMeasurementProcessor());
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = singleThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Single-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testMultiThreadPerformance3() {
        MeasurementService multiThreadService = new MeasurementService(new MultiThreadMeasurementProcessor(MAX_THREAD_POOL_SIZE));
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = multiThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Multi-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testSingleThreadPerformance4() {
        MeasurementService singleThreadService = new MeasurementService(new SingleThreadMeasurementProcessor());
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = singleThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Single-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

    @Test
    public void testMultiThreadPerformance4() {
        MeasurementService multiThreadService = new MeasurementService(new MultiThreadMeasurementProcessor(MAX_THREAD_POOL_SIZE));
        MeasurementRequest measurementRequest = new MeasurementRequest(measurements, Instant.now().toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response result = multiThreadService.processMeasurements(measurementRequest);

        stopWatch.stop();

        System.out.println("Multi-thread processor time: " + stopWatch.getTotalTimeMillis() + " ms");

        assertThat(result).isNotNull();
    }

}
